package it.greentrails.backend.gestioneutenze.controller;

import it.greentrails.backend.entities.Preferenze;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.enums.*;
import it.greentrails.backend.gestioneutenze.service.GestioneUtenzeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GestioneUtenzeControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private GestioneUtenzeService gestioneUtenzeService;

  @MockBean
  private PasswordEncoder passwordEncoder;

  private Utente visitatore;
  private Utente gestore;
  private Preferenze preferenze;

  @BeforeEach
  void setUp() {
    visitatore = new Utente();
    visitatore.setId(1L);
    visitatore.setNome("Mario");
    visitatore.setCognome("Rossi");
    visitatore.setEmail("mario.rossi@test.com");
    visitatore.setPassword("encodedPassword");
    visitatore.setDataNascita(new Date());
    visitatore.setRuolo(RuoloUtente.VISITATORE);

    gestore = new Utente();
    gestore.setId(2L);
    gestore.setNome("Luigi");
    gestore.setCognome("Verdi");
    gestore.setEmail("luigi.verdi@test.com");
    gestore.setPassword("encodedPassword");
    gestore.setDataNascita(new Date());
    gestore.setRuolo(RuoloUtente.GESTORE_ATTIVITA);

    preferenze = new Preferenze();
    preferenze.setId(1L);
    preferenze.setVisitatore(visitatore);
    preferenze.setViaggioPreferito(PreferenzeViaggio.MONTAGNA);
    preferenze.setAlloggioPreferito(PreferenzeAlloggio.HOTEL);
    preferenze.setAttivitaPreferita(PreferenzeAttivita.ALL_APERTO);
    preferenze.setPreferenzaAlimentare(PreferenzeAlimentari.NESSUNA_PREFERENZA);
    preferenze.setAnimaleDomestico(false);
    preferenze.setBudgetPreferito(PreferenzeBudget.MEDIO);
    preferenze.setSouvenir(true);
    preferenze.setStagioniPreferite(PreferenzeStagione.PRIMAVERA_ESTATE);
  }

  @Test
  void testRegistrazione_VisitatorSuccess() throws Exception {
    String utenteJson = """
        {
          "nome": "Carlo",
          "cognome": "Bianchi",
          "email": "carlo.bianchi@test.com",
          "password": "password123",
          "dataNascita": "1990-01-01"
        }
        """;

    when(gestioneUtenzeService.findByEmail("carlo.bianchi@test.com"))
        .thenReturn(Optional.empty());
    when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
    when(gestioneUtenzeService.saveUtente(any(Utente.class))).thenReturn(visitatore);

    mockMvc.perform(put("/api/utenti")
            .param("isGestore", "false")
            .contentType(MediaType.APPLICATION_JSON)
            .content(utenteJson)
            .with(csrf()))
        .andExpect(status().isOk());

    verify(gestioneUtenzeService).findByEmail("carlo.bianchi@test.com");
    verify(passwordEncoder).encode("password123");

    ArgumentCaptor<Utente> utenteCaptor = ArgumentCaptor.forClass(Utente.class);
    verify(gestioneUtenzeService).saveUtente(utenteCaptor.capture());

    Utente utenteSalvato = utenteCaptor.getValue();
    assertEquals("encodedPassword", utenteSalvato.getPassword());
    assertEquals(RuoloUtente.VISITATORE, utenteSalvato.getRuolo());
  }

  @Test
  void testRegistrazione_GestoreSuccess() throws Exception {
    String gestoreJson = """
        {
          "nome": "Paolo",
          "cognome": "Neri",
          "email": "paolo.neri@test.com",
          "password": "password123",
          "dataNascita": "1985-05-15"
        }
        """;

    when(gestioneUtenzeService.findByEmail("paolo.neri@test.com"))
        .thenReturn(Optional.empty());
    when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
    when(gestioneUtenzeService.saveUtente(any(Utente.class))).thenReturn(gestore);

    mockMvc.perform(put("/api/utenti")
            .param("isGestore", "true")
            .contentType(MediaType.APPLICATION_JSON)
            .content(gestoreJson)
            .with(csrf()))
        .andExpect(status().isOk());

    verify(gestioneUtenzeService).findByEmail("paolo.neri@test.com");
    verify(passwordEncoder).encode("password123");

    ArgumentCaptor<Utente> utenteCaptor = ArgumentCaptor.forClass(Utente.class);
    verify(gestioneUtenzeService).saveUtente(utenteCaptor.capture());

    Utente utenteSalvato = utenteCaptor.getValue();
    assertEquals("encodedPassword", utenteSalvato.getPassword());
    assertEquals(RuoloUtente.GESTORE_ATTIVITA, utenteSalvato.getRuolo());
  }

  @Test
  void testRegistrazione_UtenteGiaEsistente() throws Exception {
    String utenteJson = """
        {
          "nome": "Mario",
          "cognome": "Rossi",
          "email": "mario.rossi@test.com",
          "password": "password123",
          "dataNascita": "1988-03-20"
        }
        """;

    when(gestioneUtenzeService.findByEmail("mario.rossi@test.com"))
        .thenReturn(Optional.of(visitatore));

    mockMvc.perform(put("/api/utenti")
            .param("isGestore", "false")
            .contentType(MediaType.APPLICATION_JSON)
            .content(utenteJson)
            .with(csrf()))
        .andExpect(status().isBadRequest());

    verify(gestioneUtenzeService).findByEmail("mario.rossi@test.com");
    verify(gestioneUtenzeService, never()).saveUtente(any());
  }


  @Test
  void testVisualizzaPreferenze_Success() throws Exception {
    when(gestioneUtenzeService.getPreferenzeById(1L)).thenReturn(preferenze);

    mockMvc.perform(get("/api/utenti/preferenze")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(gestioneUtenzeService).getPreferenzeById(1L);
  }

  @Test
  void testVisualizzaPreferenze_Exception() throws Exception {
    when(gestioneUtenzeService.getPreferenzeById(1L))
        .thenThrow(new Exception("Preferenze non trovate"));

    mockMvc.perform(get("/api/utenti/preferenze")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(gestioneUtenzeService).getPreferenzeById(1L);
  }

  @Test
  void testCompilaQuestionario_NuovePreferenze() throws Exception {
    when(gestioneUtenzeService.getPreferenzeById(1L))
        .thenThrow(new Exception("Preferenze non esistono"));
    when(gestioneUtenzeService.savePreferenze(any(Preferenze.class))).thenReturn(preferenze);

    mockMvc.perform(post("/api/utenti/questionario")
            .param("viaggioPreferito", "MONTAGNA")
            .param("alloggioPreferito", "HOTEL")
            .param("attivitaPreferita", "ALL_APERTO")
            .param("preferenzaAlimentare", "NESSUNA_PREFERENZA")
            .param("animaleDomestico", "false")
            .param("budgetPreferito", "MEDIO")
            .param("souvenir", "true")
            .param("stagioniPreferite", "PRIMAVERA_ESTATE")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(gestioneUtenzeService).getPreferenzeById(1L);

    ArgumentCaptor<Preferenze> preferenzeCaptor = ArgumentCaptor.forClass(Preferenze.class);
    verify(gestioneUtenzeService).savePreferenze(preferenzeCaptor.capture());

    Preferenze preferenzeSalvate = preferenzeCaptor.getValue();
    assertEquals(1L, preferenzeSalvate.getId());
    assertEquals(visitatore, preferenzeSalvate.getVisitatore());
    assertEquals(PreferenzeViaggio.MONTAGNA, preferenzeSalvate.getViaggioPreferito());
    assertEquals(PreferenzeAlloggio.HOTEL, preferenzeSalvate.getAlloggioPreferito());
  }

  @Test
  void testCompilaQuestionario_AggiornaPreferenzeEsistenti() throws Exception {
    when(gestioneUtenzeService.getPreferenzeById(1L)).thenReturn(preferenze);
    when(gestioneUtenzeService.savePreferenze(any(Preferenze.class))).thenReturn(preferenze);

    mockMvc.perform(post("/api/utenti/questionario")
            .param("viaggioPreferito", "MARE")
            .param("alloggioPreferito", "BED_AND_BREAKFAST")
            .param("attivitaPreferita", "VISITE_CULTURALI_STORICHE")
            .param("preferenzaAlimentare", "VEGETARIAN")
            .param("animaleDomestico", "true")
            .param("budgetPreferito", "ALTO")
            .param("souvenir", "false")
            .param("stagioniPreferite", "AUTUNNO_INVERNO")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(gestioneUtenzeService).getPreferenzeById(1L);

    ArgumentCaptor<Preferenze> preferenzeCaptor = ArgumentCaptor.forClass(Preferenze.class);
    verify(gestioneUtenzeService).savePreferenze(preferenzeCaptor.capture());

    Preferenze preferenzeAggiornate = preferenzeCaptor.getValue();
    assertEquals(PreferenzeViaggio.MARE, preferenzeAggiornate.getViaggioPreferito());
    assertEquals(PreferenzeAlloggio.BED_AND_BREAKFAST, preferenzeAggiornate.getAlloggioPreferito());
    assertEquals(PreferenzeAttivita.VISITE_CULTURALI_STORICHE, preferenzeAggiornate.getAttivitaPreferita());
    assertEquals(PreferenzeAlimentari.VEGETARIAN, preferenzeAggiornate.getPreferenzaAlimentare());
    assertTrue(preferenzeAggiornate.isAnimaleDomestico());
    assertEquals(PreferenzeBudget.ALTO, preferenzeAggiornate.getBudgetPreferito());
    assertFalse(preferenzeAggiornate.isSouvenir());
    assertEquals(PreferenzeStagione.AUTUNNO_INVERNO, preferenzeAggiornate.getStagioniPreferite());
  }

  @Test
  void testCompilaQuestionario_Exception() throws Exception {
    when(gestioneUtenzeService.getPreferenzeById(1L)).thenReturn(preferenze);
    when(gestioneUtenzeService.savePreferenze(any(Preferenze.class)))
        .thenThrow(new Exception("Errore salvataggio"));

    mockMvc.perform(post("/api/utenti/questionario")
            .param("viaggioPreferito", "MONTAGNA")
            .param("alloggioPreferito", "HOTEL")
            .param("attivitaPreferita", "ALL_APERTO")
            .param("preferenzaAlimentare", "NESSUNA_PREFERENZA")
            .param("animaleDomestico", "false")
            .param("budgetPreferito", "MEDIO")
            .param("souvenir", "true")
            .param("stagioniPreferite", "PRIMAVERA_ESTATE")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(gestioneUtenzeService).savePreferenze(any(Preferenze.class));
  }

  @Test
  void testVisualizzaInfo_Success() throws Exception {
    when(gestioneUtenzeService.findByEmail("mario.rossi@test.com"))
        .thenReturn(Optional.of(visitatore));

    mockMvc.perform(get("/api/utenti")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(gestioneUtenzeService).findByEmail("mario.rossi@test.com");
  }

  @Test
  void testVisualizzaInfo_UtenteNonTrovato() throws Exception {
    when(gestioneUtenzeService.findByEmail("mario.rossi@test.com"))
        .thenReturn(Optional.empty());

    mockMvc.perform(get("/api/utenti")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isBadRequest());

    verify(gestioneUtenzeService).findByEmail("mario.rossi@test.com");
  }
}


