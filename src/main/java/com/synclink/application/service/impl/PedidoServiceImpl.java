package com.synclink.application.service.impl;

import com.synclink.application.dto.*;
import com.synclink.application.mapper.PedidoMapper;
import com.synclink.application.service.PedidoService;
import com.synclink.domain.repository.*;
import com.synclink.model.*;
import com.synclink.model.enums.StatusPedido;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
    private final PedidoMapper pedidoMapper;

    // ==============================
    // CRUD BÁSICO
    // ==============================
    @Override
    @Transactional(readOnly = true)
    public List<PedidoDTO> findAll() {
        return pedidoMapper.toDtoList(pedidoRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoDTO findById(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com ID: " + id));
        return pedidoMapper.toDto(pedido);
    }

    @Override
    public PedidoDTO create(CreatePedidoDTO dto, Long usuarioId) {
        Mesa mesa = mesaRepository.findById(dto.getMesaId())
                .orElseThrow(() -> new EntityNotFoundException("Mesa não encontrada com ID: " + dto.getMesaId()));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + usuarioId));

        Pedido pedido = Pedido.builder()
                .mesa(mesa)
                .usuario(usuario)
                .observacao(dto.getObservacao())
                .status(StatusPedido.ABERTO)
                .build();

        Pedido saved = pedidoRepository.save(pedido);
        log.info("✅ Pedido criado com ID {} para mesa {}", saved.getId(), mesa.getNumero());
        return pedidoMapper.toDto(saved);
    }

    @Override
    public PedidoDTO update(Long id, PedidoDTO dto) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com ID: " + id));

        pedidoMapper.updateEntityFromDto(dto, pedido);
        pedido.setDataAtualizacao(LocalDateTime.now());
        Pedido updated = pedidoRepository.save(pedido);

        log.info("🔁 Pedido ID {} atualizado com sucesso", id);
        return pedidoMapper.toDto(updated);
    }

    @Override
    public void delete(Long id) {
        if (!pedidoRepository.existsById(id)) {
            throw new EntityNotFoundException("Pedido não encontrado com ID: " + id);
        }
        pedidoRepository.deleteById(id);
        log.info("🗑️ Pedido ID {} excluído com sucesso", id);
    }

    // ==============================
    // GESTÃO DE ITENS
    // ==============================
    @Override
    public PedidoDTO adicionarItem(Long pedidoId, AdicionarItemPedidoDTO itemDTO) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com ID: " + pedidoId));

        Produto produto = produtoRepository.findById(itemDTO.getProdutoId())
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com ID: " + itemDTO.getProdutoId()));

        ItemPedido item = new ItemPedido();
        item.setProduto(produto);
        item.setQuantidade(itemDTO.getQuantidade());
        item.calcularSubtotal();
        pedido.adicionarItem(item);

        pedidoRepository.save(pedido);
        log.info("➕ Produto '{}' adicionado ao pedido {}", produto.getNome(), pedido.getId());

        return pedidoMapper.toDto(pedido);
    }

    @Override
    public PedidoDTO removerItem(Long pedidoId, Long itemId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com ID: " + pedidoId));

        ItemPedido item = itemPedidoRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item não encontrado com ID: " + itemId));

        pedido.removerItem(item);
        pedidoRepository.save(pedido);

        log.info("➖ Item ID {} removido do pedido {}", itemId, pedidoId);
        return pedidoMapper.toDto(pedido);
    }

    @Override
    public PedidoDTO atualizarQuantidadeItem(Long pedidoId, Long itemId, Integer novaQuantidade) {
        ItemPedido item = itemPedidoRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item não encontrado com ID: " + itemId));

        if (!item.getPedido().getId().equals(pedidoId))
            throw new IllegalArgumentException("Item não pertence a este pedido.");

        item.setQuantidade(novaQuantidade);
        item.calcularSubtotal();
        itemPedidoRepository.save(item);

        Pedido pedido = item.getPedido();
        pedido.calcularTotais();
        pedidoRepository.save(pedido);

        log.info("🔄 Quantidade do item {} atualizada para {}", itemId, novaQuantidade);
        return pedidoMapper.toDto(pedido);
    }

    // ==============================
    // STATUS / FLUXO DO PEDIDO
    // ==============================
    @Override
    public PedidoDTO atualizarStatus(Long pedidoId, StatusPedido status) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com ID: " + pedidoId));

        pedido.setStatus(status);
        pedido.setDataAtualizacao(LocalDateTime.now());
        pedidoRepository.save(pedido);

        log.info("🚦 Status do pedido {} alterado para {}", pedidoId, status);
        return pedidoMapper.toDto(pedido);
    }

    @Override
    public PedidoDTO moverParaPreparo(Long id) {
        return atualizarStatus(id, StatusPedido.EM_PREPARO);
    }

    @Override
    public PedidoDTO marcarComoPronto(Long id) {
        return atualizarStatus(id, StatusPedido.PRONTO);
    }

    @Override
    public PedidoDTO marcarComoEntregue(Long id) {
        return atualizarStatus(id, StatusPedido.ENTREGUE);
    }

    @Override
    public PedidoDTO cancelarPedido(Long id, String motivo) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com ID: " + id));

        pedido.setStatus(StatusPedido.CANCELADO);
        pedido.setObservacao((pedido.getObservacao() != null ? pedido.getObservacao() + " | " : "") +
                "Cancelado: " + motivo);
        pedidoRepository.save(pedido);

        log.warn("❌ Pedido {} cancelado. Motivo: {}", id, motivo);
        return pedidoMapper.toDto(pedido);
    }

    // ==============================
    // FINANCEIRO
    // ==============================
    @Override
    public PedidoDTO aplicarAcrescimo(Long pedidoId, BigDecimal valor, String justificativa) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com ID: " + pedidoId));
        pedido.aplicarAcrescimo(valor, justificativa);
        return pedidoMapper.toDto(pedidoRepository.save(pedido));
    }

    @Override
    public PedidoDTO aplicarDesconto(Long pedidoId, BigDecimal valor, String justificativa) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com ID: " + pedidoId));
        pedido.aplicarDesconto(valor, justificativa);
        return pedidoMapper.toDto(pedidoRepository.save(pedido));
    }

    @Override
    public PedidoDTO fecharPedido(Long pedidoId, FecharPedidoDTO dto) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com ID: " + pedidoId));

        pedido.aplicarAcrescimo(dto.getAcrescimo(), dto.getJustificativaAcrescimo());
        pedido.aplicarDesconto(dto.getDesconto(), dto.getJustificativaDesconto());
        pedido.fecharPedido();
        pedidoRepository.save(pedido);

        log.info("💰 Pedido {} fechado com valor final de R$ {}", pedidoId, pedido.getValorFinal());
        return pedidoMapper.toDto(pedido);
    }

    @Override
    public PedidoFinalizadoDTO finalizarVenda(Long pedidoId, FinalizarVendaDTO dto) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com ID: " + pedidoId));

        BigDecimal troco = calcularTroco(pedidoId, dto.getValorPago());
        pedido.fecharPedido();
        pedidoRepository.save(pedido);

        log.info("✅ Pedido {} finalizado com pagamento de R$ {} (troco R$ {})",
                pedidoId, dto.getValorPago(), troco);

        return PedidoFinalizadoDTO.builder()
                .pedidoId(pedidoId)
                .mesaId(pedido.getMesa().getId())
                .mesaNumero(pedido.getMesa().getNumero())
                .total(pedido.getValorFinal())
                .valorPago(dto.getValorPago())
                .troco(troco)
                .formaPagamento(dto.getFormaPagamento())
                .codigoTransacao(dto.getCodigoTransacao())
                .dataFechamento(LocalDateTime.now())
                .nomeCliente(dto.getNomeCliente())
                .documentoCliente(dto.getDocumentoCliente())
                .build();
    }

    @Override
    public BigDecimal calcularTroco(Long pedidoId, BigDecimal valorPago) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com ID: " + pedidoId));
        return valorPago.subtract(pedido.getValorFinal());
    }

    @Override
    public PedidoDTO reabrirPedido(Long pedidoId, String motivo) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com ID: " + pedidoId));

        pedido.reabrirPedido(motivo);
        pedidoRepository.save(pedido);

        log.info("🔓 Pedido {} reaberto. Motivo: {}", pedidoId, motivo);
        return pedidoMapper.toDto(pedido);
    }

    // ==============================
    // CONSULTAS / RELATÓRIOS
    // ==============================
    @Override
    @Transactional(readOnly = true)
    public List<PedidoDTO> findByStatus(StatusPedido status) {
        return pedidoMapper.toDtoList(pedidoRepository.findByStatus(status));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoDTO> findByMesaId(Long mesaId) {
        return pedidoMapper.toDtoList(pedidoRepository.findByMesaId(mesaId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoDTO> findPedidosAtivos() {
        return pedidoMapper.toDtoList(pedidoRepository.findPedidosAtivos());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoDTO> findPedidosPorPeriodo(String dataInicio, String dataFim) {
        LocalDateTime inicio = LocalDate.parse(dataInicio).atStartOfDay();
        LocalDateTime fim = LocalDate.parse(dataFim).atTime(23, 59, 59);
        return pedidoMapper.toDtoList(pedidoRepository.findByPeriodo(inicio, fim));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoDTO> findPedidosCozinha() {
        List<StatusPedido> status = List.of(
                StatusPedido.EM_PREPARO,
                StatusPedido.PRONTO,
                StatusPedido.ABERTO
        );
        return pedidoMapper.toDtoList(pedidoRepository.findByStatusIn(status));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResumidoDTO> findResumoVendasDiarias(String data) {
        LocalDateTime inicio = LocalDate.parse(data).atStartOfDay();
        LocalDateTime fim = inicio.plusDays(1);

        List<Pedido> pedidos = pedidoRepository.findByPeriodo(inicio, fim);
        return pedidos.stream().map(p ->
                PedidoResumidoDTO.builder()
                        .id(p.getId())
                        .mesaNumero(p.getMesa().getNumero())
                        .status(p.getStatus())
                        .total(p.getValorFinal())
                        .dataCriacao(p.getDataCriacao())
                        .usuarioNome(p.getUsuario().getNome())
                        .build()
        ).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EstatisticasPedidosDTO obterEstatisticas(String dataInicio, String dataFim) {
        List<PedidoDTO> pedidos = findPedidosPorPeriodo(dataInicio, dataFim);

        BigDecimal totalVendas = pedidos.stream()
                .map(PedidoDTO::getValorFinal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalPedidos = pedidos.size();
        long cancelados = pedidos.stream().filter(p -> p.getStatus() == StatusPedido.CANCELADO).count();

        return EstatisticasPedidosDTO.builder()
                .totalPedidos(totalPedidos)
                .totalCancelados(cancelados)
                .totalVendas(totalVendas)
                .periodoInicio(dataInicio)
                .periodoFim(dataFim)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularTotalVendasPeriodo(String dataInicio, String dataFim) {
        return obterEstatisticas(dataInicio, dataFim).getTotalVendas();
    }
}
