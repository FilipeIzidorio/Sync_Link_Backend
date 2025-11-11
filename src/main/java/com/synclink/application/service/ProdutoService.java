package com.synclink.application.service;

import com.synclink.application.dto.ProdutoDTO;
import java.util.List;

public interface ProdutoService {

    List<ProdutoDTO> findAll();
    ProdutoDTO findById(Long id);
    ProdutoDTO create(ProdutoDTO produtoDTO);
    ProdutoDTO update(Long id, ProdutoDTO produtoDTO);
    void delete(Long id);
    List<ProdutoDTO> findByCategoriaId(Long categoriaId);
    List<ProdutoDTO> findByAtivo(Boolean ativo);
    ProdutoDTO ativar(Long id);
    ProdutoDTO inativar(Long id);
}