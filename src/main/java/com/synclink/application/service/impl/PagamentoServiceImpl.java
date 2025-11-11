package com.synclink.application.service.impl;

import com.synclink.application.dto.PagamentoDTO;
import com.synclink.application.mapper.PagamentoMapper;
import com.synclink.application.service.PagamentoService;
import com.synclink.application.service.WebSocketService;
import com.synclink.model.*;
import com.synclink.domain.repository.PagamentoRepository;
import com.synclink.domain.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PagamentoServiceImpl implements PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final PedidoRepository pedidoRepository;
    private final PagamentoMapper pagamentoMapper;
    private final WebSocketService webSocketService;

    @Override
    @Transactional(readOnly = true)
    public List<PagamentoDTO> findAll() {
        try {
            return pagamentoMapper.toDtoList(pagamentoRepository.findAll());
        } catch (Exception e) {
            log.error("Erro ao buscar todos os pagamentos", e);
            throw new RuntimeException("Erro ao buscar pagamentos: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PagamentoDTO findById(Long id) {
        try {
            Pagamento pagamento = pagamentoRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Pagamento não encontrado com ID: " + id));
            return pagamentoMapper.toDto(pagamento);
        } catch (NoSuchElementException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao buscar pagamento por ID: {}", id, e);
            throw new RuntimeException("Erro ao buscar pagamento: " + e.getMessage());
        }
    }

    @Override
    public PagamentoDTO create(PagamentoDTO pagamentoDTO) {
        try {
            // Validar dados do pagamento
            validarPagamento(pagamentoDTO);

            Pagamento pagamento = pagamentoMapper.toEntity(pagamentoDTO);

            // Gerar código de transação único
            if (pagamento.getCodigoTransacao() == null) {
                pagamento.setCodigoTransacao(gerarCodigoTransacao());
            }

            pagamento.setStatus(StatusPagamento.PENDENTE);
            pagamento.setDataCriacao(LocalDateTime.now());

            pagamento = pagamentoRepository.save(pagamento);
            return pagamentoMapper.toDto(pagamento);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao criar pagamento", e);
            throw new RuntimeException("Erro ao criar pagamento: " + e.getMessage());
        }
    }

    @Override
    public PagamentoDTO update(Long id, PagamentoDTO pagamentoDTO) {
        try {
            Pagamento pagamento = pagamentoRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Pagamento não encontrado com ID: " + id));

            // Validar se pagamento pode ser editado
            if (pagamento.getStatus() != StatusPagamento.PENDENTE) {
                throw new IllegalStateException("Não é possível editar um pagamento com status: " + pagamento.getStatus());
            }

            pagamentoMapper.updateEntityFromDto(pagamentoDTO, pagamento);
            pagamento = pagamentoRepository.save(pagamento);

            return pagamentoMapper.toDto(pagamento);

        } catch (NoSuchElementException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao atualizar pagamento: {}", id, e);
            throw new RuntimeException("Erro ao atualizar pagamento: " + e.getMessage());
        }
    }

    @Override
    public void delete(Long id) {
        try {
            Pagamento pagamento = pagamentoRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Pagamento não encontrado com ID: " + id));

            // Validar se pagamento pode ser excluído
            if (pagamento.getStatus() != StatusPagamento.PENDENTE) {
                throw new IllegalStateException("Não é possível excluir um pagamento com status: " + pagamento.getStatus());
            }

            pagamentoRepository.delete(pagamento);

            log.info("Pagamento excluído com sucesso: ID {}", id);

        } catch (NoSuchElementException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao excluir pagamento: {}", id, e);
            throw new RuntimeException("Erro ao excluir pagamento: " + e.getMessage());
        }
    }

    @Override
    public PagamentoDTO processarPagamento(Long pedidoId, FormaPagamento formaPagamento, BigDecimal valor) {
        try {
            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new NoSuchElementException("Pedido não encontrado com ID: " + pedidoId));

            // Validar se pedido pode receber pagamento
            if (pedido.getStatus() != StatusPedido.FECHADO) {
                throw new IllegalStateException("Só é possível processar pagamento para pedidos fechados");
            }

            // Validar valor do pagamento
            if (valor.compareTo(pedido.getValorFinal()) != 0) {
                throw new IllegalArgumentException(
                        String.format("Valor do pagamento (R$ %.2f) não corresponde ao total do pedido (R$ %.2f)",
                                valor, pedido.getValorFinal())
                );
            }

            Pagamento pagamento = new Pagamento();
            pagamento.setPedido(pedido);
            pagamento.setFormaPagamento(formaPagamento);
            pagamento.setValor(valor);
            pagamento.setStatus(StatusPagamento.APROVADO);
            pagamento.setCodigoTransacao(gerarCodigoTransacao());
            pagamento.setDataCriacao(LocalDateTime.now());
            pagamento.setDataConfirmacao(LocalDateTime.now());

            pagamento = pagamentoRepository.save(pagamento);

            PagamentoDTO pagamentoDTO = pagamentoMapper.toDto(pagamento);

            // Notificar via WebSocket
            webSocketService.notificarPagamentoProcessado(pagamentoDTO, pedidoId);

            log.info("Pagamento processado com sucesso: Pedido {}, Valor R$ {}, Forma: {}",
                    pedidoId, valor, formaPagamento);

            return pagamentoDTO;

        } catch (NoSuchElementException | IllegalStateException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao processar pagamento para pedido: {}", pedidoId, e);
            throw new RuntimeException("Erro ao processar pagamento: " + e.getMessage());
        }
    }

    @Override
    public PagamentoDTO processarPagamentoCompleto(Long pedidoId, PagamentoDTO pagamentoDTO) {
        try {
            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new NoSuchElementException("Pedido não encontrado com ID: " + pedidoId));

            // Validar se pedido pode receber pagamento
            if (pedido.getStatus() != StatusPedido.FECHADO) {
                throw new IllegalStateException("Só é possível processar pagamento para pedidos fechados");
            }

            // Validar valor do pagamento
            if (pagamentoDTO.getValor().compareTo(pedido.getValorFinal()) != 0) {
                throw new IllegalArgumentException(
                        String.format("Valor do pagamento (R$ %.2f) não corresponde ao total do pedido (R$ %.2f)",
                                pagamentoDTO.getValor(), pedido.getValorFinal())
                );
            }

            Pagamento pagamento = pagamentoMapper.toEntity(pagamentoDTO);
            pagamento.setPedido(pedido);

            if (pagamento.getCodigoTransacao() == null) {
                pagamento.setCodigoTransacao(gerarCodigoTransacao());
            }

            pagamento.setStatus(StatusPagamento.APROVADO);
            pagamento.setDataCriacao(LocalDateTime.now());
            pagamento.setDataConfirmacao(LocalDateTime.now());

            pagamento = pagamentoRepository.save(pagamento);

            PagamentoDTO resultado = pagamentoMapper.toDto(pagamento);

            // Notificar via WebSocket
            webSocketService.notificarPagamentoProcessado(resultado, pedidoId);

            log.info("Pagamento completo processado: Pedido {}, Valor R$ {}, Forma: {}",
                    pedidoId, pagamentoDTO.getValor(), pagamentoDTO.getFormaPagamento());

            return resultado;

        } catch (NoSuchElementException | IllegalStateException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao processar pagamento completo para pedido: {}", pedidoId, e);
            throw new RuntimeException("Erro ao processar pagamento: " + e.getMessage());
        }
    }

    @Override
    public PagamentoDTO estornarPagamento(Long pagamentoId) {
        try {
            Pagamento pagamento = pagamentoRepository.findById(pagamentoId)
                    .orElseThrow(() -> new NoSuchElementException("Pagamento não encontrado com ID: " + pagamentoId));

            // Validar se pagamento pode ser estornado
            if (pagamento.getStatus() != StatusPagamento.APROVADO) {
                throw new IllegalStateException("Só é possível estornar pagamentos aprovados");
            }

            // Verificar se não se passou muito tempo (ex: 30 dias)
            LocalDateTime limiteEstorno = LocalDateTime.now().minusDays(30);
            if (pagamento.getDataConfirmacao().isBefore(limiteEstorno)) {
                throw new IllegalStateException("Não é possível estornar pagamentos com mais de 30 dias");
            }

            pagamento.setStatus(StatusPagamento.ESTORNADO);
            pagamento = pagamentoRepository.save(pagamento);

            PagamentoDTO pagamentoDTO = pagamentoMapper.toDto(pagamento);

            // Notificar estorno
            webSocketService.enviarParaTodos("/topic/pagamentos",
                    new com.synclink.application.dto.WebSocketMessageDTO(
                            "PAGAMENTO_ESTORNADO",
                            pagamentoDTO,
                            null
                    )
            );

            log.info("Pagamento estornado: ID {}, Pedido {}", pagamentoId, pagamento.getPedido().getId());
            return pagamentoDTO;

        } catch (NoSuchElementException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao estornar pagamento: {}", pagamentoId, e);
            throw new RuntimeException("Erro ao estornar pagamento: " + e.getMessage());
        }
    }

    @Override
    public PagamentoDTO confirmarPagamento(Long pagamentoId) {
        try {
            Pagamento pagamento = pagamentoRepository.findById(pagamentoId)
                    .orElseThrow(() -> new NoSuchElementException("Pagamento não encontrado com ID: " + pagamentoId));

            if (pagamento.getStatus() != StatusPagamento.PENDENTE) {
                throw new IllegalStateException("Só é possível confirmar pagamentos pendentes");
            }

            pagamento.setStatus(StatusPagamento.APROVADO);
            pagamento.setDataConfirmacao(LocalDateTime.now());
            pagamento = pagamentoRepository.save(pagamento);

            PagamentoDTO pagamentoDTO = pagamentoMapper.toDto(pagamento);

            // Notificar confirmação
            webSocketService.notificarPagamentoProcessado(pagamentoDTO, pagamento.getPedido().getId());

            log.info("Pagamento confirmado: ID {}, Pedido {}", pagamentoId, pagamento.getPedido().getId());
            return pagamentoDTO;

        } catch (NoSuchElementException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao confirmar pagamento: {}", pagamentoId, e);
            throw new RuntimeException("Erro ao confirmar pagamento: " + e.getMessage());
        }
    }

    @Override
    public PagamentoDTO recusarPagamento(Long pagamentoId, String motivo) {
        try {
            Pagamento pagamento = pagamentoRepository.findById(pagamentoId)
                    .orElseThrow(() -> new NoSuchElementException("Pagamento não encontrado com ID: " + pagamentoId));

            if (pagamento.getStatus() != StatusPagamento.PENDENTE) {
                throw new IllegalStateException("Só é possível recusar pagamentos pendentes");
            }

            pagamento.setStatus(StatusPagamento.RECUSADO);
            pagamento.setObservacao((pagamento.getObservacao() != null ? pagamento.getObservacao() + " | " : "") +
                    "Motivo da recusa: " + motivo);
            pagamento = pagamentoRepository.save(pagamento);

            PagamentoDTO pagamentoDTO = pagamentoMapper.toDto(pagamento);

            // Notificar recusa
            webSocketService.enviarParaTodos("/topic/pagamentos",
                    new com.synclink.application.dto.WebSocketMessageDTO(
                            "PAGAMENTO_RECUSADO",
                            pagamentoDTO,
                            null
                    )
            );

            log.info("Pagamento recusado: ID {}, Motivo: {}", pagamentoId, motivo);
            return pagamentoDTO;

        } catch (NoSuchElementException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao recusar pagamento: {}", pagamentoId, e);
            throw new RuntimeException("Erro ao recusar pagamento: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagamentoDTO> findByPedidoId(Long pedidoId) {
        try {
            return pagamentoMapper.toDtoList(pagamentoRepository.findByPedidoId(pedidoId));
        } catch (Exception e) {
            log.error("Erro ao buscar pagamentos por pedido: {}", pedidoId, e);
            throw new RuntimeException("Erro ao buscar pagamentos: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagamentoDTO> findByStatus(StatusPagamento status) {
        try {
            return pagamentoMapper.toDtoList(pagamentoRepository.findByStatus(status));
        } catch (Exception e) {
            log.error("Erro ao buscar pagamentos por status: {}", status, e);
            throw new RuntimeException("Erro ao buscar pagamentos: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagamentoDTO> findByFormaPagamento(FormaPagamento formaPagamento) {
        try {
            // Implementação temporária - você precisará criar este método no repository
            List<Pagamento> todosPagamentos = pagamentoRepository.findAll();
            return pagamentoMapper.toDtoList(
                    todosPagamentos.stream()
                            .filter(p -> p.getFormaPagamento() == formaPagamento)
                            .toList()
            );
        } catch (Exception e) {
            log.error("Erro ao buscar pagamentos por forma de pagamento: {}", formaPagamento, e);
            throw new RuntimeException("Erro ao buscar pagamentos: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagamentoDTO> findPagamentosPorPeriodo(String dataInicio, String dataFim) {
        try {
            LocalDateTime inicio = LocalDate.parse(dataInicio).atStartOfDay();
            LocalDateTime fim = LocalDate.parse(dataFim).atTime(LocalTime.MAX);

            // Implementação temporária - você precisará criar este método no repository
            List<Pagamento> todosPagamentos = pagamentoRepository.findAll();
            return pagamentoMapper.toDtoList(
                    todosPagamentos.stream()
                            .filter(p -> !p.getDataCriacao().isBefore(inicio) && !p.getDataCriacao().isAfter(fim))
                            .toList()
            );
        } catch (Exception e) {
            log.error("Erro ao buscar pagamentos por período: {} a {}", dataInicio, dataFim, e);
            throw new RuntimeException("Erro ao buscar pagamentos: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularTotalPagamentosPeriodo(String dataInicio, String dataFim) {
        try {
            List<PagamentoDTO> pagamentos = findPagamentosPorPeriodo(dataInicio, dataFim);

            return pagamentos.stream()
                    .filter(p -> p.getStatus() == StatusPagamento.APROVADO)
                    .map(PagamentoDTO::getValor)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

        } catch (Exception e) {
            log.error("Erro ao calcular total de pagamentos para período: {} a {}", dataInicio, dataFim, e);
            throw new RuntimeException("Erro ao calcular total de pagamentos: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagamentoDTO> findPagamentosDia(String data) {
        try {
            return findPagamentosPorPeriodo(data, data);
        } catch (Exception e) {
            log.error("Erro ao buscar pagamentos do dia: {}", data, e);
            throw new RuntimeException("Erro ao buscar pagamentos do dia: " + e.getMessage());
        }
    }

    // ============================================================
    // MÉTODOS PRIVADOS AUXILIARES
    // ============================================================

    private void validarPagamento(PagamentoDTO pagamentoDTO) {
        if (pagamentoDTO.getPedidoId() == null) {
            throw new IllegalArgumentException("ID do pedido é obrigatório");
        }

        if (pagamentoDTO.getFormaPagamento() == null) {
            throw new IllegalArgumentException("Forma de pagamento é obrigatória");
        }

        if (pagamentoDTO.getValor() == null || pagamentoDTO.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor do pagamento deve ser maior que zero");
        }

        // Verificar se pedido existe
        if (!pedidoRepository.existsById(pagamentoDTO.getPedidoId())) {
            throw new IllegalArgumentException("Pedido não encontrado com ID: " + pagamentoDTO.getPedidoId());
        }
    }

    private String gerarCodigoTransacao() {
        return "TXN" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}