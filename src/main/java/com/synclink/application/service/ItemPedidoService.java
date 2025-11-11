package com.synclink.application.service;

import com.synclink.application.dto.ItemPedidoDTO;

import java.util.List;

public interface ItemPedidoService {

    List<ItemPedidoDTO> findAll();

    ItemPedidoDTO findById(Long id);

    ItemPedidoDTO create(ItemPedidoDTO dto);

    ItemPedidoDTO update(Long id, ItemPedidoDTO dto);

    void delete(Long id);

    List<ItemPedidoDTO> findByPedidoId(Long pedidoId);
}
