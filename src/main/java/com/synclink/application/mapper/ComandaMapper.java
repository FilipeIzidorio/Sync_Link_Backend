package com.synclink.application.mapper;

import com.synclink.application.dto.ComandaDTO;
import com.synclink.model.Comanda;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ComandaMapper {
    ComandaMapper INSTANCE = Mappers.getMapper(ComandaMapper.class);

    ComandaDTO toDto(Comanda comanda);


    Comanda toEntity(ComandaDTO comandaDTO);

    List<ComandaDTO> toDtoList(List<Comanda> comandas);


    void updateEntityFromDto(ComandaDTO comandaDTO, @MappingTarget Comanda comanda);
}