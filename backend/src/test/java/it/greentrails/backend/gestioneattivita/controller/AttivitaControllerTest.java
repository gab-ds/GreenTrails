package it.greentrails.backend.gestioneattivita.controller;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.entities.ValoriEcosostenibilita;
import it.greentrails.backend.enums.CategorieAlloggio;
import it.greentrails.backend.enums.CategorieAttivitaTuristica;
import it.greentrails.backend.enums.RuoloUtente;
import it.greentrails.backend.gestioneattivita.service.AttivitaService;
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
import org.springframework.security.test.context.support.WithMockUser;
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
class AttivitaControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AttivitaService attivitaService;

  @MockBean
  private GestioneUtenzeService gestioneUtenzeService;

  @MockBean
  private ValoriEcosostenibilitaService valoriEcosostenibilitaService;

  @MockBean
  private ArchiviazioneService archiviazioneService;


  private Utente utente;
  private Attivita alloggio;
  private Attivita attivitaTuristica;
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
    attivitaTuristica.setNome("Tour Verde");
    attivitaTuristica.setIndirizzo("Via Verde 2");
    attivitaTuristica.setCap("00100");
    attivitaTuristica.setCitta("Roma");
    attivitaTuristica.setProvincia("RM");
    attivitaTuristica.setCoordinate(new Point(41.9028, 12.4964));
    attivitaTuristica.setDescrizioneBreve("Tour ecologico");
    attivitaTuristica.setDescrizioneLunga("Un tour completamente ecologico");
    attivitaTuristica.setValoriEcosostenibilita(valori);
    attivitaTuristica.setPrezzo(50.0);
    attivitaTuristica.setDisponibilita(20);
    attivitaTuristica.setCategoriaAttivitaTuristica(CategorieAttivitaTuristica.ALL_APERTO);
    attivitaTuristica.setMedia("media-uuid-2");
    attivitaTuristica.setEliminata(false);
  }


  @Test
  @WithMockUser(username = "gestore@test.com", roles = {"GESTORE"})
  void testCreaAlloggio_Success() throws Exception {
    MockMultipartFile mockFile = new MockMultipartFile("immagine", "test.jpg", "image/jpeg",
        "test image content".getBytes());

    when(gestioneUtenzeService.findById(1L)).thenReturn(utente);
    when(valoriEcosostenibilitaService.findById(1L)).thenReturn(valori);
    when(attivitaService.saveAttivita(any(Attivita.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    mockMvc.perform(multipart("/api/attivita")
            .file(mockFile)
            .param("alloggio", "true")
            .param("nome", "Hotel Eco")
            .param("indirizzo", "Via Verde 1")
            .param("cap", "00100")
            .param("citta", "Roma")
            .param("provincia", "RM")
            .param("latitudine", "41.9028")
            .param("longitudine", "12.4964")
            .param("descrizioneBreve", "Hotel ecosostenibile")
            .param("descrizioneLunga", "Un hotel completamente ecosostenibile")
            .param("valori", "1")
            .param("categoriaAlloggio", "0")
            .with(user(utente))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.alloggio").value(true))
        .andExpect(jsonPath("$.data.gestore.id").value(1))
        .andExpect(jsonPath("$.data.nome").value("Hotel Eco"))
        .andExpect(jsonPath("$.data.indirizzo").value("Via Verde 1"))
        .andExpect(jsonPath("$.data.cap").value("00100"))
        .andExpect(jsonPath("$.data.citta").value("Roma"))
        .andExpect(jsonPath("$.data.provincia").value("RM"))
        .andExpect(jsonPath("$.data.descrizioneBreve").value("Hotel ecosostenibile"))
        .andExpect(jsonPath("$.data.descrizioneLunga").value("Un hotel completamente ecosostenibile"))
        .andExpect(jsonPath("$.data.valoriEcosostenibilita.id").value(1))
        .andExpect(jsonPath("$.data.media").exists())
        .andExpect(jsonPath("$.data.categoriaAlloggio").exists());

    verify(archiviazioneService).store(anyString(), eq(mockFile));
    verify(attivitaService).saveAttivita(any(Attivita.class));
  }

  @Test
  @WithMockUser(username = "gestore@test.com", roles = {"GESTORE"})
  void testCreaAttivitaTuristica_Success() throws Exception {
    MockMultipartFile mockFile = new MockMultipartFile("immagine", "test.jpg", "image/jpeg",
        "test image content".getBytes());

    when(gestioneUtenzeService.findById(1L)).thenReturn(utente);
    when(valoriEcosostenibilitaService.findById(1L)).thenReturn(valori);
    when(attivitaService.saveAttivita(any(Attivita.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    mockMvc.perform(multipart("/api/attivita")
            .file(mockFile)
            .param("alloggio", "false")
            .param("nome", "Tour Verde")
            .param("indirizzo", "Via Verde 2")
            .param("cap", "00100")
            .param("citta", "Roma")
            .param("provincia", "RM")
            .param("latitudine", "41.9028")
            .param("longitudine", "12.4964")
            .param("descrizioneBreve", "Tour ecologico")
            .param("descrizioneLunga", "Un tour completamente ecologico")
            .param("valori", "1")
            .param("prezzo", "50.0")
            .param("disponibilita", "20")
            .param("categoriaAttivitaTuristica", "0")
            .with(user(utente))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.alloggio").value(false))
        .andExpect(jsonPath("$.data.gestore.id").value(1))
        .andExpect(jsonPath("$.data.nome").value("Tour Verde"))
        .andExpect(jsonPath("$.data.indirizzo").value("Via Verde 2"))
        .andExpect(jsonPath("$.data.cap").value("00100"))
        .andExpect(jsonPath("$.data.citta").value("Roma"))
        .andExpect(jsonPath("$.data.provincia").value("RM"))
        .andExpect(jsonPath("$.data.descrizioneBreve").value("Tour ecologico"))
        .andExpect(jsonPath("$.data.descrizioneLunga").value("Un tour completamente ecologico"))
        .andExpect(jsonPath("$.data.valoriEcosostenibilita.id").value(1))
        .andExpect(jsonPath("$.data.media").exists())
        .andExpect(jsonPath("$.data.prezzo").value(50.0))
        .andExpect(jsonPath("$.data.disponibilita").value(20))
        .andExpect(jsonPath("$.data.categoriaAttivitaTuristica").exists());

    verify(archiviazioneService).store(anyString(), eq(mockFile));
    verify(attivitaService).saveAttivita(any(Attivita.class));
  }

  @Test
  @WithMockUser(username = "gestore@test.com", roles = {"GESTORE"})
  void testCreaAlloggio_SenzaCategoria() throws Exception {
    MockMultipartFile mockFile = new MockMultipartFile("immagine", "test.jpg", "image/jpeg",
        "test image content".getBytes());

    when(gestioneUtenzeService.findById(1L)).thenReturn(utente);
    when(valoriEcosostenibilitaService.findById(1L)).thenReturn(valori);
    doNothing().when(archiviazioneService).store(anyString(), any());

    mockMvc.perform(multipart("/api/attivita")
            .file(mockFile)
            .param("alloggio", "true")
            .param("nome", "Hotel Eco")
            .param("indirizzo", "Via Verde 1")
            .param("cap", "00100")
            .param("citta", "Roma")
            .param("provincia", "RM")
            .param("latitudine", "41.9028")
            .param("longitudine", "12.4964")
            .param("descrizioneBreve", "Hotel ecosostenibile")
            .param("descrizioneLunga", "Un hotel completamente ecosostenibile")
            .param("valori", "1")
            .with(user(utente))
            .with(csrf()))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "gestore@test.com", roles = {"GESTORE"})
  void testCreaAttivitaTuristica_SenzaDisponibilita() throws Exception {
    MockMultipartFile mockFile = new MockMultipartFile("immagine", "test.jpg", "image/jpeg",
        "test image content".getBytes());

    when(gestioneUtenzeService.findById(1L)).thenReturn(utente);
    when(valoriEcosostenibilitaService.findById(1L)).thenReturn(valori);
    doNothing().when(archiviazioneService).store(anyString(), any());

    mockMvc.perform(multipart("/api/attivita")
            .file(mockFile)
            .param("alloggio", "false")
            .param("nome", "Tour Verde")
            .param("indirizzo", "Via Verde 2")
            .param("cap", "00100")
            .param("citta", "Roma")
            .param("provincia", "RM")
            .param("latitudine", "41.9028")
            .param("longitudine", "12.4964")
            .param("descrizioneBreve", "Tour ecologico")
            .param("descrizioneLunga", "Un tour completamente ecologico")
            .param("valori", "1")
            .param("prezzo", "50.0")
            .param("categoriaAttivitaTuristica", "0")
            .with(user(utente))
            .with(csrf()))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "gestore@test.com", roles = {"GESTORE"})
  void testCreaAttivitaTuristica_SenzaPrezzo() throws Exception {
    MockMultipartFile mockFile = new MockMultipartFile("immagine", "test.jpg", "image/jpeg",
        "test image content".getBytes());

    when(gestioneUtenzeService.findById(1L)).thenReturn(utente);
    when(valoriEcosostenibilitaService.findById(1L)).thenReturn(valori);
    doNothing().when(archiviazioneService).store(anyString(), any());

    mockMvc.perform(multipart("/api/attivita")
            .file(mockFile)
            .param("alloggio", "false")
            .param("nome", "Tour Verde")
            .param("indirizzo", "Via Verde 2")
            .param("cap", "00100")
            .param("citta", "Roma")
            .param("provincia", "RM")
            .param("latitudine", "41.9028")
            .param("longitudine", "12.4964")
            .param("descrizioneBreve", "Tour ecologico")
            .param("descrizioneLunga", "Un tour completamente ecologico")
            .param("valori", "1")
            .param("disponibilita", "20")
            .param("categoriaAttivitaTuristica", "0")
            .with(user(utente))
            .with(csrf()))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "gestore@test.com", roles = {"GESTORE"})
  void testCreaAttivitaTuristica_SenzaCategoria() throws Exception {
    MockMultipartFile mockFile = new MockMultipartFile("immagine", "test.jpg", "image/jpeg",
        "test image content".getBytes());

    when(gestioneUtenzeService.findById(1L)).thenReturn(utente);
    when(valoriEcosostenibilitaService.findById(1L)).thenReturn(valori);
    doNothing().when(archiviazioneService).store(anyString(), any());

    mockMvc.perform(multipart("/api/attivita")
            .file(mockFile)
            .param("alloggio", "false")
            .param("nome", "Tour Verde")
            .param("indirizzo", "Via Verde 2")
            .param("cap", "00100")
            .param("citta", "Roma")
            .param("provincia", "RM")
            .param("latitudine", "41.9028")
            .param("longitudine", "12.4964")
            .param("descrizioneBreve", "Tour ecologico")
            .param("descrizioneLunga", "Un tour completamente ecologico")
            .param("valori", "1")
            .param("prezzo", "50.0")
            .param("disponibilita", "20")
            .with(user(utente))
            .with(csrf()))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "gestore@test.com", roles = {"GESTORE"})
  void testCreaAttivita_Exception() throws Exception {
    MockMultipartFile mockFile = new MockMultipartFile("immagine", "test.jpg", "image/jpeg",
        "test image content".getBytes());

    when(gestioneUtenzeService.findById(1L)).thenThrow(new RuntimeException("Errore DB"));

    mockMvc.perform(multipart("/api/attivita")
            .file(mockFile)
            .param("alloggio", "true")
            .param("nome", "Hotel Eco")
            .param("indirizzo", "Via Verde 1")
            .param("cap", "00100")
            .param("citta", "Roma")
            .param("provincia", "RM")
            .param("latitudine", "41.9028")
            .param("longitudine", "12.4964")
            .param("descrizioneBreve", "Hotel ecosostenibile")
            .param("descrizioneLunga", "Un hotel completamente ecosostenibile")
            .param("valori", "1")
            .param("categoriaAlloggio", "0")
            .with(user(utente))
            .with(csrf()))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @WithMockUser(username = "gestore@test.com", roles = {"GESTORE"})
  void testCreaAttivita_ValoriNotFound() throws Exception {
    MockMultipartFile mockFile = new MockMultipartFile("immagine", "test.jpg", "image/jpeg",
        "test image content".getBytes());

    when(gestioneUtenzeService.findById(1L)).thenReturn(utente);
    when(valoriEcosostenibilitaService.findById(1L)).thenThrow(new RuntimeException("Valori non trovati"));

    mockMvc.perform(multipart("/api/attivita")
            .file(mockFile)
            .param("alloggio", "true")
            .param("nome", "Hotel Eco")
            .param("indirizzo", "Via Verde 1")
            .param("cap", "00100")
            .param("citta", "Roma")
            .param("provincia", "RM")
            .param("latitudine", "41.9028")
            .param("longitudine", "12.4964")
            .param("descrizioneBreve", "Hotel ecosostenibile")
            .param("descrizioneLunga", "Un hotel completamente ecosostenibile")
            .param("valori", "1")
            .param("categoriaAlloggio", "0")
            .with(user(utente))
            .with(csrf()))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @WithMockUser(username = "gestore@test.com", roles = {"GESTORE"})
  void testCreaAttivita_ArchiviazioneException() throws Exception {
    MockMultipartFile mockFile = new MockMultipartFile("immagine", "test.jpg", "image/jpeg",
        "test image content".getBytes());

    when(gestioneUtenzeService.findById(1L)).thenReturn(utente);
    when(valoriEcosostenibilitaService.findById(1L)).thenReturn(valori);
    doThrow(new RuntimeException("Errore storage")).when(archiviazioneService).store(anyString(), any());

    mockMvc.perform(multipart("/api/attivita")
            .file(mockFile)
            .param("alloggio", "true")
            .param("nome", "Hotel Eco")
            .param("indirizzo", "Via Verde 1")
            .param("cap", "00100")
            .param("citta", "Roma")
            .param("provincia", "RM")
            .param("latitudine", "41.9028")
            .param("longitudine", "12.4964")
            .param("descrizioneBreve", "Hotel ecosostenibile")
            .param("descrizioneLunga", "Un hotel completamente ecosostenibile")
            .param("valori", "1")
            .param("categoriaAlloggio", "0")
            .with(user(utente))
            .with(csrf()))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @WithMockUser(username = "gestore@test.com", roles = {"GESTORE"})
  void testCreaAttivita_SaveException() throws Exception {
    MockMultipartFile mockFile = new MockMultipartFile("immagine", "test.jpg", "image/jpeg",
        "test image content".getBytes());

    when(gestioneUtenzeService.findById(1L)).thenReturn(utente);
    when(valoriEcosostenibilitaService.findById(1L)).thenReturn(valori);
    when(attivitaService.saveAttivita(any(Attivita.class))).thenThrow(new RuntimeException("Errore salvataggio"));

    mockMvc.perform(multipart("/api/attivita")
            .file(mockFile)
            .param("alloggio", "true")
            .param("nome", "Hotel Eco")
            .param("indirizzo", "Via Verde 1")
            .param("cap", "00100")
            .param("citta", "Roma")
            .param("provincia", "RM")
            .param("latitudine", "41.9028")
            .param("longitudine", "12.4964")
            .param("descrizioneBreve", "Hotel ecosostenibile")
            .param("descrizioneLunga", "Un hotel completamente ecosostenibile")
            .param("valori", "1")
            .param("categoriaAlloggio", "0")
            .with(user(utente))
            .with(csrf()))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @WithMockUser(username = "gestore@test.com", roles = {"GESTORE"})
  void testVisualizzaAttivita_Success() throws Exception {
    when(attivitaService.findById(1L)).thenReturn(alloggio);

    mockMvc.perform(get("/api/attivita/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.nome").value("Hotel Eco"));

    verify(attivitaService).findById(1L);
  }

  @Test
  void testVisualizzaAttivita_Exception() throws Exception {
    when(attivitaService.findById(1L)).thenThrow(new RuntimeException("Attività non trovata"));

    mockMvc.perform(get("/api/attivita/1"))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @WithMockUser(username = "gestore@test.com", roles = {"GESTORE"})
  void testVisualizzaAttivitaPerGestore_Success() throws Exception {
    List<Attivita> attivita = Arrays.asList(alloggio, attivitaTuristica);
    when(attivitaService.findAllAttivitaByGestore(1L)).thenReturn(attivita);

    mockMvc.perform(get("/api/attivita/perGestore")
            .with(user(utente)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data[0].id").value(1));

    verify(attivitaService).findAllAttivitaByGestore(1L);
  }

  @Test
  @WithMockUser(username = "gestore@test.com", roles = {"GESTORE"})
  void testVisualizzaAttivitaPerGestore_Exception() throws Exception {
    when(attivitaService.findAllAttivitaByGestore(1L))
        .thenThrow(new RuntimeException("Errore DB"));

    mockMvc.perform(get("/api/attivita/perGestore")
            .with(user(utente)))
        .andExpect(status().isInternalServerError());
  }

  @Test
  void testVisualizzaAttivitaPerPrezzo_ConLimite() throws Exception {
    List<Attivita> attivita = Arrays.asList(attivitaTuristica);
    when(attivitaService.getAttivitaTuristicheEconomiche(5)).thenReturn(attivita);

    mockMvc.perform(get("/api/attivita/perPrezzo")
            .param("limite", "5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data[0].id").value(2));

    verify(attivitaService).getAttivitaTuristicheEconomiche(5);
  }

  @Test
  void testVisualizzaAttivitaPerPrezzo_SenzaLimite() throws Exception {
    List<Attivita> attivita = Arrays.asList(attivitaTuristica);
    when(attivitaService.getAttivitaTuristicheEconomiche(10)).thenReturn(attivita);

    mockMvc.perform(get("/api/attivita/perPrezzo"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data[0].id").value(2));

    verify(attivitaService).getAttivitaTuristicheEconomiche(10);
  }

  @Test
  @WithMockUser(username = "gestore@test.com", roles = {"GESTORE"})
  void testModificaAlloggio_Success() throws Exception {
    when(gestioneUtenzeService.findById(1L)).thenReturn(utente);
    when(attivitaService.findById(1L)).thenReturn(alloggio);
    when(valoriEcosostenibilitaService.findById(1L)).thenReturn(valori);
    when(attivitaService.saveAttivita(any(Attivita.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    mockMvc.perform(post("/api/attivita/1")
            .param("nome", "Hotel Eco Updated")
            .param("indirizzo", "Via Verde 1")
            .param("cap", "00100")
            .param("citta", "Roma")
            .param("provincia", "RM")
            .param("latitudine", "41.9028")
            .param("longitudine", "12.4964")
            .param("descrizioneBreve", "Hotel ecosostenibile")
            .param("descrizioneLunga", "Un hotel completamente ecosostenibile")
            .param("valori", "1")
            .param("categoriaAlloggio", "0")
            .with(user(utente))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.nome").value("Hotel Eco Updated"))
        .andExpect(jsonPath("$.data.indirizzo").value("Via Verde 1"))
        .andExpect(jsonPath("$.data.cap").value("00100"))
        .andExpect(jsonPath("$.data.citta").value("Roma"))
        .andExpect(jsonPath("$.data.provincia").value("RM"))
        .andExpect(jsonPath("$.data.descrizioneBreve").value("Hotel ecosostenibile"))
        .andExpect(jsonPath("$.data.descrizioneLunga").value("Un hotel completamente ecosostenibile"))
        .andExpect(jsonPath("$.data.valoriEcosostenibilita.id").value(1))
        .andExpect(jsonPath("$.data.categoriaAlloggio").exists());

    verify(attivitaService).saveAttivita(any(Attivita.class));
  }

  @Test
  @WithMockUser(username = "gestore@test.com", roles = {"GESTORE"})
  void testModificaAttivitaTuristica_Success() throws Exception {
    when(gestioneUtenzeService.findById(1L)).thenReturn(utente);
    when(attivitaService.findById(2L)).thenReturn(attivitaTuristica);
    when(valoriEcosostenibilitaService.findById(1L)).thenReturn(valori);
    when(attivitaService.saveAttivita(any(Attivita.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    mockMvc.perform(post("/api/attivita/2")
            .param("nome", "Tour Verde Updated")
            .param("indirizzo", "Via Verde 2")
            .param("cap", "00100")
            .param("citta", "Roma")
            .param("provincia", "RM")
            .param("latitudine", "41.9028")
            .param("longitudine", "12.4964")
            .param("descrizioneBreve", "Tour ecologico")
            .param("descrizioneLunga", "Un tour completamente ecologico")
            .param("valori", "1")
            .param("prezzo", "60.0")
            .param("disponibilita", "25")
            .param("categoriaAttivitaTuristica", "0")
            .with(user(utente))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.nome").value("Tour Verde Updated"))
        .andExpect(jsonPath("$.data.indirizzo").value("Via Verde 2"))
        .andExpect(jsonPath("$.data.cap").value("00100"))
        .andExpect(jsonPath("$.data.citta").value("Roma"))
        .andExpect(jsonPath("$.data.provincia").value("RM"))
        .andExpect(jsonPath("$.data.descrizioneBreve").value("Tour ecologico"))
        .andExpect(jsonPath("$.data.descrizioneLunga").value("Un tour completamente ecologico"))
        .andExpect(jsonPath("$.data.valoriEcosostenibilita.id").value(1))
        .andExpect(jsonPath("$.data.prezzo").value(60.0))
        .andExpect(jsonPath("$.data.disponibilita").value(25))
        .andExpect(jsonPath("$.data.categoriaAttivitaTuristica").exists());

    verify(attivitaService).saveAttivita(any(Attivita.class));
  }

  @Test
  @WithMockUser(username = "gestore@test.com", roles = {"GESTORE"})
  void testModificaAttivita_GestoreDiverso() throws Exception {
    Utente altroUtente = new Utente();
    altroUtente.setId(2L);

    Attivita attivitaAltroGestore = new Attivita();
    attivitaAltroGestore.setId(1L);
    attivitaAltroGestore.setGestore(altroUtente);
    attivitaAltroGestore.setEliminata(false);

    when(gestioneUtenzeService.findById(1L)).thenReturn(utente);
    when(attivitaService.findById(1L)).thenReturn(attivitaAltroGestore);

    mockMvc.perform(post("/api/attivita/1")
            .param("nome", "Hotel Eco")
            .param("indirizzo", "Via Verde 1")
            .param("cap", "00100")
            .param("citta", "Roma")
            .param("provincia", "RM")
            .param("latitudine", "41.9028")
            .param("longitudine", "12.4964")
            .param("descrizioneBreve", "Hotel ecosostenibile")
            .param("descrizioneLunga", "Un hotel completamente ecosostenibile")
            .param("valori", "1")
            .param("categoriaAlloggio", "0")
            .with(user(utente))
            .with(csrf()))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "gestore@test.com", roles = {"GESTORE"})
  void testModificaAttivita_AttivitaEliminata() throws Exception {
    Attivita alloggioEliminato = new Attivita();
    alloggioEliminato.setId(1L);
    alloggioEliminato.setAlloggio(true);
    alloggioEliminato.setGestore(utente);
    alloggioEliminato.setEliminata(true);

    when(gestioneUtenzeService.findById(1L)).thenReturn(utente);
    when(attivitaService.findById(1L)).thenReturn(alloggioEliminato);

    mockMvc.perform(post("/api/attivita/1")
            .param("nome", "Hotel Eco")
            .param("indirizzo", "Via Verde 1")
            .param("cap", "00100")
            .param("citta", "Roma")
            .param("provincia", "RM")
            .param("latitudine", "41.9028")
            .param("longitudine", "12.4964")
            .param("descrizioneBreve", "Hotel ecosostenibile")
            .param("descrizioneLunga", "Un hotel completamente ecosostenibile")
            .param("valori", "1")
            .param("categoriaAlloggio", "0")
            .with(user(utente))
            .with(csrf()))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "gestore@test.com", roles = {"GESTORE"})
  void testModificaAlloggio_SenzaCategoria() throws Exception {
    when(gestioneUtenzeService.findById(1L)).thenReturn(utente);
    when(attivitaService.findById(1L)).thenReturn(alloggio);

    mockMvc.perform(post("/api/attivita/1")
            .param("nome", "Hotel Eco")
            .param("indirizzo", "Via Verde 1")
            .param("cap", "00100")
            .param("citta", "Roma")
            .param("provincia", "RM")
            .param("latitudine", "41.9028")
            .param("longitudine", "12.4964")
            .param("descrizioneBreve", "Hotel ecosostenibile")
            .param("descrizioneLunga", "Un hotel completamente ecosostenibile")
            .param("valori", "1")
            .with(user(utente))
            .with(csrf()))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "gestore@test.com", roles = {"GESTORE"})
  void testModificaAttivitaTuristica_SenzaDisponibilita() throws Exception {
    when(gestioneUtenzeService.findById(1L)).thenReturn(utente);
    when(attivitaService.findById(2L)).thenReturn(attivitaTuristica);

    mockMvc.perform(post("/api/attivita/2")
            .param("nome", "Tour Verde")
            .param("indirizzo", "Via Verde 2")
            .param("cap", "00100")
            .param("citta", "Roma")
            .param("provincia", "RM")
            .param("latitudine", "41.9028")
            .param("longitudine", "12.4964")
            .param("descrizioneBreve", "Tour ecologico")
            .param("descrizioneLunga", "Un tour completamente ecologico")
            .param("valori", "1")
            .param("prezzo", "50.0")
            .param("categoriaAttivitaTuristica", "0")
            .with(user(utente))
            .with(csrf()))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "gestore@test.com", roles = {"GESTORE"})
  void testModificaAttivitaTuristica_SenzaPrezzo() throws Exception {
    when(gestioneUtenzeService.findById(1L)).thenReturn(utente);
    when(attivitaService.findById(2L)).thenReturn(attivitaTuristica);

    mockMvc.perform(post("/api/attivita/2")
            .param("nome", "Tour Verde")
            .param("indirizzo", "Via Verde 2")
            .param("cap", "00100")
            .param("citta", "Roma")
            .param("provincia", "RM")
            .param("latitudine", "41.9028")
            .param("longitudine", "12.4964")
            .param("descrizioneBreve", "Tour ecologico")
            .param("descrizioneLunga", "Un tour completamente ecologico")
            .param("valori", "1")
            .param("disponibilita", "20")
            .param("categoriaAttivitaTuristica", "0")
            .with(user(utente))
            .with(csrf()))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "gestore@test.com", roles = {"GESTORE"})
  void testModificaAttivitaTuristica_SenzaCategoria() throws Exception {
    when(gestioneUtenzeService.findById(1L)).thenReturn(utente);
    when(attivitaService.findById(2L)).thenReturn(attivitaTuristica);

    mockMvc.perform(post("/api/attivita/2")
            .param("nome", "Tour Verde")
            .param("indirizzo", "Via Verde 2")
            .param("cap", "00100")
            .param("citta", "Roma")
            .param("provincia", "RM")
            .param("latitudine", "41.9028")
            .param("longitudine", "12.4964")
            .param("descrizioneBreve", "Tour ecologico")
            .param("descrizioneLunga", "Un tour completamente ecologico")
            .param("valori", "1")
            .param("prezzo", "50.0")
            .param("disponibilita", "20")
            .with(user(utente))
            .with(csrf()))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "gestore@test.com", roles = {"GESTORE"})
  void testModificaAttivita_Exception() throws Exception {
    when(gestioneUtenzeService.findById(1L)).thenThrow(new RuntimeException("Errore DB"));

    mockMvc.perform(post("/api/attivita/1")
            .param("nome", "Hotel Eco")
            .param("indirizzo", "Via Verde 1")
            .param("cap", "00100")
            .param("citta", "Roma")
            .param("provincia", "RM")
            .param("latitudine", "41.9028")
            .param("longitudine", "12.4964")
            .param("descrizioneBreve", "Hotel ecosostenibile")
            .param("descrizioneLunga", "Un hotel completamente ecosostenibile")
            .param("valori", "1")
            .param("categoriaAlloggio", "0")
            .with(user(utente))
            .with(csrf()))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @WithMockUser(username = "gestore@test.com", roles = {"GESTORE"})
  void testModificaAttivita_ValoriException() throws Exception {
    when(gestioneUtenzeService.findById(1L)).thenReturn(utente);
    when(attivitaService.findById(1L)).thenReturn(alloggio);
    when(valoriEcosostenibilitaService.findById(1L)).thenThrow(new RuntimeException("Valori non trovati"));

    mockMvc.perform(post("/api/attivita/1")
            .param("nome", "Hotel Eco")
            .param("indirizzo", "Via Verde 1")
            .param("cap", "00100")
            .param("citta", "Roma")
            .param("provincia", "RM")
            .param("latitudine", "41.9028")
            .param("longitudine", "12.4964")
            .param("descrizioneBreve", "Hotel ecosostenibile")
            .param("descrizioneLunga", "Un hotel completamente ecosostenibile")
            .param("valori", "1")
            .param("categoriaAlloggio", "0")
            .with(user(utente))
            .with(csrf()))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @WithMockUser(username = "gestore@test.com", roles = {"GESTORE"})
  void testModificaAttivita_SaveException() throws Exception {
    when(gestioneUtenzeService.findById(1L)).thenReturn(utente);
    when(attivitaService.findById(1L)).thenReturn(alloggio);
    when(valoriEcosostenibilitaService.findById(1L)).thenReturn(valori);
    when(attivitaService.saveAttivita(any(Attivita.class))).thenThrow(new RuntimeException("Errore salvataggio"));

    mockMvc.perform(post("/api/attivita/1")
            .param("nome", "Hotel Eco")
            .param("indirizzo", "Via Verde 1")
            .param("cap", "00100")
            .param("citta", "Roma")
            .param("provincia", "RM")
            .param("latitudine", "41.9028")
            .param("longitudine", "12.4964")
            .param("descrizioneBreve", "Hotel ecosostenibile")
            .param("descrizioneLunga", "Un hotel completamente ecosostenibile")
            .param("valori", "1")
            .param("categoriaAlloggio", "0")
            .with(user(utente))
            .with(csrf()))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @WithMockUser(username = "gestore@test.com", roles = {"GESTORE"})
  void testCancellaAttivita_Success() throws Exception {
    when(attivitaService.findById(1L)).thenReturn(alloggio);
    when(attivitaService.deleteAttivita(alloggio)).thenReturn(true);

    mockMvc.perform(delete("/api/attivita/1")
            .with(user(utente))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").value(true));

    verify(attivitaService).deleteAttivita(alloggio);
  }

  @Test
  @WithMockUser(username = "gestore@test.com", roles = {"GESTORE"})
  void testCancellaAttivita_GestoreDiverso() throws Exception {
    Utente altroUtente = new Utente();
    altroUtente.setId(2L);

    Attivita attivitaAltroGestore = new Attivita();
    attivitaAltroGestore.setId(1L);
    attivitaAltroGestore.setGestore(altroUtente);

    when(attivitaService.findById(1L)).thenReturn(attivitaAltroGestore);

    mockMvc.perform(delete("/api/attivita/1")
            .with(user(utente))
            .with(csrf()))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(username = "gestore@test.com", roles = {"GESTORE"})
  void testCancellaAttivita_Exception() throws Exception {
    when(attivitaService.findById(1L)).thenThrow(new RuntimeException("Errore DB"));

    mockMvc.perform(delete("/api/attivita/1")
            .with(user(utente))
            .with(csrf()))
        .andExpect(status().isInternalServerError());
  }

  @Test
  void testGetAlloggi_ConLimite() throws Exception {
    List<Attivita> alloggi = Arrays.asList(alloggio);
    when(attivitaService.getAlloggi(3)).thenReturn(alloggi);

    mockMvc.perform(get("/api/attivita/alloggi")
            .param("limite", "3"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data[0].id").value(1));

    verify(attivitaService).getAlloggi(3);
  }

  @Test
  void testGetAlloggi_SenzaLimite() throws Exception {
    List<Attivita> alloggi = Arrays.asList(alloggio);
    when(attivitaService.getAlloggi(5)).thenReturn(alloggi);

    mockMvc.perform(get("/api/attivita/alloggi"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data[0].id").value(1));

    verify(attivitaService).getAlloggi(5);
  }

  @Test
  void testGetAttivitaTuristiche_ConLimite() throws Exception {
    List<Attivita> attivita = Arrays.asList(attivitaTuristica);
    when(attivitaService.getAttivitaTuristiche(3)).thenReturn(attivita);

    mockMvc.perform(get("/api/attivita/attivitaTuristiche")
            .param("limite", "3"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data[0].id").value(2));

    verify(attivitaService).getAttivitaTuristiche(3);
  }

  @Test
  void testGetAttivitaTuristiche_SenzaLimite() throws Exception {
    List<Attivita> attivita = Arrays.asList(attivitaTuristica);
    when(attivitaService.getAttivitaTuristiche(5)).thenReturn(attivita);

    mockMvc.perform(get("/api/attivita/attivitaTuristiche"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data[0].id").value(2));

    verify(attivitaService).getAttivitaTuristiche(5);
  }

  @Test
  void testFindAll() throws Exception {
    List<Attivita> attivita = Arrays.asList(alloggio, attivitaTuristica);
    when(attivitaService.findAll()).thenReturn(attivita);

    mockMvc.perform(get("/api/attivita/all"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[1].id").value(2));

    verify(attivitaService).findAll();
  }
}


