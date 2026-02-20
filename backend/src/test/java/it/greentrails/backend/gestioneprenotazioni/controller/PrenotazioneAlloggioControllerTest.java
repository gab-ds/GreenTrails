package it.greentrails.backend.gestioneprenotazioni.controller;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Camera;
import it.greentrails.backend.entities.Itinerario;
import it.greentrails.backend.entities.PrenotazioneAlloggio;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.entities.ValoriEcosostenibilita;
import it.greentrails.backend.enums.RuoloUtente;
import it.greentrails.backend.enums.StatoItinerario;
import it.greentrails.backend.enums.StatoPagamento;
import it.greentrails.backend.enums.StatoPrenotazione;
import it.greentrails.backend.gestioneattivita.service.AttivitaService;
import it.greentrails.backend.gestioneattivita.service.CameraService;
import it.greentrails.backend.gestioneitinerari.service.ItinerariService;
import it.greentrails.backend.gestioneprenotazioni.service.PrenotazioneAlloggioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
class PrenotazioneAlloggioControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ItinerariService itinerariService;

  @MockBean
  private AttivitaService attivitaService;

  @MockBean
  private CameraService cameraService;

  @MockBean
  private PrenotazioneAlloggioService prenotazioneAlloggioService;

  private Utente visitatore;
  private Utente gestore;
  private Itinerario itinerario;
  private Attivita alloggio;
  private Camera camera;
  private PrenotazioneAlloggio prenotazioneAlloggio;
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

    alloggio = new Attivita();
    alloggio.setId(1L);
    alloggio.setGestore(gestore);
    alloggio.setNome("Hotel Test");
    alloggio.setAlloggio(true);
    alloggio.setValoriEcosostenibilita(valori);

    camera = new Camera();
    camera.setId(1L);
    camera.setAlloggio(alloggio);
    camera.setTipoCamera("Doppia");
    camera.setPrezzo(100.0);
    camera.setDisponibilita(5);
    camera.setCapienza(2);
    camera.setDescrizione("Camera test");

    Date dataInizio = new Date();
    Date dataFine = new Date(dataInizio.getTime() + 86400000); // +1 giorno

    prenotazioneAlloggio = new PrenotazioneAlloggio();
    prenotazioneAlloggio.setId(1L);
    prenotazioneAlloggio.setItinerario(itinerario);
    prenotazioneAlloggio.setCamera(camera);
    prenotazioneAlloggio.setNumAdulti(2);
    prenotazioneAlloggio.setNumBambini(0);
    prenotazioneAlloggio.setNumCamere(1);
    prenotazioneAlloggio.setDataInizio(dataInizio);
    prenotazioneAlloggio.setDataFine(dataFine);
    prenotazioneAlloggio.setStato(StatoPrenotazione.CREATA);
    prenotazioneAlloggio.setStatoPagamento(StatoPagamento.IN_CORSO);
    prenotazioneAlloggio.setPrezzo(100.0);
  }

  @Test
  void testCreaPrenotazioneAlloggio_Success() throws Exception {
    when(itinerariService.findById(1L)).thenReturn(itinerario);
    when(cameraService.findById(1L)).thenReturn(camera);
    when(prenotazioneAlloggioService.controllaDisponibilitaCamera(eq(camera), any(Date.class), any(Date.class)))
        .thenReturn(5);
    when(prenotazioneAlloggioService.savePrenotazioneAlloggio(eq(camera), any(PrenotazioneAlloggio.class)))
        .thenReturn(prenotazioneAlloggio);
    when(itinerariService.saveItinerario(any(Itinerario.class))).thenReturn(itinerario);

    MvcResult result = mockMvc.perform(post("/api/prenotazioni-alloggio")
            .param("idItinerario", "1")
            .param("idCamera", "1")
            .param("numAdulti", "2")
            .param("numBambini", "0")
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-02")
            .param("numCamere", "1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").exists())
        .andReturn();

    verify(itinerariService).findById(1L);
    verify(cameraService).findById(1L);

    ArgumentCaptor<PrenotazioneAlloggio> captor = ArgumentCaptor.forClass(PrenotazioneAlloggio.class);
    verify(prenotazioneAlloggioService).savePrenotazioneAlloggio(eq(camera), captor.capture());

    PrenotazioneAlloggio captured = captor.getValue();
    assertEquals(2, captured.getNumAdulti());
    assertEquals(0, captured.getNumBambini());
    assertEquals(1, captured.getNumCamere());
    assertEquals(camera, captured.getCamera());
    assertEquals(itinerario, captured.getItinerario());
    assertNotNull(captured.getDataInizio());
    assertNotNull(captured.getDataFine());
    assertEquals(StatoPrenotazione.CREATA, captured.getStato());
    assertEquals(100.0, captured.getPrezzo(), 0.01);
  }

  @Test
  void testCreaPrenotazioneAlloggio_ItinerarioNonTrovato() throws Exception {
    Utente altroVisitatore = new Utente();
    altroVisitatore.setId(2L);
    altroVisitatore.setEmail("altro@test.com");
    altroVisitatore.setRuolo(RuoloUtente.VISITATORE);

    when(itinerariService.findById(1L)).thenReturn(itinerario);

    mockMvc.perform(post("/api/prenotazioni-alloggio")
            .param("idItinerario", "1")
            .param("idCamera", "1")
            .param("numAdulti", "2")
            .param("numBambini", "0")
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-02")
            .param("numCamere", "1")
            .with(user(altroVisitatore))
            .with(csrf()))
        .andExpect(status().isNotFound());

    verify(itinerariService).findById(1L);
    verify(cameraService, never()).findById(any());
  }

  @Test
  void testCreaPrenotazioneAlloggio_CapienzaNonSufficiente() throws Exception {
    when(itinerariService.findById(1L)).thenReturn(itinerario);
    when(cameraService.findById(1L)).thenReturn(camera);

    mockMvc.perform(post("/api/prenotazioni-alloggio")
            .param("idItinerario", "1")
            .param("idCamera", "1")
            .param("numAdulti", "3")
            .param("numBambini", "1")
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-02")
            .param("numCamere", "1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isBadRequest());

    verify(itinerariService).findById(1L);
    verify(cameraService).findById(1L);
    verify(prenotazioneAlloggioService, never()).savePrenotazioneAlloggio(any(), any());
  }

  @Test
  void testCreaPrenotazioneAlloggio_CameraNonDisponibile() throws Exception {
    when(itinerariService.findById(1L)).thenReturn(itinerario);
    when(cameraService.findById(1L)).thenReturn(camera);
    when(prenotazioneAlloggioService.controllaDisponibilitaCamera(eq(camera), any(Date.class), any(Date.class)))
        .thenReturn(0);

    mockMvc.perform(post("/api/prenotazioni-alloggio")
            .param("idItinerario", "1")
            .param("idCamera", "1")
            .param("numAdulti", "2")
            .param("numBambini", "0")
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-02")
            .param("numCamere", "1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isBadRequest());

    verify(prenotazioneAlloggioService, never()).savePrenotazioneAlloggio(any(), any());
  }

  @Test
  void testCreaPrenotazioneAlloggio_SenzaNumBambini() throws Exception {
    when(itinerariService.findById(1L)).thenReturn(itinerario);
    when(cameraService.findById(1L)).thenReturn(camera);
    when(prenotazioneAlloggioService.controllaDisponibilitaCamera(eq(camera), any(Date.class), any(Date.class)))
        .thenReturn(5);
    when(prenotazioneAlloggioService.savePrenotazioneAlloggio(eq(camera), any(PrenotazioneAlloggio.class)))
        .thenReturn(prenotazioneAlloggio);
    when(itinerariService.saveItinerario(any(Itinerario.class))).thenReturn(itinerario);

    mockMvc.perform(post("/api/prenotazioni-alloggio")
            .param("idItinerario", "1")
            .param("idCamera", "1")
            .param("numAdulti", "2")
            // numBambini omesso - dovrebbe usare default = 0
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-02")
            .param("numCamere", "1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(itinerariService).findById(1L);
    verify(cameraService).findById(1L);
    verify(prenotazioneAlloggioService).savePrenotazioneAlloggio(eq(camera), any(PrenotazioneAlloggio.class));
  }

  @Test
  void testCreaPrenotazioneAlloggio_DurataPiuDi24Ore() throws Exception {
    when(itinerariService.findById(1L)).thenReturn(itinerario);
    when(cameraService.findById(1L)).thenReturn(camera);
    when(prenotazioneAlloggioService.controllaDisponibilitaCamera(eq(camera), any(Date.class), any(Date.class)))
        .thenReturn(5);
    when(prenotazioneAlloggioService.savePrenotazioneAlloggio(eq(camera), any(PrenotazioneAlloggio.class)))
        .thenReturn(prenotazioneAlloggio);
    when(itinerariService.saveItinerario(any(Itinerario.class))).thenReturn(itinerario);

    mockMvc.perform(post("/api/prenotazioni-alloggio")
            .param("idItinerario", "1")
            .param("idCamera", "1")
            .param("numAdulti", "2")
            .param("numBambini", "0")
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-05") // 4 giorni
            .param("numCamere", "1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(itinerariService).findById(1L);
    verify(cameraService).findById(1L);
    verify(prenotazioneAlloggioService).savePrenotazioneAlloggio(eq(camera), any(PrenotazioneAlloggio.class));
    verify(itinerariService).saveItinerario(any(Itinerario.class));
  }

  @Test
  void testCreaPrenotazioneAlloggio_Exception() throws Exception {
    when(itinerariService.findById(1L))
        .thenThrow(new Exception("Errore DB"));

    mockMvc.perform(post("/api/prenotazioni-alloggio")
            .param("idItinerario", "1")
            .param("idCamera", "1")
            .param("numAdulti", "2")
            .param("numBambini", "0")
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-02")
            .param("numCamere", "1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(itinerariService).findById(1L);
  }

  @Test
  void testCreaPrenotazioneAlloggio_CapienzaEsatta() throws Exception {
    when(itinerariService.findById(1L)).thenReturn(itinerario);
    when(cameraService.findById(1L)).thenReturn(camera);
    when(prenotazioneAlloggioService.controllaDisponibilitaCamera(eq(camera), any(Date.class), any(Date.class)))
        .thenReturn(5);
    when(prenotazioneAlloggioService.savePrenotazioneAlloggio(eq(camera), any(PrenotazioneAlloggio.class)))
        .thenReturn(prenotazioneAlloggio);
    when(itinerariService.saveItinerario(any(Itinerario.class))).thenReturn(itinerario);

    // Capienza camera = 2, numAdulti + numBambini = 2 -> esattamente al limite
    mockMvc.perform(post("/api/prenotazioni-alloggio")
            .param("idItinerario", "1")
            .param("idCamera", "1")
            .param("numAdulti", "1")
            .param("numBambini", "1")
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-02")
            .param("numCamere", "1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());

    ArgumentCaptor<PrenotazioneAlloggio> captor = ArgumentCaptor.forClass(PrenotazioneAlloggio.class);
    verify(prenotazioneAlloggioService).savePrenotazioneAlloggio(eq(camera), captor.capture());

    PrenotazioneAlloggio captured = captor.getValue();
    assertEquals(1, captured.getNumAdulti());
    assertEquals(1, captured.getNumBambini());
  }

  @Test
  void testCreaPrenotazioneAlloggio_MultipleCamera() throws Exception {
    when(itinerariService.findById(1L)).thenReturn(itinerario);
    when(cameraService.findById(1L)).thenReturn(camera);
    when(prenotazioneAlloggioService.controllaDisponibilitaCamera(eq(camera), any(Date.class), any(Date.class)))
        .thenReturn(5);
    when(prenotazioneAlloggioService.savePrenotazioneAlloggio(eq(camera), any(PrenotazioneAlloggio.class)))
        .thenReturn(prenotazioneAlloggio);
    when(itinerariService.saveItinerario(any(Itinerario.class))).thenReturn(itinerario);

    // 3 camere, capienza 2 per camera = 6 persone totali, numAdulti + numBambini = 5 -> OK
    mockMvc.perform(post("/api/prenotazioni-alloggio")
            .param("idItinerario", "1")
            .param("idCamera", "1")
            .param("numAdulti", "3")
            .param("numBambini", "2")
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-02")
            .param("numCamere", "3")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());

    ArgumentCaptor<PrenotazioneAlloggio> captor = ArgumentCaptor.forClass(PrenotazioneAlloggio.class);
    verify(prenotazioneAlloggioService).savePrenotazioneAlloggio(eq(camera), captor.capture());

    PrenotazioneAlloggio captured = captor.getValue();
    assertEquals(3, captured.getNumCamere());
    // Prezzo = numCamere * prezzoCamera = 3 * 100 = 300
    assertEquals(300.0, captured.getPrezzo(), 0.01);
  }

  @Test
  void testCreaPrenotazioneAlloggio_DisponibilitaEsattaAlLimite() throws Exception {
    when(itinerariService.findById(1L)).thenReturn(itinerario);
    when(cameraService.findById(1L)).thenReturn(camera);
    // Disponibilità = 1, numCamere = 1 -> esattamente sufficiente
    when(prenotazioneAlloggioService.controllaDisponibilitaCamera(eq(camera), any(Date.class), any(Date.class)))
        .thenReturn(1);
    when(prenotazioneAlloggioService.savePrenotazioneAlloggio(eq(camera), any(PrenotazioneAlloggio.class)))
        .thenReturn(prenotazioneAlloggio);
    when(itinerariService.saveItinerario(any(Itinerario.class))).thenReturn(itinerario);

    mockMvc.perform(post("/api/prenotazioni-alloggio")
            .param("idItinerario", "1")
            .param("idCamera", "1")
            .param("numAdulti", "2")
            .param("numBambini", "0")
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-02")
            .param("numCamere", "1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());
  }

  @Test
  void testCreaPrenotazioneAlloggio_Durata48Ore() throws Exception {
    when(itinerariService.findById(1L)).thenReturn(itinerario);
    when(cameraService.findById(1L)).thenReturn(camera);
    when(prenotazioneAlloggioService.controllaDisponibilitaCamera(eq(camera), any(Date.class), any(Date.class)))
        .thenReturn(5);
    when(prenotazioneAlloggioService.savePrenotazioneAlloggio(eq(camera), any(PrenotazioneAlloggio.class)))
        .thenReturn(prenotazioneAlloggio);
    when(itinerariService.saveItinerario(any(Itinerario.class))).thenReturn(itinerario);

    // Durata = 2 giorni -> 48 ore -> ceil(48/24) = 2
    mockMvc.perform(post("/api/prenotazioni-alloggio")
            .param("idItinerario", "1")
            .param("idCamera", "1")
            .param("numAdulti", "2")
            .param("numBambini", "0")
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-03")
            .param("numCamere", "1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());

    ArgumentCaptor<PrenotazioneAlloggio> captor = ArgumentCaptor.forClass(PrenotazioneAlloggio.class);
    verify(prenotazioneAlloggioService).savePrenotazioneAlloggio(eq(camera), captor.capture());

    PrenotazioneAlloggio captured = captor.getValue();
    // Con durata > 24 ore, il prezzo viene moltiplicato per ceil(durataOre/24)
    // 48 ore -> ceil(48/24) = 2 -> prezzo = 100 * 2 = 200
    assertEquals(200.0, captured.getPrezzo(), 0.01);
  }

  @Test
  void testConfermaPrenotazioneAlloggio_Success() throws Exception {
    prenotazioneAlloggio.setStato(StatoPrenotazione.NON_CONFERMATA);
    when(prenotazioneAlloggioService.findById(1L)).thenReturn(prenotazioneAlloggio);
    when(prenotazioneAlloggioService.controllaDisponibilitaCamera(eq(camera), any(Date.class), any(Date.class)))
        .thenReturn(5);
    when(prenotazioneAlloggioService.savePrenotazioneAlloggio(eq(camera), any(PrenotazioneAlloggio.class)))
        .thenReturn(prenotazioneAlloggio);
    when(itinerariService.saveItinerario(any(Itinerario.class))).thenReturn(itinerario);

    MvcResult result = mockMvc.perform(post("/api/prenotazioni-alloggio/1")
            .param("numAdulti", "2")
            .param("numBambini", "0")
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-02")
            .param("numCamere", "1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").exists())
        .andReturn();

    verify(prenotazioneAlloggioService).findById(1L);

    ArgumentCaptor<PrenotazioneAlloggio> captor = ArgumentCaptor.forClass(PrenotazioneAlloggio.class);
    verify(prenotazioneAlloggioService).savePrenotazioneAlloggio(eq(camera), captor.capture());

    PrenotazioneAlloggio captured = captor.getValue();
    assertEquals(2, captured.getNumAdulti());
    assertEquals(0, captured.getNumBambini());
    assertEquals(1, captured.getNumCamere());
    assertNotNull(captured.getDataInizio());
    assertNotNull(captured.getDataFine());
    assertEquals(StatoPrenotazione.CREATA, captured.getStato());
    assertEquals(100.0, captured.getPrezzo(), 0.01);
  }

  @Test
  void testConfermaPrenotazioneAlloggio_SenzaNumBambini() throws Exception {
    prenotazioneAlloggio.setStato(StatoPrenotazione.NON_CONFERMATA);
    when(prenotazioneAlloggioService.findById(1L)).thenReturn(prenotazioneAlloggio);
    when(prenotazioneAlloggioService.controllaDisponibilitaCamera(eq(camera), any(Date.class), any(Date.class)))
        .thenReturn(5);
    when(prenotazioneAlloggioService.savePrenotazioneAlloggio(eq(camera), any(PrenotazioneAlloggio.class)))
        .thenReturn(prenotazioneAlloggio);
    when(itinerariService.saveItinerario(any(Itinerario.class))).thenReturn(itinerario);

    mockMvc.perform(post("/api/prenotazioni-alloggio/1")
            .param("numAdulti", "2")
            // numBambini omesso - dovrebbe usare default = 0
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-02")
            .param("numCamere", "1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(prenotazioneAlloggioService).findById(1L);
    verify(prenotazioneAlloggioService).savePrenotazioneAlloggio(eq(camera), any(PrenotazioneAlloggio.class));
  }

  @Test
  void testConfermaPrenotazioneAlloggio_PrenotazioneNonTrovata() throws Exception {
    Utente altroVisitatore = new Utente();
    altroVisitatore.setId(2L);
    altroVisitatore.setEmail("altro@test.com");
    altroVisitatore.setRuolo(RuoloUtente.VISITATORE);

    when(prenotazioneAlloggioService.findById(1L)).thenReturn(prenotazioneAlloggio);

    mockMvc.perform(post("/api/prenotazioni-alloggio/1")
            .param("numAdulti", "2")
            .param("numBambini", "0")
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-02")
            .param("numCamere", "1")
            .with(user(altroVisitatore))
            .with(csrf()))
        .andExpect(status().isNotFound());

    verify(prenotazioneAlloggioService).findById(1L);
  }

  @Test
  void testConfermaPrenotazioneAlloggio_PrenotazioneNonModificabile() throws Exception {
    when(prenotazioneAlloggioService.findById(1L)).thenReturn(prenotazioneAlloggio);

    mockMvc.perform(post("/api/prenotazioni-alloggio/1")
            .param("numAdulti", "2")
            .param("numBambini", "0")
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-02")
            .param("numCamere", "1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isBadRequest());

    verify(prenotazioneAlloggioService).findById(1L);
    verify(prenotazioneAlloggioService, never()).savePrenotazioneAlloggio(any(), any());
  }

  @Test
  void testConfermaPrenotazioneAlloggio_CapienzaNonSufficiente() throws Exception {
    prenotazioneAlloggio.setStato(StatoPrenotazione.NON_CONFERMATA);
    when(prenotazioneAlloggioService.findById(1L)).thenReturn(prenotazioneAlloggio);

    mockMvc.perform(post("/api/prenotazioni-alloggio/1")
            .param("numAdulti", "3")
            .param("numBambini", "1")
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-02")
            .param("numCamere", "1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isBadRequest());

    verify(prenotazioneAlloggioService).findById(1L);
    verify(prenotazioneAlloggioService, never()).savePrenotazioneAlloggio(any(), any());
  }

  @Test
  void testConfermaPrenotazioneAlloggio_CameraNonDisponibile() throws Exception {
    prenotazioneAlloggio.setStato(StatoPrenotazione.NON_CONFERMATA);
    when(prenotazioneAlloggioService.findById(1L)).thenReturn(prenotazioneAlloggio);
    when(prenotazioneAlloggioService.controllaDisponibilitaCamera(eq(camera), any(Date.class), any(Date.class)))
        .thenReturn(0);

    mockMvc.perform(post("/api/prenotazioni-alloggio/1")
            .param("numAdulti", "2")
            .param("numBambini", "0")
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-02")
            .param("numCamere", "1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isBadRequest());

    verify(prenotazioneAlloggioService).findById(1L);
    verify(prenotazioneAlloggioService, never()).savePrenotazioneAlloggio(any(), any());
  }

  @Test
  void testConfermaPrenotazioneAlloggio_DurataPiuDi24Ore() throws Exception {
    prenotazioneAlloggio.setStato(StatoPrenotazione.NON_CONFERMATA);
    when(prenotazioneAlloggioService.findById(1L)).thenReturn(prenotazioneAlloggio);
    when(prenotazioneAlloggioService.controllaDisponibilitaCamera(eq(camera), any(Date.class), any(Date.class)))
        .thenReturn(5);
    when(prenotazioneAlloggioService.savePrenotazioneAlloggio(eq(camera), any(PrenotazioneAlloggio.class)))
        .thenReturn(prenotazioneAlloggio);
    when(itinerariService.saveItinerario(any(Itinerario.class))).thenReturn(itinerario);

    mockMvc.perform(post("/api/prenotazioni-alloggio/1")
            .param("numAdulti", "2")
            .param("numBambini", "0")
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-05") // 4 giorni
            .param("numCamere", "1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(prenotazioneAlloggioService).findById(1L);
    verify(prenotazioneAlloggioService).savePrenotazioneAlloggio(eq(camera), any(PrenotazioneAlloggio.class));
    verify(itinerariService).saveItinerario(any(Itinerario.class));
  }

  @Test
  void testConfermaPrenotazioneAlloggio_Exception() throws Exception {
    when(prenotazioneAlloggioService.findById(1L))
        .thenThrow(new Exception("Errore DB"));

    mockMvc.perform(post("/api/prenotazioni-alloggio/1")
            .param("numAdulti", "2")
            .param("numBambini", "0")
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-02")
            .param("numCamere", "1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(prenotazioneAlloggioService).findById(1L);
  }

  @Test
  void testConfermaPrenotazioneAlloggio_CapienzaEsatta() throws Exception {
    prenotazioneAlloggio.setStato(StatoPrenotazione.NON_CONFERMATA);
    when(prenotazioneAlloggioService.findById(1L)).thenReturn(prenotazioneAlloggio);
    when(prenotazioneAlloggioService.controllaDisponibilitaCamera(eq(camera), any(Date.class), any(Date.class)))
        .thenReturn(5);
    when(prenotazioneAlloggioService.savePrenotazioneAlloggio(eq(camera), any(PrenotazioneAlloggio.class)))
        .thenReturn(prenotazioneAlloggio);
    when(itinerariService.saveItinerario(any(Itinerario.class))).thenReturn(itinerario);

    // Capienza camera = 2, numAdulti + numBambini = 2 -> esattamente al limite
    mockMvc.perform(post("/api/prenotazioni-alloggio/1")
            .param("numAdulti", "1")
            .param("numBambini", "1")
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-02")
            .param("numCamere", "1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());

    ArgumentCaptor<PrenotazioneAlloggio> captor = ArgumentCaptor.forClass(PrenotazioneAlloggio.class);
    verify(prenotazioneAlloggioService).savePrenotazioneAlloggio(eq(camera), captor.capture());

    PrenotazioneAlloggio captured = captor.getValue();
    assertEquals(1, captured.getNumAdulti());
    assertEquals(1, captured.getNumBambini());
  }

  @Test
  void testConfermaPrenotazioneAlloggio_MultipleCamera() throws Exception {
    prenotazioneAlloggio.setStato(StatoPrenotazione.NON_CONFERMATA);
    when(prenotazioneAlloggioService.findById(1L)).thenReturn(prenotazioneAlloggio);
    when(prenotazioneAlloggioService.controllaDisponibilitaCamera(eq(camera), any(Date.class), any(Date.class)))
        .thenReturn(5);
    when(prenotazioneAlloggioService.savePrenotazioneAlloggio(eq(camera), any(PrenotazioneAlloggio.class)))
        .thenReturn(prenotazioneAlloggio);
    when(itinerariService.saveItinerario(any(Itinerario.class))).thenReturn(itinerario);

    // 3 camere, capienza 2 per camera = 6 persone totali
    mockMvc.perform(post("/api/prenotazioni-alloggio/1")
            .param("numAdulti", "3")
            .param("numBambini", "2")
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-02")
            .param("numCamere", "3")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());

    ArgumentCaptor<PrenotazioneAlloggio> captor = ArgumentCaptor.forClass(PrenotazioneAlloggio.class);
    verify(prenotazioneAlloggioService).savePrenotazioneAlloggio(eq(camera), captor.capture());

    PrenotazioneAlloggio captured = captor.getValue();
    assertEquals(3, captured.getNumCamere());
    // Prezzo = numCamere * prezzoCamera = 3 * 100 = 300
    assertEquals(300.0, captured.getPrezzo(), 0.01);
  }

  @Test
  void testConfermaPrenotazioneAlloggio_DisponibilitaEsattaAlLimite() throws Exception {
    prenotazioneAlloggio.setStato(StatoPrenotazione.NON_CONFERMATA);
    when(prenotazioneAlloggioService.findById(1L)).thenReturn(prenotazioneAlloggio);
    // Disponibilità = 1, numCamere = 1 -> esattamente sufficiente
    when(prenotazioneAlloggioService.controllaDisponibilitaCamera(eq(camera), any(Date.class), any(Date.class)))
        .thenReturn(1);
    when(prenotazioneAlloggioService.savePrenotazioneAlloggio(eq(camera), any(PrenotazioneAlloggio.class)))
        .thenReturn(prenotazioneAlloggio);
    when(itinerariService.saveItinerario(any(Itinerario.class))).thenReturn(itinerario);

    mockMvc.perform(post("/api/prenotazioni-alloggio/1")
            .param("numAdulti", "2")
            .param("numBambini", "0")
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-02")
            .param("numCamere", "1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());
  }

  @Test
  void testConfermaPrenotazioneAlloggio_Durata48Ore() throws Exception {
    prenotazioneAlloggio.setStato(StatoPrenotazione.NON_CONFERMATA);
    when(prenotazioneAlloggioService.findById(1L)).thenReturn(prenotazioneAlloggio);
    when(prenotazioneAlloggioService.controllaDisponibilitaCamera(eq(camera), any(Date.class), any(Date.class)))
        .thenReturn(5);
    when(prenotazioneAlloggioService.savePrenotazioneAlloggio(eq(camera), any(PrenotazioneAlloggio.class)))
        .thenReturn(prenotazioneAlloggio);
    when(itinerariService.saveItinerario(any(Itinerario.class))).thenReturn(itinerario);

    // Durata = 2 giorni -> 48 ore -> ceil(48/24) = 2
    mockMvc.perform(post("/api/prenotazioni-alloggio/1")
            .param("numAdulti", "2")
            .param("numBambini", "0")
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-03")
            .param("numCamere", "1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());

    ArgumentCaptor<PrenotazioneAlloggio> captor = ArgumentCaptor.forClass(PrenotazioneAlloggio.class);
    verify(prenotazioneAlloggioService).savePrenotazioneAlloggio(eq(camera), captor.capture());

    PrenotazioneAlloggio captured = captor.getValue();
    // Con durata > 24 ore, il prezzo viene moltiplicato per ceil(durataOre/24)
    // 48 ore -> ceil(48/24) = 2 -> prezzo = 100 * 2 = 200
    assertEquals(200.0, captured.getPrezzo(), 0.01);
  }

  @Test
  void testVisualizzaPrenotazioneAlloggio_Success() throws Exception {
    when(prenotazioneAlloggioService.findById(1L)).thenReturn(prenotazioneAlloggio);

    MvcResult result = mockMvc.perform(get("/api/prenotazioni-alloggio/1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").exists())
        .andReturn();

    verify(prenotazioneAlloggioService).findById(1L);
  }

  @Test
  void testVisualizzaPrenotazioneAlloggio_PrenotazioneNonTrovata() throws Exception {
    Utente altroVisitatore = new Utente();
    altroVisitatore.setId(2L);
    altroVisitatore.setEmail("altro@test.com");
    altroVisitatore.setRuolo(RuoloUtente.VISITATORE);

    when(prenotazioneAlloggioService.findById(1L)).thenReturn(prenotazioneAlloggio);

    mockMvc.perform(get("/api/prenotazioni-alloggio/1")
            .with(user(altroVisitatore))
            .with(csrf()))
        .andExpect(status().isNotFound());

    verify(prenotazioneAlloggioService).findById(1L);
  }

  @Test
  void testVisualizzaPrenotazioneAlloggio_Exception() throws Exception {
    when(prenotazioneAlloggioService.findById(999L))
        .thenThrow(new Exception("Prenotazione non trovata"));

    mockMvc.perform(get("/api/prenotazioni-alloggio/999")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(prenotazioneAlloggioService).findById(999L);
  }

  @Test
  void testVisualizzaPrenotazioniAlloggioPerAttivita_Success() throws Exception {
    List<PrenotazioneAlloggio> prenotazioni = new ArrayList<>();
    prenotazioni.add(prenotazioneAlloggio);

    when(attivitaService.findById(1L)).thenReturn(alloggio);
    when(prenotazioneAlloggioService.getPrenotazioniByAlloggio(alloggio))
        .thenReturn(prenotazioni);

    MvcResult result = mockMvc.perform(get("/api/prenotazioni-alloggio/perAttivita/1")
            .with(user(gestore))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").exists())
        .andReturn();

    verify(attivitaService).findById(1L);
    verify(prenotazioneAlloggioService).getPrenotazioniByAlloggio(alloggio);
  }

  @Test
  void testVisualizzaPrenotazioniAlloggioPerAttivita_AlloggioNonTrovato() throws Exception {
    Utente altroGestore = new Utente();
    altroGestore.setId(3L);
    altroGestore.setEmail("altro.gestore@test.com");
    altroGestore.setRuolo(RuoloUtente.GESTORE_ATTIVITA);

    when(attivitaService.findById(1L)).thenReturn(alloggio);

    mockMvc.perform(get("/api/prenotazioni-alloggio/perAttivita/1")
            .with(user(altroGestore))
            .with(csrf()))
        .andExpect(status().isNotFound());

    verify(attivitaService).findById(1L);
    verify(prenotazioneAlloggioService, never()).getPrenotazioniByAlloggio(any());
  }

  @Test
  void testVisualizzaPrenotazioniAlloggioPerAttivita_Exception() throws Exception {
    when(attivitaService.findById(1L))
        .thenThrow(new Exception("Attività non trovata"));

    mockMvc.perform(get("/api/prenotazioni-alloggio/perAttivita/1")
            .with(user(gestore))
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(attivitaService).findById(1L);
  }

  @Test
  void testVisualizzaDisponibilitaPerAlloggio_Success() throws Exception {
    when(attivitaService.findById(1L)).thenReturn(alloggio);
    when(prenotazioneAlloggioService.controllaDisponibilitaAlloggio(eq(alloggio), any(Date.class), any(Date.class)))
        .thenReturn(5);

    MvcResult result = mockMvc.perform(get("/api/prenotazioni-alloggio/perAttivita/1/disponibilita")
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-02")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").exists())
        .andReturn();

    verify(attivitaService).findById(1L);
    verify(prenotazioneAlloggioService).controllaDisponibilitaAlloggio(eq(alloggio), any(Date.class), any(Date.class));
  }

  @Test
  void testVisualizzaDisponibilitaPerAlloggio_Exception() throws Exception {
    when(attivitaService.findById(1L))
        .thenThrow(new Exception("Attività non trovata"));

    mockMvc.perform(get("/api/prenotazioni-alloggio/perAttivita/1/disponibilita")
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-02")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(attivitaService).findById(1L);
  }

  @Test
  void testVisualizzaDisponibilitaPerCamera_Success() throws Exception {
    when(cameraService.findById(1L)).thenReturn(camera);
    when(prenotazioneAlloggioService.controllaDisponibilitaCamera(eq(camera), any(Date.class), any(Date.class)))
        .thenReturn(5);

    MvcResult result = mockMvc.perform(get("/api/prenotazioni-alloggio/perCamera/1/disponibilita")
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-02")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").exists())
        .andReturn();

    verify(cameraService).findById(1L);
    verify(prenotazioneAlloggioService).controllaDisponibilitaCamera(eq(camera), any(Date.class), any(Date.class));
  }

  @Test
  void testVisualizzaDisponibilitaPerCamera_Exception() throws Exception {
    when(cameraService.findById(1L))
        .thenThrow(new Exception("Camera non trovata"));

    mockMvc.perform(get("/api/prenotazioni-alloggio/perCamera/1/disponibilita")
            .param("dataInizio", "2026-03-01")
            .param("dataFine", "2026-03-02")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(cameraService).findById(1L);
  }

  @Test
  void testVisualizzaPrenotazioniAlloggioPerVisitatore_Success() throws Exception {
    List<PrenotazioneAlloggio> prenotazioni = new ArrayList<>();
    prenotazioni.add(prenotazioneAlloggio);

    when(prenotazioneAlloggioService.getPrenotazioniByVisitatore(visitatore))
        .thenReturn(prenotazioni);

    MvcResult result = mockMvc.perform(get("/api/prenotazioni-alloggio")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").exists())
        .andReturn();

    verify(prenotazioneAlloggioService).getPrenotazioniByVisitatore(visitatore);
  }

  @Test
  void testVisualizzaPrenotazioniAlloggioPerVisitatore_Exception() throws Exception {
    when(prenotazioneAlloggioService.getPrenotazioniByVisitatore(visitatore))
        .thenThrow(new Exception("Errore DB"));

    mockMvc.perform(get("/api/prenotazioni-alloggio")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(prenotazioneAlloggioService).getPrenotazioniByVisitatore(visitatore);
  }

  @Test
  void testCancellaPrenotazioneAlloggio_Success() throws Exception {
    when(prenotazioneAlloggioService.findById(1L)).thenReturn(prenotazioneAlloggio);
    when(prenotazioneAlloggioService.deletePrenotazioneAlloggio(prenotazioneAlloggio))
        .thenReturn(true);
    when(itinerariService.saveItinerario(any(Itinerario.class))).thenReturn(itinerario);

    MvcResult result = mockMvc.perform(delete("/api/prenotazioni-alloggio/1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").exists())
        .andReturn();

    verify(prenotazioneAlloggioService).findById(1L);
    verify(prenotazioneAlloggioService).deletePrenotazioneAlloggio(prenotazioneAlloggio);

    ArgumentCaptor<Itinerario> captor = ArgumentCaptor.forClass(Itinerario.class);
    verify(itinerariService).saveItinerario(captor.capture());

    Itinerario captured = captor.getValue();
    // Verifica che il totale dell'itinerario sia stato aggiornato (totale - prezzo prenotazione)
    assertEquals(0.0, captured.getTotale(), 0.01);
  }

  @Test
  void testCancellaPrenotazioneAlloggio_PrenotazioneNonTrovata() throws Exception {
    Utente altroVisitatore = new Utente();
    altroVisitatore.setId(2L);
    altroVisitatore.setEmail("altro@test.com");
    altroVisitatore.setRuolo(RuoloUtente.VISITATORE);

    when(prenotazioneAlloggioService.findById(1L)).thenReturn(prenotazioneAlloggio);

    mockMvc.perform(delete("/api/prenotazioni-alloggio/1")
            .with(user(altroVisitatore))
            .with(csrf()))
        .andExpect(status().isNotFound());

    verify(prenotazioneAlloggioService).findById(1L);
    verify(prenotazioneAlloggioService, never()).deletePrenotazioneAlloggio(any());
  }

  @Test
  void testCancellaPrenotazioneAlloggio_Exception() throws Exception {
    when(prenotazioneAlloggioService.findById(1L))
        .thenThrow(new Exception("Prenotazione non trovata"));

    mockMvc.perform(delete("/api/prenotazioni-alloggio/1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(prenotazioneAlloggioService).findById(1L);
    verify(prenotazioneAlloggioService, never()).deletePrenotazioneAlloggio(any());
  }
}