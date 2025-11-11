package com.synclink.application.mapper;

import com.synclink.application.dto.CategoriaDTO;
import com.synclink.model.Categoria;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {
    CategoriaMapper INSTANCE = Mappers.getMapper(CategoriaMapper.class);

    CategoriaDTO toDto(Categoria categoria);

    Categoria toEntity(CategoriaDTO categoriaDTO);

    List<CategoriaDTO> toDtoList(List<Categoria> categorias);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(CategoriaDTO categoriaDTO, @MappingTarget Categoria categoria);
}