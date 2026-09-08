package it.greentrails.backend.gestioneutenze.controller;

import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.enums.RuoloUtente;
import it.greentrails.backend.gestioneutenze.security.JwtUtil;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private JwtUtil jwtUtil;

  @MockitoBean
  private AuthenticationManager authenticationManager;

  private Utente utente;

  @BeforeEach
  void setUp() {
    utente = new Utente();
    utente.setId(1L);
    utente.setNome("Mario");
    utente.setCognome("Rossi");
    utente.setEmail("mario@test.it");
    utente.setPassword("encodedPassword");
    utente.setDataNascita(new Date());
    utente.setRuolo(RuoloUtente.VISITATORE);
  }

  @Test
  void login_validCredentials_returnsOkWithUser() throws Exception {
    final Authentication auth = mock(Authentication.class);
    when(auth.getPrincipal()).thenReturn(utente);
    when(authenticationManager.authenticate(any())).thenReturn(auth);

    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"email":"mario@test.it","password":"password123"}
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("success"))
        .andExpect(jsonPath("$.data.utente.nome").value("Mario"))
        .andExpect(jsonPath("$.data.utente.cognome").value("Rossi"))
        .andExpect(jsonPath("$.data.utente.email").value("mario@test.it"))
        .andExpect(jsonPath("$.data.utente.ruolo").value("VISITATORE"))
        .andExpect(jsonPath("$.data.utente.id").value(1));
  }

  @Test
  void login_validCredentials_setsHttpOnlyCookie() throws Exception {
    final Authentication auth = mock(Authentication.class);
    when(auth.getPrincipal()).thenReturn(utente);
    when(authenticationManager.authenticate(any())).thenReturn(auth);

    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"email":"mario@test.it","password":"password123"}
                """))
        .andExpect(status().isOk())
        .andExpect(result -> {
          final String setCookie = result.getResponse().getHeader("Set-Cookie");
          assert setCookie != null;
          assert setCookie.contains("token=");
          assert setCookie.contains("HttpOnly");
          assert setCookie.contains("Secure");
          assert setCookie.contains("SameSite=Strict");
          assert setCookie.contains("Path=/");
        });
  }

  @Test
  void login_validCredentials_tokenNotInBody() throws Exception {
    final Authentication auth = mock(Authentication.class);
    when(auth.getPrincipal()).thenReturn(utente);
    when(authenticationManager.authenticate(any())).thenReturn(auth);

    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"email":"mario@test.it","password":"password123"}
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.token").doesNotExist());
  }

  @Test
  void login_invalidCredentials_returnsUnauthorized() throws Exception {
    when(authenticationManager.authenticate(any()))
        .thenThrow(new BadCredentialsException("Bad credentials"));

    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"email":"mario@test.it","password":"wrongpassword"}
                """))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status").value("failure"))
        .andExpect(jsonPath("$.data").value("Credenziali non valide."));
  }

  @Test
  void logout_returnsOk() throws Exception {
    mockMvc.perform(post("/api/auth/logout")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("success"))
        .andExpect(jsonPath("$.data").value("Logout effettuato."));
  }

  @Test
  void logout_clearsCookie() throws Exception {
    mockMvc.perform(post("/api/auth/logout")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(result -> {
          final String setCookie = result.getResponse().getHeader("Set-Cookie");
          assert setCookie != null;
          assert setCookie.contains("token=");
          assert setCookie.contains("Max-Age=0");
          assert setCookie.contains("HttpOnly");
          assert setCookie.contains("Secure");
          assert setCookie.contains("SameSite=Strict");
        });
  }
}
