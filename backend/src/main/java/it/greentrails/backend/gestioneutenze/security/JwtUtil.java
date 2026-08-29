package it.greentrails.backend.gestioneutenze.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  private final SecretKey signingKey;
  private final long expirationMs;

  public JwtUtil(
      @Value("${jwt.secret:GreenTrailsDefaultSecretKey2026!ChangeInProd}") final String secret,
      @Value("${jwt.expiration-ms:3600000}") final long expirationMs
  ) {
    this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expirationMs = expirationMs;
  }

  public String generateToken(final Long id, final String email,
      final String nome, final String cognome, final String ruolo) {
    final Date now = new Date();
    final Date expiry = new Date(now.getTime() + expirationMs);
    return Jwts.builder()
        .subject(email)
        .claim("id", id)
        .claim("nome", nome)
        .claim("cognome", cognome)
        .claim("ruolo", ruolo)
        .issuedAt(now)
        .expiration(expiry)
        .signWith(signingKey)
        .compact();
  }

  public String extractEmail(final String token) {
    return parseClaims(token).getSubject();
  }

  public Long extractId(final String token) {
    return parseClaims(token).get("id", Long.class);
  }

  public String extractNome(final String token) {
    return parseClaims(token).get("nome", String.class);
  }

  public String extractCognome(final String token) {
    return parseClaims(token).get("cognome", String.class);
  }

  public String extractRuolo(final String token) {
    return parseClaims(token).get("ruolo", String.class);
  }

  public boolean validateToken(final String token) {
    try {
      parseClaims(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  private Claims parseClaims(final String token) {
    return Jwts.parser()
        .verifyWith(signingKey)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}
