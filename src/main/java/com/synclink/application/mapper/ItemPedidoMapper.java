package com.synclink.application.mapper;

import com.synclink.application.dto.ItemPedidoDTO;
import com.synclink.model.ItemPedido;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemPedidoMapper {

    ItemPedidoMapper INSTANCE = Mappers.getMapper(ItemPedidoMapper.class);

    @Mapping(target = "pedidoId", source = "pedido.id")
    @Mapping(target = "produtoId", source = "produto.id")
    @Mapping(target = "produtoNome", source = "produto.nome")
    @Mapping(target = "precoProduto", source = "produto.preco")
    @Mapping(target = "subtotal", expression = "java(itemPedido.getSubtotal())")
    ItemPedidoDTO toDto(ItemPedido itemPedido);

    @InheritInverseConfiguration
    ItemPedido toEntity(ItemPedidoDTO dto);

    List<ItemPedidoDTO> toDtoList(List<ItemPedido> itensPedido);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ItemPedidoDTO dto, @MappingTarget ItemPedido entity);
}
