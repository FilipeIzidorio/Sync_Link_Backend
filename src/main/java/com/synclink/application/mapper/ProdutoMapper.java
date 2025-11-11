package com.synclink.application.mapper;

import com.synclink.application.dto.ProdutoDTO;
import com.synclink.model.Categoria;
import com.synclink.model.Produto;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProdutoMapper {

    ProdutoMapper INSTANCE = Mappers.getMapper(ProdutoMapper.class);


    @Mapping(target = "categoriaId", source = "categoria.id")
    ProdutoDTO toDto(Produto produto);



    @Mapping(target = "categoria.id", source = "categoriaId")
    Produto toEntity(ProdutoDTO produtoDTO);

    // ✅ Lista de entidades → lista de DTOs
    List<ProdutoDTO> toDtoList(List<Produto> produtos);

    // ✅ Atualização parcial (PATCH/PUT)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "categoria.id", source = "categoriaId", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ProdutoDTO produtoDTO, @MappingTarget Produto produto);

    // ⚙️ Método auxiliar (para evitar erro de referência nula)
    default Categoria mapCategoria(Long categoriaId) {
        if (categoriaId == null) return null;
        Categoria categoria = new Categoria();
        categoria.setId(categoriaId);
        return categoria;
    }
}
