package com.synclink.application.mapper;

import com.synclink.application.dto.EstoqueDTO;
import com.synclink.model.Estoque;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ProdutoMapper.class})
public interface EstoqueMapper {
    EstoqueMapper INSTANCE = Mappers.getMapper(EstoqueMapper.class);


    EstoqueDTO toDto(Estoque estoque);


    Estoque toEntity(EstoqueDTO estoqueDTO);

    List<EstoqueDTO> toDtoList(List<Estoque> estoques);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(EstoqueDTO estoqueDTO, @MappingTarget Estoque estoque);
}