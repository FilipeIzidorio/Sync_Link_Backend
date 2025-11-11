package com.synclink.application.service.impl;

import com.synclink.application.dto.CategoriaDTO;
import com.synclink.application.mapper.CategoriaMapper;
import com.synclink.application.service.CategoriaService;
import com.synclink.domain.repository.CategoriaRepository;
import com.synclink.model.Categoria;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDTO> findAll() {
        return mapper.toDtoList(categoriaRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaDTO findById(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada com ID: " + id));
        return mapper.toDto(categoria);
    }

    @Override
    @Transactional
    public CategoriaDTO create(CategoriaDTO dto) {
        if (categoriaRepository.existsByNomeIgnoreCase(dto.getNome())) {
            throw new RuntimeException("Já existe uma categoria com este nome");
        }
        Categoria entity = mapper.toEntity(dto);
        return mapper.toDto(categoriaRepository.save(entity));
    }

    @Override
    @Transactional
    public CategoriaDTO update(Long id, CategoriaDTO dto) {
        Categoria entity = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada com ID: " + id));
        mapper.updateEntityFromDto(dto, entity);
        return mapper.toDto(categoriaRepository.save(entity));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Categoria entity = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada com ID: " + id));
        categoriaRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDTO> findByAtivo(Boolean ativo) {
        return mapper.toDtoList(categoriaRepository.findByAtivo(ativo));
    }
}
