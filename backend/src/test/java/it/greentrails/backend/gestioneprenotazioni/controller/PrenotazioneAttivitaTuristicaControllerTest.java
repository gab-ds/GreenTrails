package it.greentrails.backend.gestioneprenotazioni.controller;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Itinerario;
import it.greentrails.backend.entities.PrenotazioneAttivitaTuristica;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.entities.ValoriEcosostenibilita;
import it.greentrails.backend.enums.RuoloUtente;
import it.greentrails.backend.enums.StatoItinerario;
import it.greentrails.backend.enums.StatoPagamento;
import it.greentrails.backend.enums.StatoPrenotazione;
import it.greentrails.backend.gestioneattivita.service.AttivitaService;
import it.greentrails.backend.gestioneitinerari.service.ItinerariService;
import it.greentrails.backend.gestioneprenotazioni.service.PrenotazioneAttivitaTuristicaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PrenotazioneAttivitaTuristicaControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ItinerariService itinerariService;

  @MockBean
  private AttivitaService attivitaService;

  @MockBean
  private PrenotazioneAttivitaTuristicaService prenotazioneAttivitaTuristicaService;

  private Utente visitatore;
  private Utente gestore;
  private Itinerario itinerario;
  private Attivita attivitaTuristica;
  private PrenotazioneAttivitaTuristica prenotazioneAttivitaTuristica;
  private ValoriEcosostenibilita valori;

  @BeforeEach
  void setUp() {
    visitatore = new Utente();
    visitatore.setId(1L);
    visitatore.setEmail("visitatore@test.com");
    visitatore.setRuolo(RuoloUtente.VISITATORE);

    gestore = new Utente();
    gestore.setId(2L);
    gestore.setEmail("gestore@test.com");
    gestore.setRuolo(RuoloUtente.GESTORE_ATTIVITA);

    itinerario = new Itinerario();
    itinerario.setId(1L);
    itinerario.setVisitatore(visitatore);
    itinerario.setStato(StatoItinerario.PIANIFICATO);
    itinerario.setTotale(100.0);

    valori = new ValoriEcosostenibilita();
    valori.setId(1L);

    attivitaTuristica = new Attivita();
    attivitaTuristica.setId(1L);
    attivitaTuristica.setGestore(gestore);
    attivitaTuristica.setNome("Tour Verde");
    attivitaTuristica.setAlloggio(false);
    attivitaTuristica.setPrezzo(50.0);
    attivitaTuristica.setDisponibilita(20);
    attivitaTuristica.setValoriEcosostenibilita(valori);

    Date dataInizio = new Date();
    Date dataFine = new Date(dataInizio.getTime() + 86400000); // +1 giorno

    prenotazioneAttivitaTuristica = new PrenotazioneAttivitaTuristica();
    prenotazioneAttivitaTuristica.setId(1L);
    prenotazioneAttivitaTuristica.setItinerario(itinerario);
    prenotazioneAttivitaTuristica.setAttivitaTuristica(attivitaTuristica);
    prenotazioneAttivitaTuristica.setNumAdulti(2);
    prenotazioneAttivitaTuristica.setNumBambini(0);
    prenotazioneAttivitaTuristica.setDataInizio(dataInizio);
    prenotazioneAttivitaTuristica.setDataFine(dataFine);
    prenotazioneAttivitaTuristica.setStato(StatoPrenotazione.CREATA);
    prenotazioneAttivitaTuristica.setStatoPagamento(StatoPagamento.IN_CORSO);
    prenotazioneAttivitaTuristica.setPrezzo(100.0);
  }

  @Test
  void testCreaPrenotazioneAttivitaTuristica_Success() throws Exception {
    when(itinerariService.findById(1L)).thenReturn(itinerario);
    when(attivitaService.findById(1L)).thenReturn(attivitaTuristica);
    when(prenotazioneAttivitaTuristicaService.controllaDisponibilitaAttivitaTuristica(
        eq(attivitaTuristica), any(Date.class))).thenReturn(20);
    when(prenotazioneAttivitaTuristicaService.savePrenotazioneAttivitaTuristica(
        eq(attivitaTuristica), any(PrenotazioneAttivitaTuristica.class)))
        .thenReturn(prenotazioneAttivitaTuristica);
    when(itinerariService.saveItinerario(any(Itinerario.class))).thenReturn(itinerario);

    mockMvc.perform(post("/api/prenotazioni-attivita-turistica")
            .param("idItinerario", "1")
            .param("idAttivita", "1")
            .param("numAdulti", "2")
            .param("numBambini", "0")
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-02")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(itinerariService).findById(1L);
    verify(attivitaService).findById(1L);
    verify(prenotazioneAttivitaTuristicaService).savePrenotazioneAttivitaTuristica(
        eq(attivitaTuristica), any(PrenotazioneAttivitaTuristica.class));
  }

  @Test
  void testCreaPrenotazioneAttivitaTuristica_ItinerarioNonTrovato() throws Exception {
    Utente altroVisitatore = new Utente();
    altroVisitatore.setId(2L);
    altroVisitatore.setEmail("altro@test.com");
    altroVisitatore.setRuolo(RuoloUtente.VISITATORE);

    when(itinerariService.findById(1L)).thenReturn(itinerario);

    mockMvc.perform(post("/api/prenotazioni-attivita-turistica")
            .param("idItinerario", "1")
            .param("idAttivita", "1")
            .param("numAdulti", "2")
            .param("numBambini", "0")
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-02")
            .with(user(altroVisitatore))
            .with(csrf()))
        .andExpect(status().isNotFound());

    verify(itinerariService).findById(1L);
    verify(attivitaService, never()).findById(any());
  }

  @Test
  void testCreaPrenotazioneAttivitaTuristica_AttivitaEAlloggio() throws Exception {
    attivitaTuristica.setAlloggio(true);

    when(itinerariService.findById(1L)).thenReturn(itinerario);
    when(attivitaService.findById(1L)).thenReturn(attivitaTuristica);

    mockMvc.perform(post("/api/prenotazioni-attivita-turistica")
            .param("idItinerario", "1")
            .param("idAttivita", "1")
            .param("numAdulti", "2")
            .param("numBambini", "0")
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-02")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(itinerariService).findById(1L);
    verify(attivitaService).findById(1L);
    verify(prenotazioneAttivitaTuristicaService, never())
        .savePrenotazioneAttivitaTuristica(any(), any());
  }

  @Test
  void testCreaPrenotazioneAttivitaTuristica_AttivitaNonDisponibile() throws Exception {
    when(itinerariService.findById(1L)).thenReturn(itinerario);
    when(attivitaService.findById(1L)).thenReturn(attivitaTuristica);
    when(prenotazioneAttivitaTuristicaService.controllaDisponibilitaAttivitaTuristica(
        eq(attivitaTuristica), any(Date.class))).thenReturn(1);

    mockMvc.perform(post("/api/prenotazioni-attivita-turistica")
            .param("idItinerario", "1")
            .param("idAttivita", "1")
            .param("numAdulti", "2")
            .param("numBambini", "0")
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-02")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isBadRequest());

    verify(prenotazioneAttivitaTuristicaService, never())
        .savePrenotazioneAttivitaTuristica(any(), any());
  }

  @Test
  void testCreaPrenotazioneAttivitaTuristica_DataFineAntecedente() throws Exception {
    when(itinerariService.findById(1L)).thenReturn(itinerario);
    when(attivitaService.findById(1L)).thenReturn(attivitaTuristica);
    when(prenotazioneAttivitaTuristicaService.controllaDisponibilitaAttivitaTuristica(
        eq(attivitaTuristica), any(Date.class))).thenReturn(20);

    mockMvc.perform(post("/api/prenotazioni-attivita-turistica")
            .param("idItinerario", "1")
            .param("idAttivita", "1")
            .param("numAdulti", "2")
            .param("numBambini", "0")
            .param("dataInizio", "2026-03-05")
            .param("dataFine", "2026-03-01") // dataFine < dataInizio
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isBadRequest());

    verify(prenotazioneAttivitaTuristicaService, never())
        .savePrenotazioneAttivitaTuristica(any(), any());
  }

  @Test
  void testCreaPrenotazioneAttivitaTuristica_SenzaDataFine() throws Exception {
    when(itinerariService.findById(1L)).thenReturn(itinerario);
    when(attivitaService.findById(1L)).thenReturn(attivitaTuristica);
    when(prenotazioneAttivitaTuristicaService.controllaDisponibilitaAttivitaTuristica(
        eq(attivitaTuristica), any(Date.class))).thenReturn(20);
    when(prenotazioneAttivitaTuristicaService.savePrenotazioneAttivitaTuristica(
        eq(attivitaTuristica), any(PrenotazioneAttivitaTuristica.class)))
        .thenReturn(prenotazioneAttivitaTuristica);
    when(itinerariService.saveItinerario(any(Itinerario.class))).thenReturn(itinerario);

    mockMvc.perform(post("/api/prenotazioni-attivita-turistica")
            .param("idItinerario", "1")
            .param("idAttivita", "1")
            .param("numAdulti", "2")
            .param("numBambini", "0")
            .param("dataInizio", "2026-03-01")
            // dataFine omessa
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(itinerariService).findById(1L);
    verify(attivitaService).findById(1L);
    verify(prenotazioneAttivitaTuristicaService).savePrenotazioneAttivitaTuristica(
        eq(attivitaTuristica), any(PrenotazioneAttivitaTuristica.class));
  }

  @Test
  void testCreaPrenotazioneAttivitaTuristica_SenzaNumBambini() throws Exception {
    when(itinerariService.findById(1L)).thenReturn(itinerario);
    when(attivitaService.findById(1L)).thenReturn(attivitaTuristica);
    when(prenotazioneAttivitaTuristicaService.controllaDisponibilitaAttivitaTuristica(
        eq(attivitaTuristica), any(Date.class))).thenReturn(20);
    when(prenotazioneAttivitaTuristicaService.savePrenotazioneAttivitaTuristica(
        eq(attivitaTuristica), any(PrenotazioneAttivitaTuristica.class)))
        .thenReturn(prenotazioneAttivitaTuristica);
    when(itinerariService.saveItinerario(any(Itinerario.class))).thenReturn(itinerario);

    mockMvc.perform(post("/api/prenotazioni-attivita-turistica")
            .param("idItinerario", "1")
            .param("idAttivita", "1")
            .param("numAdulti", "2")
            // numBambini omesso - dovrebbe usare default = 0
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-02")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(prenotazioneAttivitaTuristicaService).savePrenotazioneAttivitaTuristica(
        eq(attivitaTuristica), any(PrenotazioneAttivitaTuristica.class));
  }

  @Test
  void testCreaPrenotazioneAttivitaTuristica_DurataPiuDi24Ore() throws Exception {
    when(itinerariService.findById(1L)).thenReturn(itinerario);
    when(attivitaService.findById(1L)).thenReturn(attivitaTuristica);
    when(prenotazioneAttivitaTuristicaService.controllaDisponibilitaAttivitaTuristica(
        eq(attivitaTuristica), any(Date.class))).thenReturn(20);
    when(prenotazioneAttivitaTuristicaService.savePrenotazioneAttivitaTuristica(
        eq(attivitaTuristica), any(PrenotazioneAttivitaTuristica.class)))
        .thenReturn(prenotazioneAttivitaTuristica);
    when(itinerariService.saveItinerario(any(Itinerario.class))).thenReturn(itinerario);

    mockMvc.perform(post("/api/prenotazioni-attivita-turistica")
            .param("idItinerario", "1")
            .param("idAttivita", "1")
            .param("numAdulti", "2")
            .param("numBambini", "0")
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-05") // 4 giorni
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(prenotazioneAttivitaTuristicaService).savePrenotazioneAttivitaTuristica(
        eq(attivitaTuristica), any(PrenotazioneAttivitaTuristica.class));
  }

  @Test
  void testCreaPrenotazioneAttivitaTuristica_Exception() throws Exception {
    when(itinerariService.findById(1L))
        .thenThrow(new Exception("Errore DB"));

    mockMvc.perform(post("/api/prenotazioni-attivita-turistica")
            .param("idItinerario", "1")
            .param("idAttivita", "1")
            .param("numAdulti", "2")
            .param("numBambini", "0")
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-02")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(itinerariService).findById(1L);
  }

  @Test
  void testConfermaPrenotazioneAttivitaTuristica_Success() throws Exception {
    prenotazioneAttivitaTuristica.setStato(StatoPrenotazione.NON_CONFERMATA);
    when(prenotazioneAttivitaTuristicaService.findById(1L))
        .thenReturn(prenotazioneAttivitaTuristica);
    when(prenotazioneAttivitaTuristicaService.controllaDisponibilitaAttivitaTuristica(
        eq(attivitaTuristica), any(Date.class))).thenReturn(20);
    when(prenotazioneAttivitaTuristicaService.savePrenotazioneAttivitaTuristica(
        eq(attivitaTuristica), any(PrenotazioneAttivitaTuristica.class)))
        .thenReturn(prenotazioneAttivitaTuristica);
    when(itinerariService.saveItinerario(any(Itinerario.class))).thenReturn(itinerario);

    mockMvc.perform(post("/api/prenotazioni-attivita-turistica/1")
            .param("numAdulti", "2")
            .param("numBambini", "0")
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-02")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(prenotazioneAttivitaTuristicaService).findById(1L);
    verify(prenotazioneAttivitaTuristicaService).savePrenotazioneAttivitaTuristica(
        eq(attivitaTuristica), any(PrenotazioneAttivitaTuristica.class));
  }

  @Test
  void testConfermaPrenotazioneAttivitaTuristica_PrenotazioneNonTrovata() throws Exception {
    Utente altroVisitatore = new Utente();
    altroVisitatore.setId(2L);
    altroVisitatore.setEmail("altro@test.com");
    altroVisitatore.setRuolo(RuoloUtente.VISITATORE);

    when(prenotazioneAttivitaTuristicaService.findById(1L))
        .thenReturn(prenotazioneAttivitaTuristica);

    mockMvc.perform(post("/api/prenotazioni-attivita-turistica/1")
            .param("numAdulti", "2")
            .param("numBambini", "0")
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-02")
            .with(user(altroVisitatore))
            .with(csrf()))
        .andExpect(status().isNotFound());

    verify(prenotazioneAttivitaTuristicaService).findById(1L);
  }

  @Test
  void testConfermaPrenotazioneAttivitaTuristica_PrenotazioneNonModificabile() throws Exception {
    when(prenotazioneAttivitaTuristicaService.findById(1L))
        .thenReturn(prenotazioneAttivitaTuristica);

    mockMvc.perform(post("/api/prenotazioni-attivita-turistica/1")
            .param("numAdulti", "2")
            .param("numBambini", "0")
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-02")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isBadRequest());

    verify(prenotazioneAttivitaTuristicaService).findById(1L);
    verify(prenotazioneAttivitaTuristicaService, never())
        .savePrenotazioneAttivitaTuristica(any(), any());
  }

  @Test
  void testConfermaPrenotazioneAttivitaTuristica_AttivitaNonDisponibile() throws Exception {
    prenotazioneAttivitaTuristica.setStato(StatoPrenotazione.NON_CONFERMATA);
    when(prenotazioneAttivitaTuristicaService.findById(1L))
        .thenReturn(prenotazioneAttivitaTuristica);
    when(prenotazioneAttivitaTuristicaService.controllaDisponibilitaAttivitaTuristica(
        eq(attivitaTuristica), any(Date.class))).thenReturn(1);

    mockMvc.perform(post("/api/prenotazioni-attivita-turistica/1")
            .param("numAdulti", "2")
            .param("numBambini", "0")
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-02")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isBadRequest());

    verify(prenotazioneAttivitaTuristicaService, never())
        .savePrenotazioneAttivitaTuristica(any(), any());
  }

  @Test
  void testConfermaPrenotazioneAttivitaTuristica_DataFineAntecedente() throws Exception {
    prenotazioneAttivitaTuristica.setStato(StatoPrenotazione.NON_CONFERMATA);
    when(prenotazioneAttivitaTuristicaService.findById(1L))
        .thenReturn(prenotazioneAttivitaTuristica);
    when(prenotazioneAttivitaTuristicaService.controllaDisponibilitaAttivitaTuristica(
        eq(attivitaTuristica), any(Date.class))).thenReturn(20);

    mockMvc.perform(post("/api/prenotazioni-attivita-turistica/1")
            .param("numAdulti", "2")
            .param("numBambini", "0")
            .param("dataInizio", "2026-03-05")
            .param("dataFine", "2026-03-01") // dataFine < dataInizio
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isBadRequest());

    verify(prenotazioneAttivitaTuristicaService, never())
        .savePrenotazioneAttivitaTuristica(any(), any());
  }

  @Test
  void testConfermaPrenotazioneAttivitaTuristica_SenzaDataFine() throws Exception {
    prenotazioneAttivitaTuristica.setStato(StatoPrenotazione.NON_CONFERMATA);
    when(prenotazioneAttivitaTuristicaService.findById(1L))
        .thenReturn(prenotazioneAttivitaTuristica);
    when(prenotazioneAttivitaTuristicaService.controllaDisponibilitaAttivitaTuristica(
        eq(attivitaTuristica), any(Date.class))).thenReturn(20);
    when(prenotazioneAttivitaTuristicaService.savePrenotazioneAttivitaTuristica(
        eq(attivitaTuristica), any(PrenotazioneAttivitaTuristica.class)))
        .thenReturn(prenotazioneAttivitaTuristica);
    when(itinerariService.saveItinerario(any(Itinerario.class))).thenReturn(itinerario);

    mockMvc.perform(post("/api/prenotazioni-attivita-turistica/1")
            .param("numAdulti", "2")
            .param("numBambini", "0")
            .param("dataInizio", "2026-03-01")
            // dataFine omessa
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(prenotazioneAttivitaTuristicaService).savePrenotazioneAttivitaTuristica(
        eq(attivitaTuristica), any(PrenotazioneAttivitaTuristica.class));
  }

  @Test
  void testConfermaPrenotazioneAttivitaTuristica_SenzaNumBambini() throws Exception {
    prenotazioneAttivitaTuristica.setStato(StatoPrenotazione.NON_CONFERMATA);
    when(prenotazioneAttivitaTuristicaService.findById(1L))
        .thenReturn(prenotazioneAttivitaTuristica);
    when(prenotazioneAttivitaTuristicaService.controllaDisponibilitaAttivitaTuristica(
        eq(attivitaTuristica), any(Date.class))).thenReturn(20);
    when(prenotazioneAttivitaTuristicaService.savePrenotazioneAttivitaTuristica(
        eq(attivitaTuristica), any(PrenotazioneAttivitaTuristica.class)))
        .thenReturn(prenotazioneAttivitaTuristica);
    when(itinerariService.saveItinerario(any(Itinerario.class))).thenReturn(itinerario);

    mockMvc.perform(post("/api/prenotazioni-attivita-turistica/1")
            .param("numAdulti", "2")
            // numBambini omesso
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-02")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(prenotazioneAttivitaTuristicaService).savePrenotazioneAttivitaTuristica(
        eq(attivitaTuristica), any(PrenotazioneAttivitaTuristica.class));
  }

  @Test
  void testConfermaPrenotazioneAttivitaTuristica_DurataPiuDi24Ore() throws Exception {
    prenotazioneAttivitaTuristica.setStato(StatoPrenotazione.NON_CONFERMATA);
    when(prenotazioneAttivitaTuristicaService.findById(1L))
        .thenReturn(prenotazioneAttivitaTuristica);
    when(prenotazioneAttivitaTuristicaService.controllaDisponibilitaAttivitaTuristica(
        eq(attivitaTuristica), any(Date.class))).thenReturn(20);
    when(prenotazioneAttivitaTuristicaService.savePrenotazioneAttivitaTuristica(
        eq(attivitaTuristica), any(PrenotazioneAttivitaTuristica.class)))
        .thenReturn(prenotazioneAttivitaTuristica);
    when(itinerariService.saveItinerario(any(Itinerario.class))).thenReturn(itinerario);

    mockMvc.perform(post("/api/prenotazioni-attivita-turistica/1")
            .param("numAdulti", "2")
            .param("numBambini", "0")
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-05") // 4 giorni
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(prenotazioneAttivitaTuristicaService).savePrenotazioneAttivitaTuristica(
        eq(attivitaTuristica), any(PrenotazioneAttivitaTuristica.class));
  }

  @Test
  void testConfermaPrenotazioneAttivitaTuristica_Exception() throws Exception {
    when(prenotazioneAttivitaTuristicaService.findById(1L))
        .thenThrow(new Exception("Errore DB"));

    mockMvc.perform(post("/api/prenotazioni-attivita-turistica/1")
            .param("numAdulti", "2")
            .param("numBambini", "0")
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-02")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(prenotazioneAttivitaTuristicaService).findById(1L);
  }

  @Test
  void testVisualizzaPrenotazioneAttivitaTuristica_Success() throws Exception {
    when(prenotazioneAttivitaTuristicaService.findById(1L))
        .thenReturn(prenotazioneAttivitaTuristica);

    mockMvc.perform(get("/api/prenotazioni-attivita-turistica/1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(prenotazioneAttivitaTuristicaService).findById(1L);
  }

  @Test
  void testVisualizzaPrenotazioneAttivitaTuristica_PrenotazioneNonTrovata() throws Exception {
    Utente altroVisitatore = new Utente();
    altroVisitatore.setId(2L);
    altroVisitatore.setEmail("altro@test.com");
    altroVisitatore.setRuolo(RuoloUtente.VISITATORE);

    when(prenotazioneAttivitaTuristicaService.findById(1L))
        .thenReturn(prenotazioneAttivitaTuristica);

    mockMvc.perform(get("/api/prenotazioni-attivita-turistica/1")
            .with(user(altroVisitatore))
            .with(csrf()))
        .andExpect(status().isNotFound());

    verify(prenotazioneAttivitaTuristicaService).findById(1L);
  }

  @Test
  void testVisualizzaPrenotazioneAttivitaTuristica_Exception() throws Exception {
    when(prenotazioneAttivitaTuristicaService.findById(999L))
        .thenThrow(new Exception("Prenotazione non trovata"));

    mockMvc.perform(get("/api/prenotazioni-attivita-turistica/999")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(prenotazioneAttivitaTuristicaService).findById(999L);
  }

  @Test
  void testVisualizzaPrenotazioniAttivitaTuristicaPerAttivita_Success() throws Exception {
    List<PrenotazioneAttivitaTuristica> prenotazioni = new ArrayList<>();
    prenotazioni.add(prenotazioneAttivitaTuristica);

    when(attivitaService.findById(1L)).thenReturn(attivitaTuristica);
    when(prenotazioneAttivitaTuristicaService.getPrenotazioniByAttivitaTuristica(
        attivitaTuristica)).thenReturn(prenotazioni);

    mockMvc.perform(get("/api/prenotazioni-attivita-turistica/perAttivita/1")
            .with(user(gestore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(attivitaService).findById(1L);
    verify(prenotazioneAttivitaTuristicaService)
        .getPrenotazioniByAttivitaTuristica(attivitaTuristica);
  }

  @Test
  void testVisualizzaPrenotazioniAttivitaTuristicaPerAttivita_AttivitaNonTrovata()
      throws Exception {
    Utente altroGestore = new Utente();
    altroGestore.setId(3L);
    altroGestore.setEmail("altro.gestore@test.com");
    altroGestore.setRuolo(RuoloUtente.GESTORE_ATTIVITA);

    when(attivitaService.findById(1L)).thenReturn(attivitaTuristica);

    mockMvc.perform(get("/api/prenotazioni-attivita-turistica/perAttivita/1")
            .with(user(altroGestore))
            .with(csrf()))
        .andExpect(status().isNotFound());

    verify(attivitaService).findById(1L);
    verify(prenotazioneAttivitaTuristicaService, never())
        .getPrenotazioniByAttivitaTuristica(any());
  }

  @Test
  void testVisualizzaPrenotazioniAttivitaTuristicaPerAttivita_Exception() throws Exception {
    when(attivitaService.findById(1L))
        .thenThrow(new Exception("Attività non trovata"));

    mockMvc.perform(get("/api/prenotazioni-attivita-turistica/perAttivita/1")
            .with(user(gestore))
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(attivitaService).findById(1L);
  }

  @Test
  void testVisualizzaDisponibilitaPerAttivitaTuristica_Success() throws Exception {
    when(attivitaService.findById(1L)).thenReturn(attivitaTuristica);
    when(prenotazioneAttivitaTuristicaService.controllaDisponibilitaAttivitaTuristica(
        eq(attivitaTuristica), any(Date.class))).thenReturn(20);

    mockMvc.perform(get("/api/prenotazioni-attivita-turistica/perAttivita/1/disponibilita")
            .param("dataInizio", "2026-03-01")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(attivitaService).findById(1L);
    verify(prenotazioneAttivitaTuristicaService).controllaDisponibilitaAttivitaTuristica(
        eq(attivitaTuristica), any(Date.class));
  }

  @Test
  void testVisualizzaDisponibilitaPerAttivitaTuristica_Exception() throws Exception {
    when(attivitaService.findById(1L))
        .thenThrow(new Exception("Attività non trovata"));

    mockMvc.perform(get("/api/prenotazioni-attivita-turistica/perAttivita/1/disponibilita")
            .param("dataInizio", "2026-03-01")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(attivitaService).findById(1L);
  }

  @Test
  void testVisualizzaPrenotazioniAttivitaTuristicaPerVisitatore_Success() throws Exception {
    List<PrenotazioneAttivitaTuristica> prenotazioni = new ArrayList<>();
    prenotazioni.add(prenotazioneAttivitaTuristica);

    when(prenotazioneAttivitaTuristicaService.getPrenotazioniByVisitatore(visitatore))
        .thenReturn(prenotazioni);

    mockMvc.perform(get("/api/prenotazioni-attivita-turistica")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(prenotazioneAttivitaTuristicaService).getPrenotazioniByVisitatore(visitatore);
  }

  @Test
  void testVisualizzaPrenotazioniAttivitaTuristicaPerVisitatore_Exception() throws Exception {
    when(prenotazioneAttivitaTuristicaService.getPrenotazioniByVisitatore(visitatore))
        .thenThrow(new Exception("Errore DB"));

    mockMvc.perform(get("/api/prenotazioni-attivita-turistica")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(prenotazioneAttivitaTuristicaService).getPrenotazioniByVisitatore(visitatore);
  }

  @Test
  void testCancellaPrenotazioneAttivitaTuristica_Success() throws Exception {
    when(prenotazioneAttivitaTuristicaService.findById(1L))
        .thenReturn(prenotazioneAttivitaTuristica);
    when(prenotazioneAttivitaTuristicaService.deletePrenotazioneAttivitaTuristica(
        prenotazioneAttivitaTuristica)).thenReturn(true);
    when(itinerariService.saveItinerario(any(Itinerario.class))).thenReturn(itinerario);

    mockMvc.perform(delete("/api/prenotazioni-attivita-turistica/1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(prenotazioneAttivitaTuristicaService).findById(1L);
    verify(prenotazioneAttivitaTuristicaService)
        .deletePrenotazioneAttivitaTuristica(prenotazioneAttivitaTuristica);
    verify(itinerariService).saveItinerario(any(Itinerario.class));
  }

  @Test
  void testCancellaPrenotazioneAttivitaTuristica_PrenotazioneNonTrovata() throws Exception {
    Utente altroVisitatore = new Utente();
    altroVisitatore.setId(2L);
    altroVisitatore.setEmail("altro@test.com");
    altroVisitatore.setRuolo(RuoloUtente.VISITATORE);

    when(prenotazioneAttivitaTuristicaService.findById(1L))
        .thenReturn(prenotazioneAttivitaTuristica);

    mockMvc.perform(delete("/api/prenotazioni-attivita-turistica/1")
            .with(user(altroVisitatore))
            .with(csrf()))
        .andExpect(status().isNotFound());

    verify(prenotazioneAttivitaTuristicaService).findById(1L);
    verify(prenotazioneAttivitaTuristicaService, never())
        .deletePrenotazioneAttivitaTuristica(any());
  }

  @Test
  void testCancellaPrenotazioneAttivitaTuristica_Exception() throws Exception {
    when(prenotazioneAttivitaTuristicaService.findById(1L))
        .thenThrow(new Exception("Prenotazione non trovata"));

    mockMvc.perform(delete("/api/prenotazioni-attivita-turistica/1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(prenotazioneAttivitaTuristicaService).findById(1L);
    verify(prenotazioneAttivitaTuristicaService, never())
        .deletePrenotazioneAttivitaTuristica(any());
  }
}

