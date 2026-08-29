package it.greentrails.backend.gestioneutenze.security;

import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.gestioneutenze.service.GestioneUtenzeService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final GestioneUtenzeService utenteService;

  @Override
  protected void doFilterInternal(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain filterChain
  ) throws ServletException, IOException {

    final String header = request.getHeader("Authorization");

    if (header != null && header.startsWith("Bearer ")) {
      final String token = header.substring(7);

      if (jwtUtil.validateToken(token)) {
        final String email = jwtUtil.extractEmail(token);
        final String ruolo = jwtUtil.extractRuolo(token);

        final Utente utente = (Utente) utenteService.loadUserByUsername(email);
        final var authorities = new java.util.ArrayList<SimpleGrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + ruolo));

        final var authentication = new UsernamePasswordAuthenticationToken(
            utente, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    }

    filterChain.doFilter(request, response);
  }
}
