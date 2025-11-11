package com.synclink.application.mapper;

import com.synclink.application.dto.EstoqueDTO;
import com.synclink.model.Estoque;
import com.synclink.model.Produto;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EstoqueMapper {

    EstoqueMapper INSTANCE = Mappers.getMapper(EstoqueMapper.class);

    @Mapping(target = "produtoId", source = "produto.id")
    EstoqueDTO toDto(Estoque estoque);

    @Mapping(target = "produto", expression = "java(mapProduto(dto.getProdutoId()))")
    Estoque toEntity(EstoqueDTO dto);

    List<EstoqueDTO> toDtoList(List<Estoque> estoques);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "produto", expression = "java(mapProduto(dto.getProdutoId()))")
    void updateEntityFromDto(EstoqueDTO dto, @MappingTarget Estoque estoque);

    // Método auxiliar para criar apenas referência do Produto
    default Produto mapProduto(Long produtoId) {
        if (produtoId == null) return null;
        Produto p = new Produto();
        p.setId(produtoId);
        return p;
    }
}
