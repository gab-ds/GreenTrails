package it.greentrails.backend.gestioneattivita.controller;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Camera;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.entities.ValoriEcosostenibilita;
import it.greentrails.backend.enums.CategorieAlloggio;
import it.greentrails.backend.enums.RuoloUtente;
import it.greentrails.backend.gestioneattivita.service.AttivitaService;
import it.greentrails.backend.gestioneattivita.service.CameraService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.geo.Point;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
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
class CameraControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AttivitaService attivitaService;

  @MockBean
  private CameraService cameraService;

  private Utente utente;
  private Attivita alloggio;
  private Attivita attivitaTuristica;
  private Camera camera;
  private ValoriEcosostenibilita valori;

  @BeforeEach
  void setUp() {
    utente = new Utente();
    utente.setId(1L);
    utente.setEmail("gestore@test.com");
    utente.setRuolo(RuoloUtente.GESTORE_ATTIVITA);

    valori = new ValoriEcosostenibilita();
    valori.setId(1L);

    alloggio = new Attivita();
    alloggio.setId(1L);
    alloggio.setAlloggio(true);
    alloggio.setGestore(utente);
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

    attivitaTuristica = new Attivita();
    attivitaTuristica.setId(2L);
    attivitaTuristica.setAlloggio(false);
    attivitaTuristica.setGestore(utente);

    camera = new Camera();
    camera.setId(1L);
    camera.setAlloggio(alloggio);
    camera.setTipoCamera("Doppia");
    camera.setDisponibilita(10);
    camera.setDescrizione("Camera doppia con vista");
    camera.setCapienza(2);
    camera.setPrezzo(100.0);
  }

  @Test
  void testCreaCamera_Success() throws Exception {
    when(attivitaService.findById(1L)).thenReturn(alloggio);
    when(cameraService.saveCamera(any(Camera.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    mockMvc.perform(post("/api/camere")
            .param("idAlloggio", "1")
            .param("tipoCamera", "Doppia")
            .param("disponibilita", "10")
            .param("descrizione", "Camera doppia con vista")
            .param("capienza", "2")
            .param("prezzo", "100.0")
            .with(user(utente))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.alloggio.id").value(1))
        .andExpect(jsonPath("$.data.tipoCamera").value("Doppia"))
        .andExpect(jsonPath("$.data.disponibilita").value(10))
        .andExpect(jsonPath("$.data.descrizione").value("Camera doppia con vista"))
        .andExpect(jsonPath("$.data.capienza").value(2))
        .andExpect(jsonPath("$.data.prezzo").value(100.0));

    verify(attivitaService).findById(1L);
    verify(cameraService).saveCamera(any(Camera.class));
  }

  @Test
  void testCreaCamera_AlloggioNonTrovato() throws Exception {
    Utente altroUtente = new Utente();
    altroUtente.setId(2L);
    altroUtente.setEmail("altro@test.com");
    altroUtente.setRuolo(RuoloUtente.GESTORE_ATTIVITA);

    when(attivitaService.findById(1L)).thenReturn(alloggio);

    mockMvc.perform(post("/api/camere")
            .param("idAlloggio", "1")
            .param("tipoCamera", "Doppia")
            .param("disponibilita", "10")
            .param("descrizione", "Camera doppia con vista")
            .param("capienza", "2")
            .param("prezzo", "100.0")
            .with(user(altroUtente))
            .with(csrf()))
        .andExpect(status().isNotFound());

    verify(attivitaService).findById(1L);
    verify(cameraService, never()).saveCamera(any(Camera.class));
  }

  @Test
  void testCreaCamera_NonAlloggio() throws Exception {
    when(attivitaService.findById(2L)).thenReturn(attivitaTuristica);

    mockMvc.perform(post("/api/camere")
            .param("idAlloggio", "2")
            .param("tipoCamera", "Doppia")
            .param("disponibilita", "10")
            .param("descrizione", "Camera doppia con vista")
            .param("capienza", "2")
            .param("prezzo", "100.0")
            .with(user(utente))
            .with(csrf()))
        .andExpect(status().isBadRequest());

    verify(attivitaService).findById(2L);
    verify(cameraService, never()).saveCamera(any(Camera.class));
  }

  @Test
  void testCreaCamera_Exception() throws Exception {
    when(attivitaService.findById(1L)).thenThrow(new RuntimeException("Errore DB"));

    mockMvc.perform(post("/api/camere")
            .param("idAlloggio", "1")
            .param("tipoCamera", "Doppia")
            .param("disponibilita", "10")
            .param("descrizione", "Camera doppia con vista")
            .param("capienza", "2")
            .param("prezzo", "100.0")
            .with(user(utente))
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(attivitaService).findById(1L);
  }

  @Test
  void testVisualizzaCamera_Success() throws Exception {
    when(cameraService.findById(1L)).thenReturn(camera);

    mockMvc.perform(get("/api/camere/1")
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.tipoCamera").value("Doppia"))
        .andExpect(jsonPath("$.data.disponibilita").value(10));

    verify(cameraService).findById(1L);
  }

  @Test
  void testVisualizzaCamera_Exception() throws Exception {
    when(cameraService.findById(999L)).thenThrow(new Exception("Camera non trovata"));

    mockMvc.perform(get("/api/camere/999")
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(cameraService).findById(999L);
  }

  @Test
  void testVisualizzaCamerePerAlloggio_Success() throws Exception {
    List<Camera> camere = Arrays.asList(camera);
    when(attivitaService.findById(1L)).thenReturn(alloggio);
    when(cameraService.getCamereByAlloggio(alloggio)).thenReturn(camere);

    mockMvc.perform(get("/api/camere/perAlloggio/1")
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].tipoCamera").value("Doppia"));

    verify(attivitaService).findById(1L);
    verify(cameraService).getCamereByAlloggio(alloggio);
  }

  @Test
  void testVisualizzaCamerePerAlloggio_NonAlloggio() throws Exception {
    when(attivitaService.findById(2L)).thenReturn(attivitaTuristica);

    mockMvc.perform(get("/api/camere/perAlloggio/2")
            .with(csrf()))
        .andExpect(status().isBadRequest());

    verify(attivitaService).findById(2L);
    verify(cameraService, never()).getCamereByAlloggio(any());
  }

  @Test
  void testVisualizzaCamerePerAlloggio_Exception() throws Exception {
    when(attivitaService.findById(1L)).thenThrow(new Exception("Alloggio non trovato"));

    mockMvc.perform(get("/api/camere/perAlloggio/1")
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(attivitaService).findById(1L);
  }

  @Test
  void testCancellaCamera_Success() throws Exception {
    when(cameraService.findById(1L)).thenReturn(camera);
    when(cameraService.deleteCamera(camera)).thenReturn(true);

    mockMvc.perform(delete("/api/camere/1")
            .with(user(utente))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").value(true));

    verify(cameraService).findById(1L);
    verify(cameraService).deleteCamera(camera);
  }

  @Test
  void testCancellaCamera_CameraNonTrovata() throws Exception {
    Utente altroUtente = new Utente();
    altroUtente.setId(2L);
    altroUtente.setEmail("altro@test.com");
    altroUtente.setRuolo(RuoloUtente.GESTORE_ATTIVITA);

    when(cameraService.findById(1L)).thenReturn(camera);

    mockMvc.perform(delete("/api/camere/1")
            .with(user(altroUtente))
            .with(csrf()))
        .andExpect(status().isNotFound());

    verify(cameraService).findById(1L);
    verify(cameraService, never()).deleteCamera(any());
  }

  @Test
  void testCancellaCamera_Exception() throws Exception {
    when(cameraService.findById(1L)).thenThrow(new Exception("Errore DB"));

    mockMvc.perform(delete("/api/camere/1")
            .with(user(utente))
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(cameraService).findById(1L);
  }
}