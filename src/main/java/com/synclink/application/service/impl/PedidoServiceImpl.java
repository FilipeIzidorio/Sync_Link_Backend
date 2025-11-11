package com.synclink.application.service.impl;

import com.synclink.application.dto.*;
import com.synclink.application.mapper.PedidoMapper;
import com.synclink.application.service.PedidoService;
import com.synclink.application.service.WebSocketService;
import com.synclink.model.*;
import com.synclink.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final MesaRepository mesaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final PagamentoRepository pagamentoRepository;
    private final PedidoMapper pedidoMapper;
    private final WebSocketService webSocketService;

    @Override
    @Transactional(readOnly = true)
    public List<PedidoDTO> findAll() {
        try {
            return pedidoMapper.toDtoList(pedidoRepository.findAll());
        } catch (Exception e) {
            log.error("Erro ao buscar todos os pedidos", e);
            throw new RuntimeException("Erro ao buscar pedidos: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoDTO findById(Long id) {
        try {
            Pedido pedido = pedidoRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Pedido não encontrado com ID: " + id));
            return pedidoMapper.toDto(pedido);
        } catch (NoSuchElementException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao buscar pedido por ID: {}", id, e);
            throw new RuntimeException("Erro ao buscar pedido: " + e.getMessage());
        }
    }

    @Override
    public PedidoDTO create(CreatePedidoDTO createPedidoDTO, Long usuarioId) {
        try {
            Mesa mesa = mesaRepository.findById(createPedidoDTO.getMesaId())
                    .orElseThrow(() -> new NoSuchElementException("Mesa não encontrada com ID: " + createPedidoDTO.getMesaId()));

            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado com ID: " + usuarioId));

            // Verificar se mesa já tem pedido ativo
            if (mesa.temPedidoAtivo()) {
                throw new IllegalStateException("Mesa " + mesa.getNumero() + " já possui um pedido ativo");
            }

            // Verificar se mesa está disponível
            if (mesa.getStatus() != StatusMesa.LIVRE) {
                throw new IllegalStateException("Mesa " + mesa.getNumero() + " não está disponível. Status: " + mesa.getStatus());
            }

            Pedido pedido = new Pedido(mesa, usuario, createPedidoDTO.getObservacao());
            pedido.setDataCriacao(LocalDateTime.now());
            pedido = pedidoRepository.save(pedido);

            // Atualizar status da mesa
            mesa.setStatus(StatusMesa.OCUPADA);
            mesaRepository.save(mesa);

            PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);

            // Notificar via WebSocket
            webSocketService.notificarPedidoCriado(pedidoDTO, mesa.getId());
            webSocketService.notificarMesaAtualizada(mesaRepository.findById(mesa.getId()).get());

            log.info("Pedido criado com sucesso: ID {}, Mesa {}", pedido.getId(), mesa.getNumero());
            return pedidoDTO;

        } catch (NoSuchElementException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao criar pedido para mesa: {}", createPedidoDTO.getMesaId(), e);
            throw new RuntimeException("Erro ao criar pedido: " + e.getMessage());
        }
    }

    @Override
    public PedidoDTO update(Long id, PedidoDTO pedidoDTO) {
        try {
            Pedido pedido = pedidoRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Pedido não encontrado com ID: " + id));

            // Validar se pedido pode ser editado
            if (pedido.getStatus() == StatusPedido.FECHADO || pedido.getStatus() == StatusPedido.CANCELADO) {
                throw new IllegalStateException("Não é possível editar um pedido " + pedido.getStatus().toString().toLowerCase());
            }

            pedidoMapper.updateEntityFromDto(pedidoDTO, pedido);
            pedido.setDataAtualizacao(LocalDateTime.now());
            pedido = pedidoRepository.save(pedido);

            PedidoDTO resultado = pedidoMapper.toDto(pedido);
            webSocketService.notificarPedidoAtualizado(resultado, pedido.getMesa().getId());

            return resultado;

        } catch (NoSuchElementException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao atualizar pedido: {}", id, e);
            throw new RuntimeException("Erro ao atualizar pedido: " + e.getMessage());
        }
    }

    @Override
    public void delete(Long id) {
        try {
            Pedido pedido = pedidoRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Pedido não encontrado com ID: " + id));

            // Validar se pedido pode ser excluído
            if (pedido.getStatus() != StatusPedido.ABERTO) {
                throw new IllegalStateException("Só é possível excluir pedidos com status ABERTO");
            }

            // Liberar mesa antes de excluir
            Mesa mesa = pedido.getMesa();
            mesa.setStatus(StatusMesa.LIVRE);
            mesaRepository.save(mesa);

            pedidoRepository.delete(pedido);

            webSocketService.notificarMesaAtualizada(mesa);
            log.info("Pedido excluído com sucesso: ID {}", id);

        } catch (NoSuchElementException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao excluir pedido: {}", id, e);
            throw new RuntimeException("Erro ao excluir pedido: " + e.getMessage());
        }
    }

    @Override
    public PedidoDTO adicionarItem(Long pedidoId, AdicionarItemPedidoDTO itemDTO) {
        try {
            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new NoSuchElementException("Pedido não encontrado com ID: " + pedidoId));

            // Validar status do pedido
            if (pedido.getStatus() == StatusPedido.FECHADO || pedido.getStatus() == StatusPedido.CANCELADO) {
                throw new IllegalStateException("Não é possível adicionar itens a um pedido " +
                        pedido.getStatus().toString().toLowerCase());
            }

            Produto produto = produtoRepository.findById(itemDTO.getProdutoId())
                    .orElseThrow(() -> new NoSuchElementException("Produto não encontrado com ID: " + itemDTO.getProdutoId()));

            // Verificar se produto está ativo
            if (!produto.getAtivo()) {
                throw new IllegalStateException("Produto " + produto.getNome() + " não está ativo");
            }

            // Verificar estoque (se necessário)
            // TODO: Implementar verificação de estoque

            ItemPedido item = new ItemPedido(pedido, produto, itemDTO.getQuantidade(), itemDTO.getObservacao());
            pedido.adicionarItem(item);
            pedido.setDataAtualizacao(LocalDateTime.now());

            pedido = pedidoRepository.save(pedido);

            PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);

            // Notificar cozinha se for um item que precisa de preparo
            if (deveNotificarCozinha(produto)) {
                webSocketService.notificarCozinha(pedidoDTO);
            }

            webSocketService.notificarItemAdicionado(item, pedidoId, pedido.getMesa().getId());
            log.info("Item adicionado ao pedido {}: {} x {}", pedidoId, itemDTO.getQuantidade(), produto.getNome());

            return pedidoDTO;

        } catch (NoSuchElementException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao adicionar item ao pedido: {}", pedidoId, e);
            throw new RuntimeException("Erro ao adicionar item: " + e.getMessage());
        }
    }

    @Override
    public PedidoDTO removerItem(Long pedidoId, Long itemId) {
        try {
            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new NoSuchElementException("Pedido não encontrado com ID: " + pedidoId));

            ItemPedido item = itemPedidoRepository.findById(itemId)
                    .orElseThrow(() -> new NoSuchElementException("Item não encontrado com ID: " + itemId));

            if (!item.getPedido().getId().equals(pedidoId)) {
                throw new IllegalArgumentException("Item não pertence ao pedido");
            }

            // Validar status do pedido
            if (pedido.getStatus() == StatusPedido.FECHADO || pedido.getStatus() == StatusPedido.CANCELADO) {
                throw new IllegalStateException("Não é possível remover itens de um pedido " +
                        pedido.getStatus().toString().toLowerCase());
            }

            pedido.removerItem(item);
            pedido.setDataAtualizacao(LocalDateTime.now());

            itemPedidoRepository.delete(item);
            pedido = pedidoRepository.save(pedido);

            PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);
            webSocketService.notificarPedidoAtualizado(pedidoDTO, pedido.getMesa().getId());

            log.info("Item removido do pedido {}: Item ID {}", pedidoId, itemId);
            return pedidoDTO;

        } catch (NoSuchElementException | IllegalStateException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao remover item do pedido: {}", pedidoId, e);
            throw new RuntimeException("Erro ao remover item: " + e.getMessage());
        }
    }

    @Override
    public PedidoDTO atualizarQuantidadeItem(Long pedidoId, Long itemId, Integer novaQuantidade) {
        try {
            if (novaQuantidade <= 0) {
                throw new IllegalArgumentException("Quantidade deve ser maior que zero");
            }

            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new NoSuchElementException("Pedido não encontrado com ID: " + pedidoId));

            ItemPedido item = itemPedidoRepository.findById(itemId)
                    .orElseThrow(() -> new NoSuchElementException("Item não encontrado com ID: " + itemId));

            if (!item.getPedido().getId().equals(pedidoId)) {
                throw new IllegalArgumentException("Item não pertence ao pedido");
            }

            // Validar status do pedido
            if (pedido.getStatus() == StatusPedido.FECHADO || pedido.getStatus() == StatusPedido.CANCELADO) {
                throw new IllegalStateException("Não é possível alterar itens de um pedido " +
                        pedido.getStatus().toString().toLowerCase());
            }

            item.setQuantidade(novaQuantidade);
            pedido.setDataAtualizacao(LocalDateTime.now());

            itemPedidoRepository.save(item);
            pedido.calcularTotais();
            pedido = pedidoRepository.save(pedido);

            PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);
            webSocketService.notificarPedidoAtualizado(pedidoDTO, pedido.getMesa().getId());

            log.info("Quantidade atualizada no pedido {}: Item ID {}, Nova quantidade: {}",
                    pedidoId, itemId, novaQuantidade);
            return pedidoDTO;

        } catch (NoSuchElementException | IllegalStateException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao atualizar quantidade do item: {}", itemId, e);
            throw new RuntimeException("Erro ao atualizar quantidade: " + e.getMessage());
        }
    }

    @Override
    public PedidoDTO atualizarStatus(Long pedidoId, StatusPedido status) {
        try {
            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new NoSuchElementException("Pedido não encontrado com ID: " + pedidoId));

            // Validações de transição de status
            validarTransicaoStatus(pedido.getStatus(), status);

            pedido.setStatus(status);
            pedido.setDataAtualizacao(LocalDateTime.now());

            // Ações específicas por status
            switch (status) {
                case FECHADO:
                    pedido.setDataFechamento(LocalDateTime.now());
                    break;
                case CANCELADO:
                    // Liberar mesa se pedido for cancelado
                    Mesa mesa = pedido.getMesa();
                    mesa.setStatus(StatusMesa.LIVRE);
                    mesaRepository.save(mesa);
                    break;
            }

            pedido = pedidoRepository.save(pedido);
            PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);

            // Notificar mudança de status
            webSocketService.notificarPedidoAtualizado(pedidoDTO, pedido.getMesa().getId());

            if (status == StatusPedido.EM_PREPARO || status == StatusPedido.PRONTO) {
                webSocketService.notificarCozinha(pedidoDTO);
            }

            log.info("Status do pedido {} atualizado para: {}", pedidoId, status);
            return pedidoDTO;

        } catch (NoSuchElementException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao atualizar status do pedido: {}", pedidoId, e);
            throw new RuntimeException("Erro ao atualizar status: " + e.getMessage());
        }
    }

    @Override
    public PedidoDTO moverParaPreparo(Long pedidoId) {
        return atualizarStatus(pedidoId, StatusPedido.EM_PREPARO);
    }

    @Override
    public PedidoDTO marcarComoPronto(Long pedidoId) {
        return atualizarStatus(pedidoId, StatusPedido.PRONTO);
    }

    @Override
    public PedidoDTO marcarComoEntregue(Long pedidoId) {
        return atualizarStatus(pedidoId, StatusPedido.ENTREGUE);
    }

    @Override
    public PedidoDTO cancelarPedido(Long pedidoId, String motivoCancelamento) {
        try {
            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new NoSuchElementException("Pedido não encontrado com ID: " + pedidoId));

            if (pedido.getStatus() == StatusPedido.FECHADO) {
                throw new IllegalStateException("Não é possível cancelar um pedido fechado");
            }

            pedido.setStatus(StatusPedido.CANCELADO);
            pedido.setObservacao((pedido.getObservacao() != null ? pedido.getObservacao() + " | " : "") +
                    "CANCELADO: " + motivoCancelamento);
            pedido.setDataAtualizacao(LocalDateTime.now());

            // Liberar mesa
            Mesa mesa = pedido.getMesa();
            mesa.setStatus(StatusMesa.LIVRE);
            mesaRepository.save(mesa);

            pedido = pedidoRepository.save(pedido);
            PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);

            webSocketService.notificarPedidoAtualizado(pedidoDTO, pedido.getMesa().getId());
            webSocketService.notificarMesaAtualizada(mesa);

            log.info("Pedido {} cancelado. Motivo: {}", pedidoId, motivoCancelamento);
            return pedidoDTO;

        } catch (NoSuchElementException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao cancelar pedido: {}", pedidoId, e);
            throw new RuntimeException("Erro ao cancelar pedido: " + e.getMessage());
        }
    }

    @Override
    public PedidoDTO aplicarAcrescimo(Long pedidoId, BigDecimal valor, String justificativa) {
        try {
            if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Valor do acréscimo deve ser maior que zero");
            }

            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new NoSuchElementException("Pedido não encontrado com ID: " + pedidoId));

            if (pedido.getStatus() == StatusPedido.FECHADO || pedido.getStatus() == StatusPedido.CANCELADO) {
                throw new IllegalStateException("Não é possível aplicar acréscimo em um pedido " +
                        pedido.getStatus().toString().toLowerCase());
            }

            pedido.aplicarAcrescimo(valor, justificativa);
            pedido.setDataAtualizacao(LocalDateTime.now());
            pedido = pedidoRepository.save(pedido);

            PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);
            webSocketService.notificarPedidoAtualizado(pedidoDTO, pedido.getMesa().getId());

            log.info("Acréscimo aplicado ao pedido {}: R$ {}, Justificativa: {}",
                    pedidoId, valor, justificativa);
            return pedidoDTO;

        } catch (NoSuchElementException | IllegalStateException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao aplicar acréscimo ao pedido: {}", pedidoId, e);
            throw new RuntimeException("Erro ao aplicar acréscimo: " + e.getMessage());
        }
    }

    @Override
    public PedidoDTO aplicarDesconto(Long pedidoId, BigDecimal valor, String justificativa) {
        try {
            if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Valor do desconto deve ser maior que zero");
            }

            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new NoSuchElementException("Pedido não encontrado com ID: " + pedidoId));

            if (pedido.getStatus() == StatusPedido.FECHADO || pedido.getStatus() == StatusPedido.CANCELADO) {
                throw new IllegalStateException("Não é possível aplicar desconto em um pedido " +
                        pedido.getStatus().toString().toLowerCase());
            }

            // Validar se desconto não é maior que o subtotal
            if (valor.compareTo(pedido.getSubtotal()) > 0) {
                throw new IllegalArgumentException("Desconto não pode ser maior que o subtotal do pedido");
            }

            pedido.aplicarDesconto(valor, justificativa);
            pedido.setDataAtualizacao(LocalDateTime.now());
            pedido = pedidoRepository.save(pedido);

            PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);
            webSocketService.notificarPedidoAtualizado(pedidoDTO, pedido.getMesa().getId());

            log.info("Desconto aplicado ao pedido {}: R$ {}, Justificativa: {}",
                    pedidoId, valor, justificativa);
            return pedidoDTO;

        } catch (NoSuchElementException | IllegalStateException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao aplicar desconto ao pedido: {}", pedidoId, e);
            throw new RuntimeException("Erro ao aplicar desconto: " + e.getMessage());
        }
    }

    @Override
    public PedidoDTO fecharPedido(Long pedidoId, FecharPedidoDTO fecharPedidoDTO) {
        try {
            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new NoSuchElementException("Pedido não encontrado com ID: " + pedidoId));

            if (pedido.getStatus() == StatusPedido.FECHADO) {
                throw new IllegalStateException("Pedido já está fechado");
            }

            if (pedido.getStatus() == StatusPedido.CANCELADO) {
                throw new IllegalStateException("Não é possível fechar um pedido cancelado");
            }

            // Aplicar acréscimo e desconto se fornecidos
            if (fecharPedidoDTO.getAcrescimo() != null && fecharPedidoDTO.getAcrescimo().compareTo(BigDecimal.ZERO) > 0) {
                pedido.aplicarAcrescimo(fecharPedidoDTO.getAcrescimo(), fecharPedidoDTO.getJustificativaAcrescimo());
            }

            if (fecharPedidoDTO.getDesconto() != null && fecharPedidoDTO.getDesconto().compareTo(BigDecimal.ZERO) > 0) {
                // Validar se desconto não é maior que o subtotal + acréscimo
                BigDecimal maxDesconto = pedido.getSubtotal().add(pedido.getAcrescimo());
                if (fecharPedidoDTO.getDesconto().compareTo(maxDesconto) > 0) {
                    throw new IllegalArgumentException("Desconto não pode ser maior que o total do pedido");
                }
                pedido.aplicarDesconto(fecharPedidoDTO.getDesconto(), fecharPedidoDTO.getJustificativaDesconto());
            }

            pedido.fecharPedido();
            pedido = pedidoRepository.save(pedido);

            // Liberar mesa
            Mesa mesa = pedido.getMesa();
            mesa.setStatus(StatusMesa.LIVRE);
            mesaRepository.save(mesa);

            PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);

            webSocketService.notificarPedidoFechado(pedidoDTO, pedido.getMesa().getId());
            webSocketService.notificarMesaAtualizada(mesa);

            log.info("Pedido {} fechado. Total: R$ {}", pedidoId, pedido.getValorFinal());
            return pedidoDTO;

        } catch (NoSuchElementException | IllegalStateException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao fechar pedido: {}", pedidoId, e);
            throw new RuntimeException("Erro ao fechar pedido: " + e.getMessage());
        }
    }

    // ⭐⭐ NOVO MÉTODO PARA FINALIZAÇÃO DE VENDA ⭐⭐
    @Override
    @Transactional
    public PedidoFinalizadoDTO finalizarVenda(Long pedidoId, FinalizarVendaDTO finalizarVendaDTO) {
        try {
            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new NoSuchElementException("Pedido não encontrado com ID: " + pedidoId));

            // Validar se pedido pode ser finalizado
            if (pedido.getStatus() == StatusPedido.FECHADO) {
                throw new IllegalStateException("Pedido já está fechado");
            }

            if (pedido.getStatus() == StatusPedido.CANCELADO) {
                throw new IllegalStateException("Não é possível finalizar um pedido cancelado");
            }

            // Validar valor pago
            if (finalizarVendaDTO.getValorPago().compareTo(pedido.getValorFinal()) < 0) {
                throw new IllegalArgumentException("Valor pago é insuficiente para cobrir o total do pedido");
            }

            // Fechar pedido primeiro
            FecharPedidoDTO fecharPedidoDTO = new FecharPedidoDTO();
            fecharPedido(pedidoId, fecharPedidoDTO);

            // Criar registro de pagamento
            Pagamento pagamento = new Pagamento();
            pagamento.setPedido(pedido);
            pagamento.setFormaPagamento(finalizarVendaDTO.getFormaPagamento());
            pagamento.setValor(pedido.getValorFinal());
            pagamento.setStatus(StatusPagamento.APROVADO);
            pagamento.setDataConfirmacao(LocalDateTime.now());

            // Gerar código de transação
            pagamento.setCodigoTransacao("TXN" + System.currentTimeMillis() + pedidoId);

            pagamentoRepository.save(pagamento);

            // Calcular troco
            BigDecimal troco = calcularTroco(pedidoId, finalizarVendaDTO.getValorPago());

            // Construir resposta
            PedidoFinalizadoDTO resultado = PedidoFinalizadoDTO.builder()
                    .pedidoId(pedido.getId())
                    .mesaId(pedido.getMesa().getId())
                    .mesaNumero(pedido.getMesa().getNumero())
                    .total(pedido.getValorFinal())
                    .valorPago(finalizarVendaDTO.getValorPago())
                    .troco(troco)
                    .formaPagamento(finalizarVendaDTO.getFormaPagamento())
                    .codigoTransacao(pagamento.getCodigoTransacao())
                    .dataFechamento(pedido.getDataFechamento())
                    .nomeCliente(finalizarVendaDTO.getNomeCliente())
                    .documentoCliente(finalizarVendaDTO.getDocumentoCliente())
                    .build();

            // Notificar sistema de caixa
            webSocketService.notificarPagamentoProcessado(pagamento, pedidoId);

            log.info("Venda finalizada com sucesso - Pedido: {}, Forma Pagamento: {}, Valor: R$ {}, Troco: R$ {}",
                    pedidoId, finalizarVendaDTO.getFormaPagamento(), pedido.getValorFinal(), troco);

            return resultado;

        } catch (NoSuchElementException | IllegalStateException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao finalizar venda do pedido: {}", pedidoId, e);
            throw new RuntimeException("Erro ao finalizar venda: " + e.getMessage());
        }
    }

    @Override
    public BigDecimal calcularTroco(Long pedidoId, BigDecimal valorPago) {
        try {
            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new NoSuchElementException("Pedido não encontrado com ID: " + pedidoId));

            if (valorPago.compareTo(pedido.getValorFinal()) < 0) {
                throw new IllegalArgumentException("Valor pago é inferior ao total do pedido");
            }

            return valorPago.subtract(pedido.getValorFinal());

        } catch (NoSuchElementException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao calcular troco para pedido: {}", pedidoId, e);
            throw new RuntimeException("Erro ao calcular troco: " + e.getMessage());
        }
    }

    @Override
    public PedidoDTO reabrirPedido(Long pedidoId, String motivo) {
        try {
            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new NoSuchElementException("Pedido não encontrado com ID: " + pedidoId));

            if (pedido.getStatus() != StatusPedido.FECHADO) {
                throw new IllegalStateException("Só é possível reabrir pedidos fechados");
            }

            // Verificar se mesa está disponível
            Mesa mesa = pedido.getMesa();
            if (mesa.getStatus() != StatusMesa.LIVRE) {
                throw new IllegalStateException("Mesa não está disponível para reabrir o pedido");
            }

            pedido.setStatus(StatusPedido.ABERTO);
            pedido.setDataFechamento(null);
            pedido.setDataAtualizacao(LocalDateTime.now());
            pedido.setObservacao((pedido.getObservacao() != null ? pedido.getObservacao() + " | " : "") +
                    "REABERTO: " + motivo);

            // Ocupar mesa novamente
            mesa.setStatus(StatusMesa.OCUPADA);
            mesaRepository.save(mesa);

            pedido = pedidoRepository.save(pedido);
            PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);

            webSocketService.notificarPedidoAtualizado(pedidoDTO, pedido.getMesa().getId());
            webSocketService.notificarMesaAtualizada(mesa);

            log.info("Pedido {} reaberto. Motivo: {}", pedidoId, motivo);
            return pedidoDTO;

        } catch (NoSuchElementException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao reabrir pedido: {}", pedidoId, e);
            throw new RuntimeException("Erro ao reabrir pedido: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoDTO> findByStatus(StatusPedido status) {
        try {
            return pedidoMapper.toDtoList(pedidoRepository.findByStatus(status));
        } catch (Exception e) {
            log.error("Erro ao buscar pedidos por status: {}", status, e);
            throw new RuntimeException("Erro ao buscar pedidos: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoDTO> findByMesaId(Long mesaId) {
        try {
            return pedidoMapper.toDtoList(pedidoRepository.findByMesaId(mesaId));
        } catch (Exception e) {
            log.error("Erro ao buscar pedidos por mesa: {}", mesaId, e);
            throw new RuntimeException("Erro ao buscar pedidos: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoDTO> findPedidosAtivos() {
        try {
            return pedidoMapper.toDtoList(pedidoRepository.findPedidosAtivos());
        } catch (Exception e) {
            log.error("Erro ao buscar pedidos ativos", e);
            throw new RuntimeException("Erro ao buscar pedidos ativos: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoDTO> findPedidosCozinha() {
        try {
            List<StatusPedido> statusCozinha = Arrays.asList(
                    StatusPedido.EM_PREPARO,
                    StatusPedido.PRONTO
            );
            return pedidoMapper.toDtoList(pedidoRepository.findByStatusIn(statusCozinha));
        } catch (Exception e) {
            log.error("Erro ao buscar pedidos para cozinha", e);
            throw new RuntimeException("Erro ao buscar pedidos da cozinha: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoDTO> findPedidosPorPeriodo(String dataInicio, String dataFim) {
        try {
            LocalDateTime inicio = LocalDate.parse(dataInicio).atStartOfDay();
            LocalDateTime fim = LocalDate.parse(dataFim).atTime(LocalTime.MAX);
            return pedidoMapper.toDtoList(pedidoRepository.findByPeriodo(inicio, fim));
        } catch (Exception e) {
            log.error("Erro ao buscar pedidos por período: {} a {}", dataInicio, dataFim, e);
            throw new RuntimeException("Erro ao buscar pedidos: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResumidoDTO> findResumoVendasDiarias(String data) {
        try {
            LocalDateTime dataConsulta = LocalDate.parse(data).atStartOfDay();
            LocalDateTime fimDoDia = dataConsulta.toLocalDate().atTime(LocalTime.MAX);

            List<Pedido> pedidos = pedidoRepository.findByPeriodo(dataConsulta, fimDoDia);

            return pedidos.stream()
                    .map(pedido -> PedidoResumidoDTO.builder()
                            .id(pedido.getId())
                            .mesaNumero(pedido.getMesa().getNumero())
                            .status(pedido.getStatus())
                            .total(pedido.getValorFinal())
                            .dataCriacao(pedido.getDataCriacao())
                            .usuarioNome(pedido.getUsuario().getNome())
                            .build())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Erro ao buscar resumo de vendas para data: {}", data, e);
            throw new RuntimeException("Erro ao buscar resumo de vendas: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public EstatisticasPedidosDTO obterEstatisticas(String dataInicio, String dataFim) {
        try {
            LocalDateTime inicio = LocalDate.parse(dataInicio).atStartOfDay();
            LocalDateTime fim = LocalDate.parse(dataFim).atTime(LocalTime.MAX);

            List<Pedido> pedidos = pedidoRepository.findByPeriodo(inicio, fim);

            // Calcular estatísticas
            long totalPedidos = pedidos.size();
            BigDecimal totalVendas = pedidos.stream()
                    .map(Pedido::getValorFinal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal mediaPorPedido = totalPedidos > 0 ?
                    totalVendas.divide(BigDecimal.valueOf(totalPedidos), 2, RoundingMode.HALF_UP) :
                    BigDecimal.ZERO;

            long pedidosAbertos = pedidos.stream()
                    .filter(p -> p.getStatus() == StatusPedido.ABERTO)
                    .count();

            long pedidosCozinha = pedidos.stream()
                    .filter(p -> p.getStatus() == StatusPedido.EM_PREPARO || p.getStatus() == StatusPedido.PRONTO)
                    .count();

            long pedidosEntregues = pedidos.stream()
                    .filter(p -> p.getStatus() == StatusPedido.ENTREGUE)
                    .count();

            // Agrupar por status
            Map<StatusPedido, Long> pedidosPorStatus = pedidos.stream()
                    .collect(Collectors.groupingBy(Pedido::getStatus, Collectors.counting()));

            // TODO: Implementar vendas por forma de pagamento quando tiver os dados de pagamento

            return EstatisticasPedidosDTO.builder()
                    .totalPedidos(totalPedidos)
                    .totalVendas(totalVendas)
                    .mediaPorPedido(mediaPorPedido)
                    .pedidosAbertos(pedidosAbertos)
                    .pedidosCozinha(pedidosCozinha)
                    .pedidosEntregues(pedidosEntregues)
                    .pedidosPorStatus(pedidosPorStatus)
                    .vendasPorFormaPagamento(new HashMap<>()) // Placeholder
                    .build();

        } catch (Exception e) {
            log.error("Erro ao obter estatísticas para período: {} a {}", dataInicio, dataFim, e);
            throw new RuntimeException("Erro ao obter estatísticas: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularTotalVendasPeriodo(String dataInicio, String dataFim) {
        try {
            LocalDateTime inicio = LocalDate.parse(dataInicio).atStartOfDay();
            LocalDateTime fim = LocalDate.parse(dataFim).atTime(LocalTime.MAX);

            List<Pedido> pedidos = pedidoRepository.findByPeriodo(inicio, fim);

            return pedidos.stream()
                    .filter(p -> p.getStatus() == StatusPedido.FECHADO)
                    .map(Pedido::getValorFinal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

        } catch (Exception e) {
            log.error("Erro ao calcular total de vendas para período: {} a {}", dataInicio, dataFim, e);
            throw new RuntimeException("Erro ao calcular total de vendas: " + e.getMessage());
        }
    }

    // ============================================================
    // MÉTODOS PRIVADOS AUXILIARES
    // ============================================================

    private void validarTransicaoStatus(StatusPedido statusAtual, StatusPedido novoStatus) {
        // Regras de transição de status
        switch (statusAtual) {
            case CANCELADO:
                throw new IllegalStateException("Não é possível alterar status de um pedido cancelado");
            case FECHADO:
                throw new IllegalStateException("Não é possível alterar status de um pedido fechado");
            case ENTREGUE:
                if (novoStatus != StatusPedido.FECHADO) {
                    throw new IllegalStateException("Pedido entregue só pode ser fechado");
                }
                break;
            // Outras validações podem ser adicionadas conforme necessário
        }
    }

    private boolean deveNotificarCozinha(Produto produto) {
        // Lógica para determinar se o produto precisa de preparo na cozinha
        // Pode ser baseada na categoria do produto ou em um campo específico
        return !produto.getCategoria().getNome().toLowerCase().contains("bebida") &&
                !produto.getCategoria().getNome().toLowerCase().contains("sobremesa");
    }
}