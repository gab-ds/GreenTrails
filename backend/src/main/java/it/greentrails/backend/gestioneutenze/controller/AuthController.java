package it.greentrails.backend.gestioneutenze.controller;

import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.gestioneutenze.security.JwtUtil;
import it.greentrails.backend.utils.service.ResponseGenerator;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

  @PostMapping("login")
  public ResponseEntity<Object> login(@RequestBody final LoginRequest request) {
    try {
      final Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.email(), request.password()));

      final Utente utente = (Utente) authentication.getPrincipal();
      final String token = jwtUtil.generateToken(utente.getId(),
          utente.getEmail(), utente.getNome(),
          utente.getCognome(), utente.getRuolo().name());

      return ResponseGenerator.generateResponse(HttpStatus.OK,
          Map.of("token", token, "utente", utente));
    } catch (AuthenticationException e) {
      return ResponseGenerator.generateResponse(HttpStatus.UNAUTHORIZED,
          "Credenziali non valide.");
    }
  }

  public record LoginRequest(String email, String password) { }
}
