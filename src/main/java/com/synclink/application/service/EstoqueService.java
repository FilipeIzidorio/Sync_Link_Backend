package com.synclink.application.service;

import com.synclink.application.dto.EstoqueDTO;
import java.util.List;

public interface EstoqueService {

    List<EstoqueDTO> findAll();
    EstoqueDTO findById(Long id);
    EstoqueDTO create(EstoqueDTO estoqueDTO);
    EstoqueDTO update(Long id, EstoqueDTO estoqueDTO);
    void delete(Long id);
    List<EstoqueDTO> findByProdutoId(Long produtoId);
    List<EstoqueDTO> findPrecisaRepor();
    EstoqueDTO adicionarQuantidade(Long id, Integer quantidade);
    EstoqueDTO removerQuantidade(Long id, Integer quantidade);
}