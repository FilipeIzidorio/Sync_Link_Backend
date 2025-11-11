package com.synclink.application.mapper;

import com.synclink.application.dto.ProdutoDTO;
import com.synclink.model.Produto;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProdutoMapper {
    ProdutoMapper INSTANCE = Mappers.getMapper(ProdutoMapper.class);

    ProdutoDTO toDto(Produto produto);


    Produto toEntity(ProdutoDTO produtoDTO);

    List<ProdutoDTO> toDtoList(List<Produto> produtos);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ProdutoDTO produtoDTO, @MappingTarget Produto produto);
}