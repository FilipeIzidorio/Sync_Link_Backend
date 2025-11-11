package com.synclink.infrastructure.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * Serviço responsável pela geração e validação de tokens JWT.
 * Utiliza HMAC-SHA256 para assinatura e informações de expiração configuráveis.
 */
@Service
public class JwtService {

    @Value("${sync.security.jwt.secret}")
    private String secretKey;

    @Value("${sync.security.jwt.expiration-minutes}")
    private long expirationMinutes;

    // ============================================================
    // 🔹 GERAR TOKEN JWT (com username / e-mail)
    // ============================================================
    public String generateToken(String username) {
        return generateToken(Map.of(), username);
    }

    // ============================================================
    // 🔹 GERAR TOKEN JWT COM CLAIMS CUSTOMIZADAS
    // ============================================================
    public String generateToken(Map<String, Object> extraClaims, String username) {
        Date now = new Date(System.currentTimeMillis());
        Date expiration = new Date(now.getTime() + expirationMinutes * 60 * 1000);

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ============================================================
    // 🔹 EXTRAIR USERNAME (E-MAIL)
    // ============================================================
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // ============================================================
    // 🔹 VALIDAR TOKEN COMPLETO (seguro e centralizado)
    // ============================================================
    public boolean isTokenValid(String token, String username) {
        try {
            final String subject = extractUsername(token);
            return subject.equals(username) && !isTokenExpired(token);
        } catch (ExpiredJwtException e) {
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // ============================================================
    // 🔹 VERIFICAR EXPIRAÇÃO
    // ============================================================
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // ============================================================
    // 🔹 EXTRAIR CLAIM GENÉRICA
    // ============================================================
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // ============================================================
    // 🔹 PARSE E VALIDA A ASSINATURA DO TOKEN
    // ============================================================
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token expirado.");
        } catch (JwtException e) {
            throw new RuntimeException("Token inválido.");
        }
    }

    // ============================================================
    // 🔹 CHAVE DE ASSINATURA (Base64)
    // ============================================================
    private Key getSignInKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            throw new IllegalStateException("Chave secreta JWT inválida ou mal configurada.");
        }
    }

    // ============================================================
    // 🔹 OBTÉM TEMPO DE EXPIRAÇÃO (em segundos)
    // ============================================================
    public long getExpirationTime() {
        return expirationMinutes * 60;
    }

    // ============================================================
    // 🔹 VALIDAÇÃO RÁPIDA (para /auth/validate-token ou /refresh)
    // ============================================================
    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }
}
