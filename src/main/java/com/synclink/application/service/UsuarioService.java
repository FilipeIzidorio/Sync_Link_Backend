package com.synclink.application.service;

import com.synclink.application.dto.UsuarioDTO;
import java.util.List;

public interface UsuarioService {

    List<UsuarioDTO> findAll();
    UsuarioDTO findById(Long id);
    UsuarioDTO create(UsuarioDTO usuarioDTO);
    UsuarioDTO update(Long id, UsuarioDTO usuarioDTO);
    void delete(Long id);

    UsuarioDTO ativar(Long id);
    UsuarioDTO inativar(Long id);

    List<UsuarioDTO> findByPerfil(String perfil);
    List<UsuarioDTO> findByAtivo(Boolean ativo);

    // 🔹 NOVO MÉTODO
    UsuarioDTO getUsuarioAtual();

    // 🔹 MÉTODO PARA SPRING SECURITY
    boolean isCurrentUser(Long userId);
}