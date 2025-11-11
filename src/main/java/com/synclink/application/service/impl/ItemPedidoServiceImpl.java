package com.synclink.application.service.impl;

import com.synclink.application.dto.ItemPedidoDTO;
import com.synclink.application.mapper.ItemPedidoMapper;
import com.synclink.application.service.ItemPedidoService;
import com.synclink.domain.repository.ItemPedidoRepository;
import com.synclink.domain.repository.PedidoRepository;
import com.synclink.domain.repository.ProdutoRepository;
import com.synclink.model.ItemPedido;
import com.synclink.model.Pedido;
import com.synclink.model.Produto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ItemPedidoServiceImpl implements ItemPedidoService {

    private final ItemPedidoRepository itemPedidoRepository;
    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final ItemPedidoMapper itemPedidoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ItemPedidoDTO> findAll() {
        return itemPedidoMapper.toDtoList(itemPedidoRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public ItemPedidoDTO findById(Long id) {
        ItemPedido item = itemPedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item de pedido não encontrado com ID: " + id));
        return itemPedidoMapper.toDto(item);
    }

    @Override
    public ItemPedidoDTO create(ItemPedidoDTO dto) {
        Pedido pedido = pedidoRepository.findById(dto.getPedidoId())
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com ID: " + dto.getPedidoId()));

        Produto produto = produtoRepository.findById(dto.getProdutoId())
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com ID: " + dto.getProdutoId()));

        ItemPedido item = new ItemPedido();
        item.setPedido(pedido);
        item.setProduto(produto);
        item.setQuantidade(dto.getQuantidade());
        item.setPrecoUnitario(produto.getPreco());
        item.setObservacao(dto.getObservacao());

        itemPedidoRepository.save(item);
        pedido.calcularTotais();
        pedidoRepository.save(pedido);

        log.info("✅ Item '{}' adicionado ao pedido {}", produto.getNome(), pedido.getId());
        return itemPedidoMapper.toDto(item);
    }

    @Override
    public ItemPedidoDTO update(Long id, ItemPedidoDTO dto) {
        ItemPedido item = itemPedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item não encontrado com ID: " + id));

        item.setQuantidade(dto.getQuantidade());
        item.setObservacao(dto.getObservacao());
        item.calcularSubtotal();
        itemPedidoRepository.save(item);

        Pedido pedido = item.getPedido();
        pedido.calcularTotais();
        pedidoRepository.save(pedido);

        log.info("🔁 Item ID {} atualizado com sucesso no pedido {}", id, pedido.getId());
        return itemPedidoMapper.toDto(item);
    }

    @Override
    public void delete(Long id) {
        ItemPedido item = itemPedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item não encontrado com ID: " + id));

        Pedido pedido = item.getPedido();
        itemPedidoRepository.delete(item);

        pedido.calcularTotais();
        pedidoRepository.save(pedido);

        log.info("🗑️ Item ID {} removido do pedido {}", id, pedido.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemPedidoDTO> findByPedidoId(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com ID: " + pedidoId));

        return itemPedidoMapper.toDtoList(pedido.getItens());
    }
}
