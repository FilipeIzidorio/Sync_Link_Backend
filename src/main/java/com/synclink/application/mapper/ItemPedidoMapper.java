package com.synclink.application.mapper;

import com.synclink.application.dto.ItemPedidoDTO;
import com.synclink.model.ItemPedido;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemPedidoMapper {
    ItemPedidoMapper INSTANCE = Mappers.getMapper(ItemPedidoMapper.class);

    ItemPedidoDTO toDto(ItemPedido itemPedido);


    ItemPedido toEntity(ItemPedidoDTO itemPedidoDTO);

    List<ItemPedidoDTO> toDtoList(List<ItemPedido> itensPedido);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ItemPedidoDTO itemPedidoDTO, @MappingTarget ItemPedido itemPedido);
}