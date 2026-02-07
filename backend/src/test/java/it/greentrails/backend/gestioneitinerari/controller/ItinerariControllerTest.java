package it.greentrails.backend.gestioneitinerari.controller;

import it.greentrails.backend.entities.Itinerario;
import it.greentrails.backend.entities.Preferenze;
import it.greentrails.backend.entities.PrenotazioneAlloggio;
import it.greentrails.backend.entities.PrenotazioneAttivitaTuristica;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.enums.RuoloUtente;
import it.greentrails.backend.enums.StatoItinerario;
import it.greentrails.backend.gestioneitinerari.service.ItinerariService;
import it.greentrails.backend.gestioneprenotazioni.service.PrenotazioneAlloggioService;
import it.greentrails.backend.gestioneprenotazioni.service.PrenotazioneAttivitaTuristicaService;
import it.greentrails.backend.gestioneutenze.service.GestioneUtenzeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ItinerariControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ItinerariService itinerariService;

  @MockBean
  private PrenotazioneAttivitaTuristicaService prenotazioneAttivitaTuristicaService;

  @MockBean
  private PrenotazioneAlloggioService prenotazioneAlloggioService;

  @MockBean
  private GestioneUtenzeService gestioneUtenzeService;

  private Utente visitatore;
  private Itinerario itinerario;
  private Preferenze preferenze;
  private List<PrenotazioneAlloggio> prenotazioniAlloggio;
  private List<PrenotazioneAttivitaTuristica> prenotazioniAttivita;

  @BeforeEach
  void setUp() {
    visitatore = new Utente();
    visitatore.setId(1L);
    visitatore.setEmail("visitatore@test.com");
    visitatore.setRuolo(RuoloUtente.VISITATORE);

    itinerario = new Itinerario();
    itinerario.setId(1L);
    itinerario.setVisitatore(visitatore);
    itinerario.setStato(StatoItinerario.PIANIFICATO);
    itinerario.setTotale(500.0);

    preferenze = new Preferenze();
    preferenze.setId(1L);
    preferenze.setVisitatore(visitatore);

    prenotazioniAlloggio = new ArrayList<>();
    prenotazioniAttivita = new ArrayList<>();
  }

  @Test
  void testCreaItinerario_Success() throws Exception {
    when(itinerariService.saveItinerario(any(Itinerario.class))).thenReturn(itinerario);

    mockMvc.perform(post("/api/itinerari")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(itinerariService).saveItinerario(any(Itinerario.class));
  }

  @Test
  void testCreaItinerario_Exception() throws Exception {
    when(itinerariService.saveItinerario(any(Itinerario.class)))
        .thenThrow(new Exception("Errore DB"));

    mockMvc.perform(post("/api/itinerari")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(itinerariService).saveItinerario(any(Itinerario.class));
  }

  @Test
  void testGeneraItinerario_Success() throws Exception {
    when(gestioneUtenzeService.getPreferenzeById(1L)).thenReturn(preferenze);
    when(itinerariService.createByPreferenze(preferenze)).thenReturn(itinerario);

    mockMvc.perform(post("/api/itinerari/genera")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(gestioneUtenzeService).getPreferenzeById(1L);
    verify(itinerariService).createByPreferenze(preferenze);
  }

  @Test
  void testGeneraItinerario_Exception() throws Exception {
    when(gestioneUtenzeService.getPreferenzeById(1L))
        .thenThrow(new Exception("Preferenze non trovate"));

    mockMvc.perform(post("/api/itinerari/genera")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(gestioneUtenzeService).getPreferenzeById(1L);
  }

  @Test
  void testVisualizzaItinerario_Success() throws Exception {
    when(itinerariService.findById(1L)).thenReturn(itinerario);
    when(prenotazioneAlloggioService.getPrenotazioniByItinerario(itinerario))
        .thenReturn(prenotazioniAlloggio);
    when(prenotazioneAttivitaTuristicaService.getPrenotazioniByItinerario(itinerario))
        .thenReturn(prenotazioniAttivita);

    mockMvc.perform(get("/api/itinerari/1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(itinerariService).findById(1L);
    verify(prenotazioneAlloggioService).getPrenotazioniByItinerario(itinerario);
    verify(prenotazioneAttivitaTuristicaService).getPrenotazioniByItinerario(itinerario);
  }

  @Test
  void testVisualizzaItinerario_AltroUtente_NotFound() throws Exception {
    Utente altroVisitatore = new Utente();
    altroVisitatore.setId(2L);
    altroVisitatore.setEmail("altro@test.com");
    altroVisitatore.setRuolo(RuoloUtente.VISITATORE);

    when(itinerariService.findById(1L)).thenReturn(itinerario);

    mockMvc.perform(get("/api/itinerari/1")
            .with(user(altroVisitatore))
            .with(csrf()))
        .andExpect(status().isNotFound());

    verify(itinerariService).findById(1L);
    verify(prenotazioneAlloggioService, never()).getPrenotazioniByItinerario(any());
    verify(prenotazioneAttivitaTuristicaService, never()).getPrenotazioniByItinerario(any());
  }

  @Test
  void testVisualizzaItinerario_Exception() throws Exception {
    when(itinerariService.findById(999L))
        .thenThrow(new Exception("Itinerario non trovato"));

    mockMvc.perform(get("/api/itinerari/999")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(itinerariService).findById(999L);
  }

  @Test
  void testCancellaItinerario_Success() throws Exception {
    when(itinerariService.findById(1L)).thenReturn(itinerario);
    when(itinerariService.deleteItinerario(itinerario)).thenReturn(true);

    mockMvc.perform(delete("/api/itinerari/1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(itinerariService).findById(1L);
    verify(itinerariService).deleteItinerario(itinerario);
  }

  @Test
  void testCancellaItinerario_AltroUtente_NotFound() throws Exception {
    Utente altroVisitatore = new Utente();
    altroVisitatore.setId(2L);
    altroVisitatore.setEmail("altro@test.com");
    altroVisitatore.setRuolo(RuoloUtente.VISITATORE);

    when(itinerariService.findById(1L)).thenReturn(itinerario);

    mockMvc.perform(delete("/api/itinerari/1")
            .with(user(altroVisitatore))
            .with(csrf()))
        .andExpect(status().isNotFound());

    verify(itinerariService).findById(1L);
    verify(itinerariService, never()).deleteItinerario(any());
  }

  @Test
  void testCancellaItinerario_Exception() throws Exception {
    when(itinerariService.findById(1L))
        .thenThrow(new Exception("Itinerario non trovato"));

    mockMvc.perform(delete("/api/itinerari/1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(itinerariService).findById(1L);
  }
}