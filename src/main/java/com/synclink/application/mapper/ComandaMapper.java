package com.synclink.application.mapper;

import com.synclink.application.dto.ComandaDTO;
import com.synclink.model.Comanda;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PedidoMapper.class})
public interface ComandaMapper {

    ComandaMapper INSTANCE = Mappers.getMapper(ComandaMapper.class);

    @Mapping(target = "mesaId", source = "mesa.id")
    @Mapping(target = "mesaNumero", source = "mesa.numero")
    @Mapping(target = "total", expression = "java(comanda.calcularTotal())")
    ComandaDTO toDto(Comanda comanda);

    Comanda toEntity(ComandaDTO dto);

    List<ComandaDTO> toDtoList(List<Comanda> comandas);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ComandaDTO dto, @MappingTarget Comanda entity);
}
