package it.greentrails.backend.gestioneutenze.security;

import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.enums.RuoloUtente;
import it.greentrails.backend.gestioneutenze.service.GestioneUtenzeService;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JwtAuthenticationFilterTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private JwtUtil jwtUtil;

  @MockBean
  private GestioneUtenzeService utenteService;

  private Utente utente;
  private String validToken;

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

    validToken = jwtUtil.generateToken(utente.getId(), utente.getEmail(),
        utente.getNome(), utente.getCognome(), utente.getRuolo().name());

    when(utenteService.loadUserByUsername("mario@test.it")).thenReturn(utente);
    when(utenteService.findByEmail("mario@test.it")).thenReturn(Optional.of(utente));
  }

  @Test
  void request_withBearerHeader_authenticatesUser() throws Exception {
    mockMvc.perform(get("/api/utenti")
            .header("Authorization", "Bearer " + validToken))
        .andExpect(status().isOk());
  }

  @Test
  void request_withTokenCookie_authenticatesUser() throws Exception {
    mockMvc.perform(get("/api/utenti")
            .cookie(new jakarta.servlet.http.Cookie("token", validToken)))
        .andExpect(status().isOk());
  }

  @Test
  void request_withInvalidToken_returnsForbidden() throws Exception {
    mockMvc.perform(get("/api/utenti")
            .header("Authorization", "Bearer invalid.token.here"))
        .andExpect(status().isForbidden());
  }

  @Test
  void request_withNoAuth_returnsForbidden() throws Exception {
    mockMvc.perform(get("/api/utenti"))
        .andExpect(status().isForbidden());
  }
}
