package com.synclink.application.service;

import com.synclink.application.dto.CategoriaDTO;
import java.util.List;

public interface CategoriaService {

    List<CategoriaDTO> findAll();
    CategoriaDTO findById(Long id);
    CategoriaDTO create(CategoriaDTO categoriaDTO);
    CategoriaDTO update(Long id, CategoriaDTO categoriaDTO);
    void delete(Long id);
    List<CategoriaDTO> findByAtivo(Boolean ativo);
}