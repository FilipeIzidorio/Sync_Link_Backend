package com.synclink.application.service.impl;

import com.synclink.application.dto.UsuarioDTO;
import com.synclink.application.mapper.UsuarioMapper;
import com.synclink.application.service.UsuarioService;
import com.synclink.application.service.AuthService;
import com.synclink.model.enums.PerfilUsuario;
import com.synclink.model.Usuario;
import com.synclink.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> findAll() {
        try {
            return usuarioMapper.toDtoList(usuarioRepository.findAll());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar usuários: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO findById(Long id) {
        try {
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado com ID: " + id));
            return usuarioMapper.toDto(usuario);
        } catch (NoSuchElementException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar usuário: " + e.getMessage());
        }
    }

    @Override
    public UsuarioDTO create(UsuarioDTO usuarioDTO) {
        try {
            // Verificar se email já existe
            if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
                throw new IllegalArgumentException("Já existe um usuário com o email: " + usuarioDTO.getEmail());
            }

            // Validações adicionais para criação segura
            validarCriacaoUsuario(usuarioDTO);

            Usuario usuario = usuarioMapper.toEntity(usuarioDTO);
            usuario.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
            usuario.setAtivo(true); // Novo usuário sempre começa ativo

            usuario = usuarioRepository.save(usuario);
            return usuarioMapper.toDto(usuario);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar usuário: " + e.getMessage());
        }
    }

    @Override
    public UsuarioDTO update(Long id, UsuarioDTO usuarioDTO) {
        try {
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado com ID: " + id));

            // Verificar se novo email já existe (se foi alterado)
            if (!usuario.getEmail().equals(usuarioDTO.getEmail()) &&
                    usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
                throw new IllegalArgumentException("Já existe um usuário com o email: " + usuarioDTO.getEmail());
            }

            // Validação de segurança - usuário comum não pode alterar seu próprio perfil
            validarAtualizacaoUsuario(usuario, usuarioDTO);

            usuarioMapper.updateEntityFromDto(usuarioDTO, usuario);

            // Se senha foi fornecida, criptografar
            if (usuarioDTO.getSenha() != null && !usuarioDTO.getSenha().isEmpty()) {
                usuario.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
            }

            usuario.setDataAtualizacao(java.time.LocalDateTime.now());
            usuario = usuarioRepository.save(usuario);
            return usuarioMapper.toDto(usuario);

        } catch (NoSuchElementException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar usuário: " + e.getMessage());
        }
    }

    @Override
    public void delete(Long id) {
        try {
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado com ID: " + id));

            // Não permitir exclusão do próprio usuário
            Usuario usuarioAtual = authService.getCurrentUser();
            if (usuarioAtual != null && usuarioAtual.getId().equals(id)) {
                throw new IllegalStateException("Não é possível excluir seu próprio usuário");
            }

            // Não permitir exclusão do último ADMIN
            if (usuario.getPerfil() == PerfilUsuario.ADMIN) {
                long totalAdmins = usuarioRepository.findByPerfil(PerfilUsuario.ADMIN).size();
                if (totalAdmins <= 1) {
                    throw new IllegalStateException("Não é possível excluir o último usuário ADMIN do sistema");
                }
            }

            usuarioRepository.delete(usuario);

        } catch (NoSuchElementException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao excluir usuário: " + e.getMessage());
        }
    }

    @Override
    public UsuarioDTO ativar(Long id) {
        try {
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado com ID: " + id));

            usuario.setAtivo(true);
            usuario.setDataAtualizacao(java.time.LocalDateTime.now());
            usuario = usuarioRepository.save(usuario);
            return usuarioMapper.toDto(usuario);

        } catch (NoSuchElementException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao ativar usuário: " + e.getMessage());
        }
    }

    @Override
    public UsuarioDTO inativar(Long id) {
        try {
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado com ID: " + id));

            // Não permitir inativar o próprio usuário
            Usuario usuarioAtual = authService.getCurrentUser();
            if (usuarioAtual != null && usuarioAtual.getId().equals(id)) {
                throw new IllegalStateException("Não é possível inativar seu próprio usuário");
            }

            // Não permitir inativar o último ADMIN
            if (usuario.getPerfil() == PerfilUsuario.ADMIN) {
                long adminsAtivos = usuarioRepository.findByPerfil(PerfilUsuario.ADMIN)
                        .stream()
                        .filter(Usuario::getAtivo)
                        .count();
                if (adminsAtivos <= 1) {
                    throw new IllegalStateException("Não é possível inativar o último usuário ADMIN ativo do sistema");
                }
            }

            usuario.setAtivo(false);
            usuario.setDataAtualizacao(java.time.LocalDateTime.now());
            usuario = usuarioRepository.save(usuario);
            return usuarioMapper.toDto(usuario);

        } catch (NoSuchElementException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inativar usuário: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> findByPerfil(String perfil) {
        try {
            PerfilUsuario perfilUsuario = PerfilUsuario.valueOf(perfil.toUpperCase());
            return usuarioMapper.toDtoList(usuarioRepository.findByPerfil(perfilUsuario));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Perfil inválido: " + perfil);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar usuários por perfil: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> findByAtivo(Boolean ativo) {
        try {
            return usuarioMapper.toDtoList(usuarioRepository.findByAtivo(ativo));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar usuários ativos: " + e.getMessage());
        }
    }

    // 🔹 NOVO MÉTODO - Obter usuário atual
    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO getUsuarioAtual() {
        try {
            Usuario usuario = authService.getCurrentUser();
            if (usuario == null) {
                throw new IllegalStateException("Usuário não autenticado");
            }
            return usuarioMapper.toDto(usuario);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao obter usuário atual: " + e.getMessage());
        }
    }

    // 🔹 MÉTODO PARA SPRING SECURITY - Verificar se é o usuário atual
    public boolean isCurrentUser(Long userId) {
        Usuario usuarioAtual = authService.getCurrentUser();
        return usuarioAtual != null && usuarioAtual.getId().equals(userId);
    }

    // ============================================================
    // MÉTODOS PRIVADOS DE VALIDAÇÃO
    // ============================================================

    private void validarCriacaoUsuario(UsuarioDTO usuarioDTO) {
        // Validar perfil - apenas ADMIN pode criar outros ADMINS
        Usuario usuarioAtual = authService.getCurrentUser();
        if (usuarioAtual != null && usuarioDTO.getPerfil() == PerfilUsuario.ADMIN) {
            if (usuarioAtual.getPerfil() != PerfilUsuario.ADMIN) {
                throw new AccessDeniedException("Apenas ADMIN pode criar usuários com perfil ADMIN");
            }
        }

        // Validar força da senha
        if (usuarioDTO.getSenha() == null || usuarioDTO.getSenha().length() < 6) {
            throw new IllegalArgumentException("Senha deve ter no mínimo 6 caracteres");
        }
    }

    private void validarAtualizacaoUsuario(Usuario usuarioExistente, UsuarioDTO usuarioDTO) {
        Usuario usuarioAtual = authService.getCurrentUser();

        if (usuarioAtual == null) {
            throw new AccessDeniedException("Usuário não autenticado");
        }

        // Se não é ADMIN, validar restrições
        if (usuarioAtual.getPerfil() != PerfilUsuario.ADMIN) {
            // Usuário comum só pode atualizar seu próprio registro
            if (!usuarioAtual.getId().equals(usuarioExistente.getId())) {
                throw new AccessDeniedException("Você só pode atualizar seus próprios dados");
            }

            // Usuário comum não pode alterar seu próprio perfil
            if (usuarioDTO.getPerfil() != null &&
                    !usuarioDTO.getPerfil().equals(usuarioExistente.getPerfil())) {
                throw new AccessDeniedException("Você não pode alterar seu próprio perfil");
            }

            // Usuário comum não pode alterar status ativo
            if (usuarioDTO.getAtivo() != null &&
                    !usuarioDTO.getAtivo().equals(usuarioExistente.getAtivo())) {
                throw new AccessDeniedException("Você não pode alterar seu status ativo");
            }
        }
    }
}