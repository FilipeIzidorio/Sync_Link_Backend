package com.synclink.application.mapper;

import com.synclink.application.dto.PedidoDTO;
import com.synclink.model.Pedido;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ItemPedidoMapper.class})
public interface PedidoMapper {

    PedidoMapper INSTANCE = Mappers.getMapper(PedidoMapper.class);

    @Mapping(target = "mesaId", source = "mesa.id")
    @Mapping(target = "mesaNumero", source = "mesa.numero")
    @Mapping(target = "usuarioId", source = "usuario.id")
    @Mapping(target = "usuarioNome", source = "usuario.nome")
    PedidoDTO toDto(Pedido pedido);

    @InheritInverseConfiguration
    Pedido toEntity(PedidoDTO pedidoDTO);

    List<PedidoDTO> toDtoList(List<Pedido> pedidos);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(PedidoDTO dto, @MappingTarget Pedido pedido);
}
