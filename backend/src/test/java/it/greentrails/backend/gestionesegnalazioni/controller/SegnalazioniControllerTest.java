package it.greentrails.backend.gestionesegnalazioni.controller;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Recensione;
import it.greentrails.backend.entities.Segnalazione;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.entities.ValoriEcosostenibilita;
import it.greentrails.backend.enums.CategorieAlloggio;
import it.greentrails.backend.enums.RuoloUtente;
import it.greentrails.backend.enums.StatoSegnalazione;
import it.greentrails.backend.gestioneattivita.service.AttivitaService;
import it.greentrails.backend.gestioneattivita.service.RecensioneService;
import it.greentrails.backend.gestionesegnalazioni.service.SegnalazioniService;
import it.greentrails.backend.gestioneupload.service.ArchiviazioneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.geo.Point;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SegnalazioniControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private SegnalazioniService segnalazioniService;

  @MockBean
  private AttivitaService attivitaService;

  @MockBean
  private RecensioneService recensioneService;

  @MockBean
  private ArchiviazioneService archiviazioneService;

  private Utente visitatore;
  private Utente gestore;
  private Utente amministratore;
  private Attivita alloggio;
  private Recensione recensione;
  private Segnalazione segnalazioneAttivita;
  private Segnalazione segnalazioneRecensione;
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

    amministratore = new Utente();
    amministratore.setId(3L);
    amministratore.setEmail("admin@test.com");
    amministratore.setRuolo(RuoloUtente.AMMINISTRATORE);

    valori = new ValoriEcosostenibilita();
    valori.setId(1L);

    alloggio = new Attivita();
    alloggio.setId(1L);
    alloggio.setAlloggio(true);
    alloggio.setGestore(gestore);
    alloggio.setNome("Hotel Eco");
    alloggio.setIndirizzo("Via Verde 1");
    alloggio.setCap("00100");
    alloggio.setCitta("Roma");
    alloggio.setProvincia("RM");
    alloggio.setCoordinate(new Point(41.9028, 12.4964));
    alloggio.setDescrizioneBreve("Hotel ecosostenibile");
    alloggio.setDescrizioneLunga("Un hotel completamente ecosostenibile");
    alloggio.setValoriEcosostenibilita(valori);
    alloggio.setCategoriaAlloggio(CategorieAlloggio.HOTEL);
    alloggio.setMedia("media-uuid");
    alloggio.setEliminata(false);

    recensione = new Recensione();
    recensione.setId(1L);
    recensione.setVisitatore(visitatore);
    recensione.setAttivita(alloggio);
    recensione.setValutazioneStelleEsperienza(5);
    recensione.setDescrizione("Esperienza fantastica!");
    recensione.setMedia("media-uuid");
    recensione.setValoriEcosostenibilita(valori);

    segnalazioneAttivita = new Segnalazione();
    segnalazioneAttivita.setId(1L);
    segnalazioneAttivita.setUtente(visitatore);
    segnalazioneAttivita.setDataSegnalazione(new Date());
    segnalazioneAttivita.setDescrizione("Attività non conforme");
    segnalazioneAttivita.setAttivita(alloggio);
    segnalazioneAttivita.setForRecensione(false);
    segnalazioneAttivita.setStato(StatoSegnalazione.CREATA);

    segnalazioneRecensione = new Segnalazione();
    segnalazioneRecensione.setId(2L);
    segnalazioneRecensione.setUtente(gestore);
    segnalazioneRecensione.setDataSegnalazione(new Date());
    segnalazioneRecensione.setDescrizione("Recensione offensiva");
    segnalazioneRecensione.setRecensione(recensione);
    segnalazioneRecensione.setForRecensione(true);
    segnalazioneRecensione.setStato(StatoSegnalazione.CREATA);
  }

  @Test
  void testCreaSegnalazioneAttivita_Visitatore_ConImmagine_Success() throws Exception {
    MockMultipartFile mockFile = new MockMultipartFile("immagine", "test.jpg", "image/jpeg",
        "test image content".getBytes());

    when(attivitaService.findById(1L)).thenReturn(alloggio);
    when(segnalazioniService.saveSegnalazione(any(Segnalazione.class)))
        .thenReturn(segnalazioneAttivita);

    mockMvc.perform(multipart("/api/segnalazioni")
            .file(mockFile)
            .param("descrizione", "Attività non conforme")
            .param("idAttivita", "1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(attivitaService).findById(1L);
    verify(archiviazioneService).store(anyString(), eq(mockFile));
    verify(segnalazioniService).saveSegnalazione(any(Segnalazione.class));
  }

  @Test
  void testCreaSegnalazioneAttivita_Visitatore_SenzaImmagine_Success() throws Exception {
    when(attivitaService.findById(1L)).thenReturn(alloggio);
    when(segnalazioniService.saveSegnalazione(any(Segnalazione.class)))
        .thenReturn(segnalazioneAttivita);

    mockMvc.perform(multipart("/api/segnalazioni")
            .param("descrizione", "Attività non conforme")
            .param("idAttivita", "1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(attivitaService).findById(1L);
    verify(archiviazioneService, never()).store(anyString(), any());
    verify(segnalazioniService).saveSegnalazione(any(Segnalazione.class));
  }

  @Test
  void testCreaSegnalazioneRecensione_Gestore_ConImmagine_Success() throws Exception {
    MockMultipartFile mockFile = new MockMultipartFile("immagine", "test.jpg", "image/jpeg",
        "test image content".getBytes());

    when(recensioneService.findById(1L)).thenReturn(recensione);
    when(segnalazioniService.saveSegnalazione(any(Segnalazione.class)))
        .thenReturn(segnalazioneRecensione);

    mockMvc.perform(multipart("/api/segnalazioni")
            .file(mockFile)
            .param("descrizione", "Recensione offensiva")
            .param("idRecensione", "1")
            .with(user(gestore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(recensioneService).findById(1L);
    verify(archiviazioneService).store(anyString(), eq(mockFile));
    verify(segnalazioniService).saveSegnalazione(any(Segnalazione.class));
  }

  @Test
  void testCreaSegnalazioneRecensione_Gestore_SenzaImmagine_Success() throws Exception {
    when(recensioneService.findById(1L)).thenReturn(recensione);
    when(segnalazioniService.saveSegnalazione(any(Segnalazione.class)))
        .thenReturn(segnalazioneRecensione);

    mockMvc.perform(multipart("/api/segnalazioni")
            .param("descrizione", "Recensione offensiva")
            .param("idRecensione", "1")
            .with(user(gestore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(recensioneService).findById(1L);
    verify(archiviazioneService, never()).store(anyString(), any());
    verify(segnalazioniService).saveSegnalazione(any(Segnalazione.class));
  }

  @Test
  void testCreaSegnalazione_Visitatore_SenzaIdAttivita_BadRequest() throws Exception {
    mockMvc.perform(multipart("/api/segnalazioni")
            .param("descrizione", "Segnalazione senza ID")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isBadRequest());

    verify(segnalazioniService, never()).saveSegnalazione(any());
  }

  @Test
  void testCreaSegnalazione_Gestore_SenzaIdRecensione_BadRequest() throws Exception {
    mockMvc.perform(multipart("/api/segnalazioni")
            .param("descrizione", "Segnalazione senza ID")
            .with(user(gestore))
            .with(csrf()))
        .andExpect(status().isBadRequest());

    verify(segnalazioniService, never()).saveSegnalazione(any());
  }

  @Test
  void testCreaSegnalazione_Amministratore_BadRequest() throws Exception {
    mockMvc.perform(multipart("/api/segnalazioni")
            .param("descrizione", "Segnalazione amministratore")
            .param("idAttivita", "1")
            .with(user(amministratore))
            .with(csrf()))
        .andExpect(status().isBadRequest());

    verify(segnalazioniService, never()).saveSegnalazione(any());
  }

  @Test
  void testCreaSegnalazione_Exception() throws Exception {
    when(attivitaService.findById(1L)).thenThrow(new Exception("Attività non trovata"));

    mockMvc.perform(multipart("/api/segnalazioni")
            .param("descrizione", "Attività non conforme")
            .param("idAttivita", "1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(attivitaService).findById(1L);
  }

  @Test
  void testVisualizzaSegnalazione_Success() throws Exception {
    when(segnalazioniService.findById(1L)).thenReturn(segnalazioneAttivita);

    mockMvc.perform(get("/api/segnalazioni/1")
            .with(user(amministratore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(segnalazioniService).findById(1L);
  }

  @Test
  void testVisualizzaSegnalazione_Exception() throws Exception {
    when(segnalazioniService.findById(999L)).thenThrow(new Exception("Segnalazione non trovata"));

    mockMvc.perform(get("/api/segnalazioni/999")
            .with(user(amministratore))
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(segnalazioniService).findById(999L);
  }

  @Test
  void testVisualizzaSegnalazioniPerTipo_Attivita_Success() throws Exception {
    List<Segnalazione> segnalazioni = new ArrayList<>();
    segnalazioni.add(segnalazioneAttivita);

    when(segnalazioniService.getAllSegnalazioniByTipo(false)).thenReturn(segnalazioni);

    mockMvc.perform(get("/api/segnalazioni")
            .param("isForRecensione", "false")
            .with(user(amministratore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(segnalazioniService).getAllSegnalazioniByTipo(false);
  }

  @Test
  void testVisualizzaSegnalazioniPerTipo_Recensioni_Success() throws Exception {
    List<Segnalazione> segnalazioni = new ArrayList<>();
    segnalazioni.add(segnalazioneRecensione);

    when(segnalazioniService.getAllSegnalazioniByTipo(true)).thenReturn(segnalazioni);

    mockMvc.perform(get("/api/segnalazioni")
            .param("isForRecensione", "true")
            .with(user(amministratore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(segnalazioniService).getAllSegnalazioniByTipo(true);
  }

  @Test
  void testChiudiSegnalazione_Success() throws Exception {
    when(segnalazioniService.findById(1L)).thenReturn(segnalazioneAttivita);
    when(segnalazioniService.saveSegnalazione(any(Segnalazione.class)))
        .thenReturn(segnalazioneAttivita);

    mockMvc.perform(delete("/api/segnalazioni/1")
            .with(user(amministratore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(segnalazioniService).findById(1L);
    verify(segnalazioniService).saveSegnalazione(any(Segnalazione.class));
  }

  @Test
  void testChiudiSegnalazione_ConChiarimenti_Success() throws Exception {
    when(segnalazioniService.findById(1L)).thenReturn(segnalazioneAttivita);
    when(segnalazioniService.saveSegnalazione(any(Segnalazione.class)))
        .thenReturn(segnalazioneAttivita);

    mockMvc.perform(delete("/api/segnalazioni/1")
            .param("chiarimenti", "Problema risolto")
            .with(user(amministratore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(segnalazioniService).findById(1L);
    verify(segnalazioniService).saveSegnalazione(any(Segnalazione.class));
  }

  @Test
  void testChiudiSegnalazione_Exception() throws Exception {
    when(segnalazioniService.findById(999L)).thenThrow(new Exception("Segnalazione non trovata"));

    mockMvc.perform(delete("/api/segnalazioni/999")
            .with(user(amministratore))
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(segnalazioniService).findById(999L);
  }
}