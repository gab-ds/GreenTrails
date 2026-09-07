package it.greentrails.backend.gestioneutenze.controller;

import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.gestioneutenze.security.JwtUtil;
import it.greentrails.backend.utils.service.ResponseGenerator;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;

  @Value("${jwt.expiration-ms:3600000}")
  private long jwtExpirationMs;

  @PostMapping("login")
  public ResponseEntity<Object> login(
      @RequestBody final LoginRequest request,
      final HttpServletResponse response) {
    try {
      final Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.email(), request.password()));

      final Utente utente = (Utente) authentication.getPrincipal();
      final String token = jwtUtil.generateToken(utente.getId(),
          utente.getEmail(), utente.getNome(),
          utente.getCognome(), utente.getRuolo().name());
      final Map<String, Object> safeUser = Map.of(
          "id", utente.getId(),
          "nome", utente.getNome(),
          "cognome", utente.getCognome(),
          "email", utente.getEmail(),
          "ruolo", utente.getRuolo().name());

      final long maxAgeSeconds = jwtExpirationMs / 1000;
      final ResponseCookie cookie = ResponseCookie.from("token", token)
          .httpOnly(true)
          .secure(true)
          .sameSite("Strict")
          .path("/")
          .maxAge(maxAgeSeconds)
          .build();
      response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

      return ResponseGenerator.generateResponse(HttpStatus.OK,
          Map.of("utente", safeUser));
    } catch (AuthenticationException e) {
      return ResponseGenerator.generateResponse(HttpStatus.UNAUTHORIZED,
          "Credenziali non valide.");
    }
  }

  @PostMapping("logout")
  public ResponseEntity<Object> logout(final HttpServletResponse response) {
    final ResponseCookie cookie = ResponseCookie.from("token", "")
        .httpOnly(true)
        .secure(true)
        .sameSite("Strict")
        .path("/")
        .maxAge(0)
        .build();
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

    return ResponseGenerator.generateResponse(HttpStatus.OK, "Logout effettuato.");
  }

  public record LoginRequest(String email, String password) { }
}
