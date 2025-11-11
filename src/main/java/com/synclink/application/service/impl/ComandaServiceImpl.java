package com.synclink.application.service.impl;

import com.synclink.application.dto.ComandaDTO;
import com.synclink.application.mapper.ComandaMapper;
import com.synclink.application.service.ComandaService;
import com.synclink.application.service.WebSocketService;
import com.synclink.model.*;
import com.synclink.domain.repository.ComandaRepository;
import com.synclink.domain.repository.MesaRepository;
import com.synclink.domain.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ComandaServiceImpl implements ComandaService {

    private final ComandaRepository comandaRepository;
    private final MesaRepository mesaRepository;
    private final PedidoRepository pedidoRepository;
    private final ComandaMapper comandaMapper;
    private final WebSocketService webSocketService;

    @Override
    public ComandaDTO abrirComanda(Long mesaId) {
        try {
            Mesa mesa = mesaRepository.findById(mesaId)
                    .orElseThrow(() -> new NoSuchElementException("Mesa não encontrada com ID: " + mesaId));

            // Verificar se mesa está disponível
            if (mesa.getStatus() != StatusMesa.LIVRE) {
                throw new IllegalStateException("Mesa não está disponível para abrir comanda. Status: " + mesa.getStatus());
            }

            // Verificar se já existe comanda aberta para a mesa
            List<Comanda> comandasAbertas = comandaRepository.findByMesaIdAndStatus(mesaId, StatusComanda.ABERTA);
            if (!comandasAbertas.isEmpty()) {
                throw new IllegalStateException("Já existe uma comanda aberta para esta mesa");
            }

            // Gerar código único para a comanda
            String codigoComanda = gerarCodigoComanda();

            Comanda comanda = new Comanda();
            comanda.setMesa(mesa);
            comanda.setCodigo(codigoComanda);
            comanda.setStatus(StatusComanda.ABERTA);
            comanda.setDataAbertura(LocalDateTime.now());

            comanda = comandaRepository.save(comanda);

            // Atualizar status da mesa
            mesa.setStatus(StatusMesa.OCUPADA);
            mesaRepository.save(mesa);

            ComandaDTO comandaDTO = comandaMapper.toDto(comanda);

            // Notificar via WebSocket
            webSocketService.notificarComandaAberta(comandaDTO, mesaId);

            log.info("Comanda aberta com sucesso: Código {}, Mesa {}", codigoComanda, mesa.getNumero());
            return comandaDTO;

        } catch (NoSuchElementException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao abrir comanda para mesa: {}", mesaId, e);
            throw new RuntimeException("Erro ao abrir comanda: " + e.getMessage());
        }
    }

    @Override
    public ComandaDTO fecharComanda(Long comandaId) {
        try {
            Comanda comanda = comandaRepository.findById(comandaId)
                    .orElseThrow(() -> new NoSuchElementException("Comanda não encontrada com ID: " + comandaId));

            if (comanda.getStatus() != StatusComanda.ABERTA) {
                throw new IllegalStateException("Comanda não está aberta. Status: " + comanda.getStatus());
            }

            // Verificar se existem pedidos em aberto na comanda
            boolean hasPedidosAbertos = comanda.getPedidos().stream()
                    .anyMatch(pedido -> pedido.getStatus() != StatusPedido.FECHADO &&
                            pedido.getStatus() != StatusPedido.CANCELADO);

            if (hasPedidosAbertos) {
                throw new IllegalStateException("Não é possível fechar comanda com pedidos em aberto");
            }

            comanda.fecharComanda();
            comanda = comandaRepository.save(comanda);

            // Liberar mesa se não houver outras comandas abertas
            Mesa mesa = comanda.getMesa();
            List<Comanda> comandasAbertas = comandaRepository.findByMesaIdAndStatus(mesa.getId(), StatusComanda.ABERTA);
            if (comandasAbertas.isEmpty()) {
                mesa.setStatus(StatusMesa.LIVRE);
                mesaRepository.save(mesa);
            }

            ComandaDTO comandaDTO = comandaMapper.toDto(comanda);

            // Notificar via WebSocket
            webSocketService.notificarMesaAtualizada(mesa);

            log.info("Comanda fechada com sucesso: ID {}, Código {}", comandaId, comanda.getCodigo());
            return comandaDTO;

        } catch (NoSuchElementException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao fechar comanda: {}", comandaId, e);
            throw new RuntimeException("Erro ao fechar comanda: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ComandaDTO findById(Long id) {
        try {
            Comanda comanda = comandaRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Comanda não encontrada com ID: " + id));
            return comandaMapper.toDto(comanda);
        } catch (NoSuchElementException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao buscar comanda por ID: {}", id, e);
            throw new RuntimeException("Erro ao buscar comanda: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ComandaDTO findByCodigo(String codigo) {
        try {
            Comanda comanda = comandaRepository.findByCodigo(codigo)
                    .orElseThrow(() -> new NoSuchElementException("Comanda não encontrada com código: " + codigo));
            return comandaMapper.toDto(comanda);
        } catch (NoSuchElementException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao buscar comanda por código: {}", codigo, e);
            throw new RuntimeException("Erro ao buscar comanda: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComandaDTO> findByMesaId(Long mesaId) {
        try {
            return comandaMapper.toDtoList(comandaRepository.findByMesaId(mesaId));
        } catch (Exception e) {
            log.error("Erro ao buscar comandas por mesa: {}", mesaId, e);
            throw new RuntimeException("Erro ao buscar comandas: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComandaDTO> findComandasAbertas() {
        try {
            return comandaMapper.toDtoList(comandaRepository.findByStatus(StatusComanda.ABERTA));
        } catch (Exception e) {
            log.error("Erro ao buscar comandas abertas", e);
            throw new RuntimeException("Erro ao buscar comandas abertas: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComandaDTO> findByStatus(String status) {
        try {
            StatusComanda statusComanda = StatusComanda.valueOf(status.toUpperCase());
            return comandaMapper.toDtoList(comandaRepository.findByStatus(statusComanda));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status inválido: " + status);
        } catch (Exception e) {
            log.error("Erro ao buscar comandas por status: {}", status, e);
            throw new RuntimeException("Erro ao buscar comandas: " + e.getMessage());
        }
    }

    @Override
    public ComandaDTO adicionarPedido(Long comandaId, Long pedidoId) {
        try {
            Comanda comanda = comandaRepository.findById(comandaId)
                    .orElseThrow(() -> new NoSuchElementException("Comanda não encontrada com ID: " + comandaId));

            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new NoSuchElementException("Pedido não encontrado com ID: " + pedidoId));

            if (comanda.getStatus() != StatusComanda.ABERTA) {
                throw new IllegalStateException("Não é possível adicionar pedido a uma comanda " + comanda.getStatus().toString().toLowerCase());
            }

            // Verificar se pedido já está associado a outra comanda
            if (pedido.getComanda() != null && !pedido.getComanda().getId().equals(comandaId)) {
                throw new IllegalStateException("Pedido já está associado a outra comanda");
            }

            pedido.setComanda(comanda);
            pedidoRepository.save(pedido);

            ComandaDTO comandaDTO = comandaMapper.toDto(comanda);

            // Notificar via WebSocket
            webSocketService.notificarPedidoAtualizado(pedido, comanda.getMesa().getId());

            log.info("Pedido {} adicionado à comanda {}", pedidoId, comandaId);
            return comandaDTO;

        } catch (NoSuchElementException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao adicionar pedido à comanda: Comanda {}, Pedido {}", comandaId, pedidoId, e);
            throw new RuntimeException("Erro ao adicionar pedido à comanda: " + e.getMessage());
        }
    }

    @Override
    public ComandaDTO removerPedido(Long comandaId, Long pedidoId) {
        try {
            Comanda comanda = comandaRepository.findById(comandaId)
                    .orElseThrow(() -> new NoSuchElementException("Comanda não encontrada com ID: " + comandaId));

            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new NoSuchElementException("Pedido não encontrado com ID: " + pedidoId));

            if (!pedido.getComanda().getId().equals(comandaId)) {
                throw new IllegalStateException("Pedido não pertence a esta comanda");
            }

            pedido.setComanda(null);
            pedidoRepository.save(pedido);

            ComandaDTO comandaDTO = comandaMapper.toDto(comanda);

            // Notificar via WebSocket
            webSocketService.notificarPedidoAtualizado(pedido, comanda.getMesa().getId());

            log.info("Pedido {} removido da comanda {}", pedidoId, comandaId);
            return comandaDTO;

        } catch (NoSuchElementException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao remover pedido da comanda: Comanda {}, Pedido {}", comandaId, pedidoId, e);
            throw new RuntimeException("Erro ao remover pedido da comanda: " + e.getMessage());
        }
    }

    @Override
    public ComandaDTO cancelarComanda(Long comandaId, String motivo) {
        try {
            Comanda comanda = comandaRepository.findById(comandaId)
                    .orElseThrow(() -> new NoSuchElementException("Comanda não encontrada com ID: " + comandaId));

            if (comanda.getStatus() != StatusComanda.ABERTA) {
                throw new IllegalStateException("Só é possível cancelar comandas abertas");
            }

            comanda.setStatus(StatusComanda.CANCELADA);
            comanda.setDataFechamento(LocalDateTime.now());
            comanda = comandaRepository.save(comanda);

            // Liberar mesa
            Mesa mesa = comanda.getMesa();
            mesa.setStatus(StatusMesa.LIVRE);
            mesaRepository.save(mesa);

            // Cancelar todos os pedidos da comanda
            comanda.getPedidos().forEach(pedido -> {
                if (pedido.getStatus() != StatusPedido.FECHADO && pedido.getStatus() != StatusPedido.CANCELADO) {
                    pedido.setStatus(StatusPedido.CANCELADO);
                    pedido.setObservacao((pedido.getObservacao() != null ? pedido.getObservacao() + " | " : "") +
                            "Cancelado junto com a comanda: " + motivo);
                    pedidoRepository.save(pedido);
                }
            });

            ComandaDTO comandaDTO = comandaMapper.toDto(comanda);

            // Notificar via WebSocket
            webSocketService.notificarMesaAtualizada(mesa);

            log.info("Comanda cancelada: ID {}, Motivo: {}", comandaId, motivo);
            return comandaDTO;

        } catch (NoSuchElementException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao cancelar comanda: {}", comandaId, e);
            throw new RuntimeException("Erro ao cancelar comanda: " + e.getMessage());
        }
    }

    // ============================================================
    // MÉTODOS PRIVADOS AUXILIARES
    // ============================================================

    private String gerarCodigoComanda() {
        String codigo;
        boolean codigoUnico = false;

        // Gerar código único
        do {
            codigo = "CMD" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            codigoUnico = !comandaRepository.findByCodigo(codigo).isPresent();
        } while (!codigoUnico);

        return codigo;
    }
}