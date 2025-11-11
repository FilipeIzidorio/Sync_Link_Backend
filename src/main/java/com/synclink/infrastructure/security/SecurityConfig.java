package com.synclink.infrastructure.security;

import com.synclink.application.service.AuthService;
import com.synclink.infrastructure.config.PasswordConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuração central de segurança do sistema Sync Link.
 * Controla autenticação JWT, permissões por perfil e rotas públicas.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthService authService; // ✅ Injetado automaticamente
    private final PasswordConfig passwordConfig;

    // ============================================================
    // 🔹 DEFINIÇÃO PRINCIPAL DO FILTRO DE SEGURANÇA
    // ============================================================
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CORS e CSRF
                .cors(cors -> {}) // habilitado via CorsConfig
                .csrf(csrf -> csrf.disable())

                // Sessão stateless (JWT)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 🔓 ROTAS PÚBLICAS E PRIVADAS
                .authorizeHttpRequests(auth -> auth
                        // Libera preflight requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Rotas públicas
                        .requestMatchers(
                                "/auth/login",
                                "/auth/signup",
                                "/auth/refresh",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/error",
                                "/ws/**",
                                "/ws-test/**"
                        ).permitAll()

                        // Rotas autenticadas
                        .requestMatchers(
                                "/auth/me",
                                "/auth/logout",
                                "/auth/change-password"
                        ).authenticated()

                        // Rotas com restrição de papéis
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/gerente/**").hasAnyRole("ADMIN", "GERENTE")
                        .requestMatchers("/api/cozinha/**").hasAnyRole("ADMIN", "GERENTE", "COZINHA")
                        .requestMatchers("/api/garcom/**").hasAnyRole("ADMIN", "GERENTE", "GARCOM")
                        .requestMatchers("/api/caixa/**").hasAnyRole("ADMIN", "GERENTE", "CAIXA")
                        .requestMatchers("/api/usuarios/**").authenticated()

                        // Qualquer outra rota requer autenticação
                        .anyRequest().authenticated()
                )

                // 🔑 Provider e Filtro JWT
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ============================================================
    // 🔹 BEANS DE AUTENTICAÇÃO
    // ============================================================

    /**
     * Define o provedor de autenticação usando o AuthService e o encoder.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(authService);
        provider.setPasswordEncoder(passwordConfig.passwordEncoder());
        return provider;
    }

    /**
     * Gerenciador de autenticação central do Spring.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
