package com.synclink;

import com.synclink.model.PerfilUsuario;
import com.synclink.model.Usuario;
import com.synclink.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        criarUsuarioAdmin();
    }

    private void criarUsuarioAdmin() {
        try {
            // Verificar se já existe algum usuário admin
            boolean adminExiste = usuarioRepository.findByPerfil(PerfilUsuario.ADMIN)
                    .stream()
                    .anyMatch(Usuario::getAtivo);

            if (!adminExiste) {
                // Criar usuário admin padrão
                Usuario admin = new Usuario();
                admin.setNome("Administrador do Sistema");
                admin.setEmail("admin@gmail.com");
                admin.setSenha(passwordEncoder.encode("admin123"));
                admin.setPerfil(PerfilUsuario.ADMIN);
                admin.setAtivo(true);
                admin.setDataCriacao(LocalDateTime.now());

                usuarioRepository.save(admin);

                log.info("==========================================");
                log.info("🚀 USUÁRIO ADMIN CRIADO COM SUCESSO!");
                log.info("📧 Email: admin@gmail.com");
                log.info("🔑 Senha: admin123");
                log.info("👑 Perfil: ADMIN");
                log.info("==========================================");

            } else {
                log.info("✅ Usuário admin já existe no banco de dados");
            }

        } catch (Exception e) {
            log.error("❌ Erro ao criar usuário admin: {}", e.getMessage());
        }
    }
}