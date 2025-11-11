package com.synclink.application.mapper;

import com.synclink.application.dto.MesaDTO;
import com.synclink.model.Mesa;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MesaMapper {

    MesaMapper INSTANCE = Mappers.getMapper(MesaMapper.class);

    MesaDTO toDto(Mesa mesa);

    Mesa toEntity(MesaDTO dto);

    List<MesaDTO> toDtoList(List<Mesa> mesas);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(MesaDTO dto, @MappingTarget Mesa entity);
}
