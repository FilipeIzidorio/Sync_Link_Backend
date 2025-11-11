package com.synclink.application.mapper;

import com.synclink.application.dto.PedidoDTO;
import com.synclink.model.Pedido;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ItemPedidoMapper.class})
public interface PedidoMapper {
    PedidoMapper INSTANCE = Mappers.getMapper(PedidoMapper.class);


    PedidoDTO toDto(Pedido pedido);

    Pedido toEntity(PedidoDTO pedidoDTO);

    List<PedidoDTO> toDtoList(List<Pedido> pedidos);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(PedidoDTO pedidoDTO, @MappingTarget Pedido pedido);
}