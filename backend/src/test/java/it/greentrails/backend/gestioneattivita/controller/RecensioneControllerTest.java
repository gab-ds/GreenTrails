package it.greentrails.backend.gestioneattivita.controller;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Recensione;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.entities.ValoriEcosostenibilita;
import it.greentrails.backend.enums.CategorieAlloggio;
import it.greentrails.backend.enums.RuoloUtente;
import it.greentrails.backend.gestioneattivita.service.AttivitaService;
import it.greentrails.backend.gestioneattivita.service.RecensioneService;
import it.greentrails.backend.gestioneattivita.service.ValoriEcosostenibilitaService;
import it.greentrails.backend.gestioneupload.service.ArchiviazioneService;
import it.greentrails.backend.gestioneutenze.service.GestioneUtenzeService;
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

import java.util.Arrays;
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
class RecensioneControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private RecensioneService recensioneService;

  @MockBean
  private AttivitaService attivitaService;

  @MockBean
  private GestioneUtenzeService gestioneUtenzeService;

  @MockBean
  private ValoriEcosostenibilitaService valoriEcosostenibilitaService;

  @MockBean
  private ArchiviazioneService archiviazioneService;

  private Utente visitatore;
  private Utente amministratore;
  private Attivita alloggio;
  private Recensione recensione;
  private ValoriEcosostenibilita valori;

  @BeforeEach
  void setUp() {
    visitatore = new Utente();
    visitatore.setId(1L);
    visitatore.setEmail("visitatore@test.com");
    visitatore.setRuolo(RuoloUtente.VISITATORE);

    amministratore = new Utente();
    amministratore.setId(2L);
    amministratore.setEmail("admin@test.com");
    amministratore.setRuolo(RuoloUtente.AMMINISTRATORE);

    valori = new ValoriEcosostenibilita();
    valori.setId(1L);

    alloggio = new Attivita();
    alloggio.setId(1L);
    alloggio.setAlloggio(true);
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
  }

  @Test
  void testCreaRecensione_ConImmagine_Success() throws Exception {
    MockMultipartFile mockFile = new MockMultipartFile("immagine", "test.jpg", "image/jpeg",
        "test image content".getBytes());

    when(attivitaService.findById(1L)).thenReturn(alloggio);
    when(gestioneUtenzeService.findById(1L)).thenReturn(visitatore);
    when(valoriEcosostenibilitaService.findById(1L)).thenReturn(valori);
    when(recensioneService.saveRecensione(any(Recensione.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    mockMvc.perform(multipart("/api/recensioni")
            .file(mockFile)
            .param("idAttivita", "1")
            .param("valutazioneStelleEsperienza", "5")
            .param("descrizione", "Esperienza fantastica!")
            .param("idValori", "1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.visitatore.id").value(1))
        .andExpect(jsonPath("$.data.attivita.id").value(1))
        .andExpect(jsonPath("$.data.valutazioneStelleEsperienza").value(5))
        .andExpect(jsonPath("$.data.descrizione").value("Esperienza fantastica!"))
        .andExpect(jsonPath("$.data.media").exists())
        .andExpect(jsonPath("$.data.valoriEcosostenibilita.id").value(1));

    verify(attivitaService).findById(1L);
    verify(gestioneUtenzeService).findById(1L);
    verify(valoriEcosostenibilitaService).findById(1L);
    verify(archiviazioneService).store(anyString(), eq(mockFile));
    verify(recensioneService).saveRecensione(any(Recensione.class));
  }

  @Test
  void testCreaRecensione_SenzaImmagine_Success() throws Exception {
    when(attivitaService.findById(1L)).thenReturn(alloggio);
    when(gestioneUtenzeService.findById(1L)).thenReturn(visitatore);
    when(valoriEcosostenibilitaService.findById(1L)).thenReturn(valori);
    when(recensioneService.saveRecensione(any(Recensione.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    mockMvc.perform(multipart("/api/recensioni")
            .param("idAttivita", "1")
            .param("valutazioneStelleEsperienza", "5")
            .param("descrizione", "Esperienza fantastica!")
            .param("idValori", "1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.visitatore.id").value(1))
        .andExpect(jsonPath("$.data.attivita.id").value(1))
        .andExpect(jsonPath("$.data.valutazioneStelleEsperienza").value(5))
        .andExpect(jsonPath("$.data.descrizione").value("Esperienza fantastica!"))
        .andExpect(jsonPath("$.data.valoriEcosostenibilita.id").value(1));

    verify(attivitaService).findById(1L);
    verify(gestioneUtenzeService).findById(1L);
    verify(valoriEcosostenibilitaService).findById(1L);
    verify(archiviazioneService, never()).store(anyString(), any());
    verify(recensioneService).saveRecensione(any(Recensione.class));
  }

  @Test
  void testCreaRecensione_ConImmagineVuota_Success() throws Exception {
    MockMultipartFile mockFile = new MockMultipartFile("immagine", "test.jpg", "image/jpeg",
        new byte[0]);

    when(attivitaService.findById(1L)).thenReturn(alloggio);
    when(gestioneUtenzeService.findById(1L)).thenReturn(visitatore);
    when(valoriEcosostenibilitaService.findById(1L)).thenReturn(valori);
    when(recensioneService.saveRecensione(any(Recensione.class))).thenReturn(recensione);

    mockMvc.perform(multipart("/api/recensioni")
            .file(mockFile)
            .param("idAttivita", "1")
            .param("valutazioneStelleEsperienza", "5")
            .param("descrizione", "Esperienza fantastica!")
            .param("idValori", "1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(attivitaService).findById(1L);
    verify(gestioneUtenzeService).findById(1L);
    verify(valoriEcosostenibilitaService).findById(1L);
    verify(archiviazioneService, never()).store(anyString(), any());
    verify(recensioneService).saveRecensione(any(Recensione.class));
  }

  @Test
  void testCreaRecensione_Exception() throws Exception {
    when(attivitaService.findById(1L)).thenThrow(new Exception("Attività non trovata"));

    mockMvc.perform(multipart("/api/recensioni")
            .param("idAttivita", "1")
            .param("valutazioneStelleEsperienza", "5")
            .param("descrizione", "Esperienza fantastica!")
            .param("idValori", "1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(attivitaService).findById(1L);
  }

  @Test
  void testVisualizzaRecensione_Success() throws Exception {
    when(recensioneService.findById(1L)).thenReturn(recensione);

    mockMvc.perform(get("/api/recensioni/1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.visitatore.id").value(1))
        .andExpect(jsonPath("$.data.attivita.id").value(1));

    verify(recensioneService).findById(1L);
  }

  @Test
  void testVisualizzaRecensione_Exception() throws Exception {
    when(recensioneService.findById(999L)).thenThrow(new Exception("Recensione non trovata"));

    mockMvc.perform(get("/api/recensioni/999")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(recensioneService).findById(999L);
  }

  @Test
  void testVisualizzaRecensioniPerAttivita_Success() throws Exception {
    List<Recensione> recensioni = Arrays.asList(recensione);
    when(attivitaService.findById(1L)).thenReturn(alloggio);
    when(recensioneService.getRecensioniByAttivita(alloggio)).thenReturn(recensioni);

    mockMvc.perform(get("/api/recensioni/perAttivita/1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data[0].id").value(1));

    verify(attivitaService).findById(1L);
    verify(recensioneService).getRecensioniByAttivita(alloggio);
  }

  @Test
  void testVisualizzaRecensioniPerAttivita_Exception() throws Exception {
    when(attivitaService.findById(1L)).thenThrow(new Exception("Attività non trovata"));

    mockMvc.perform(get("/api/recensioni/perAttivita/1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(attivitaService).findById(1L);
  }

  @Test
  void testCancellaRecensione_ProprioUtente_Success() throws Exception {
    when(recensioneService.findById(1L)).thenReturn(recensione);
    when(recensioneService.deleteRecensione(recensione)).thenReturn(true);

    mockMvc.perform(delete("/api/recensioni/1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").value(true));

    verify(recensioneService).findById(1L);
    verify(recensioneService).deleteRecensione(recensione);
  }

  @Test
  void testCancellaRecensione_Amministratore_Success() throws Exception {
    when(recensioneService.findById(1L)).thenReturn(recensione);
    when(recensioneService.deleteRecensione(recensione)).thenReturn(true);

    mockMvc.perform(delete("/api/recensioni/1")
            .with(user(amministratore))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").value(true));

    verify(recensioneService).findById(1L);
    verify(recensioneService).deleteRecensione(recensione);
  }

  @Test
  void testCancellaRecensione_AltroUtente_Forbidden() throws Exception {
    Utente altroUtente = new Utente();
    altroUtente.setId(3L);
    altroUtente.setEmail("altro@test.com");
    altroUtente.setRuolo(RuoloUtente.VISITATORE);

    when(recensioneService.findById(1L)).thenReturn(recensione);

    mockMvc.perform(delete("/api/recensioni/1")
            .with(user(altroUtente))
            .with(csrf()))
        .andExpect(status().isForbidden());

    verify(recensioneService).findById(1L);
    verify(recensioneService, never()).deleteRecensione(any());
  }

  @Test
  void testCancellaRecensione_Exception() throws Exception {
    when(recensioneService.findById(1L)).thenThrow(new Exception("Recensione non trovata"));

    mockMvc.perform(delete("/api/recensioni/1")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(recensioneService).findById(1L);
  }
}