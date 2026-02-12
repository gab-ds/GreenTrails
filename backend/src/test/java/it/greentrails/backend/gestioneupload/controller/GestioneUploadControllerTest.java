package it.greentrails.backend.gestioneupload.controller;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Recensione;
import it.greentrails.backend.entities.Segnalazione;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.enums.RuoloUtente;
import it.greentrails.backend.gestioneattivita.repository.AttivitaRepository;
import it.greentrails.backend.gestioneattivita.repository.RecensioneRepository;
import it.greentrails.backend.gestionesegnalazioni.repository.SegnalazioniRepository;
import it.greentrails.backend.gestioneupload.exceptions.FileNonTrovatoException;
import it.greentrails.backend.gestioneupload.service.ArchiviazioneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.Optional;
import java.util.stream.Stream;

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
class GestioneUploadControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ArchiviazioneService archiviazioneService;

  @MockBean
  private AttivitaRepository attivitaRepository;

  @MockBean
  private RecensioneRepository recensioneRepository;

  @MockBean
  private SegnalazioniRepository segnalazioniRepository;

  private Utente visitatore;
  private Utente gestore;
  private Utente amministratore;
  private Utente altroUtente;
  private Attivita attivita;
  private Recensione recensione;
  private Segnalazione segnalazione;

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

    altroUtente = new Utente();
    altroUtente.setId(4L);
    altroUtente.setEmail("altro@test.com");
    altroUtente.setRuolo(RuoloUtente.VISITATORE);

    attivita = new Attivita();
    attivita.setId(1L);
    attivita.setGestore(gestore);
    attivita.setMedia("media123");

    recensione = new Recensione();
    recensione.setId(1L);
    recensione.setVisitatore(visitatore);
    recensione.setMedia("media456");

    segnalazione = new Segnalazione();
    segnalazione.setId(1L);
    segnalazione.setUtente(visitatore);
    segnalazione.setMedia("media789");
  }

  @Test
  void testElencaFileCaricati_Success() throws Exception {
    Path file1 = Paths.get("file1.jpg");
    Path file2 = Paths.get("file2.jpg");
    when(archiviazioneService.loadAll("media123"))
        .thenAnswer(invocation -> Stream.of(file1, file2).toList());

    mockMvc.perform(get("/api/file/media123"))
        .andExpect(status().isOk());

    verify(archiviazioneService).loadAll("media123");
  }

  @Test
  void testElencaFileCaricati_MediaVuoto() throws Exception {
    when(archiviazioneService.loadAll("mediaVuoto"))
        .thenAnswer(invocation -> Stream.<Path>empty().toList());

    mockMvc.perform(get("/api/file/mediaVuoto"))
        .andExpect(status().isOk());

    verify(archiviazioneService).loadAll("mediaVuoto");
  }

  @Test
  void testServiFile_Success() throws Exception {
    // Crea un file temporaneo reale
    Path tempFile = Files.createTempFile("test", ".jpg");
    Files.write(tempFile, "test content".getBytes());

    Resource resource = new UrlResource(tempFile.toUri());

    when(archiviazioneService.loadAsResource("media123", "test.jpg"))
        .thenReturn(resource);

    mockMvc.perform(get("/api/file/media123/test.jpg"))
        .andExpect(status().isOk())
        .andExpect(header().exists("Content-Disposition"));

    verify(archiviazioneService).loadAsResource("media123", "test.jpg");

    // Pulisci il file temporaneo
    Files.deleteIfExists(tempFile);
  }

  @Test
  void testServiFile_FileNonTrovato() throws Exception {
    when(archiviazioneService.loadAsResource("media123", "nonEsiste.jpg"))
        .thenReturn(null);

    mockMvc.perform(get("/api/file/media123/nonEsiste.jpg"))
        .andExpect(status().isNotFound());

    verify(archiviazioneService).loadAsResource("media123", "nonEsiste.jpg");
  }

  @Test
  void testServiFile_FileNonTrovatoException() throws Exception {
    when(archiviazioneService.loadAsResource("media123", "errore.jpg"))
        .thenThrow(new FileNonTrovatoException("File non trovato"));

    mockMvc.perform(get("/api/file/media123/errore.jpg"))
        .andExpect(status().isNotFound());

    verify(archiviazioneService).loadAsResource("media123", "errore.jpg");
  }

  @Test
  void testGestisciUploadFile_AttivitaProprietario() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
        "file", "test.jpg", "image/jpeg", "test content".getBytes());

    when(attivitaRepository.findOneByMedia("media123")).thenReturn(Optional.of(attivita));
    doNothing().when(archiviazioneService).store(eq("media123"), any(MockMultipartFile.class));

    mockMvc.perform(multipart("/api/file")
            .file(file)
            .param("media", "media123")
            .with(user(gestore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(attivitaRepository).findOneByMedia("media123");
    verify(archiviazioneService).store(eq("media123"), any(MockMultipartFile.class));
  }

  @Test
  void testGestisciUploadFile_RecensioneProprietario() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
        "file", "test.jpg", "image/jpeg", "test content".getBytes());

    when(attivitaRepository.findOneByMedia("media456")).thenReturn(Optional.empty());
    when(recensioneRepository.findOneByMedia("media456")).thenReturn(Optional.of(recensione));
    doNothing().when(archiviazioneService).store(eq("media456"), any(MockMultipartFile.class));

    mockMvc.perform(multipart("/api/file")
            .file(file)
            .param("media", "media456")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(attivitaRepository).findOneByMedia("media456");
    verify(recensioneRepository).findOneByMedia("media456");
    verify(archiviazioneService).store(eq("media456"), any(MockMultipartFile.class));
  }

  @Test
  void testGestisciUploadFile_SegnalazioneProprietario() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
        "file", "test.jpg", "image/jpeg", "test content".getBytes());

    when(attivitaRepository.findOneByMedia("media789")).thenReturn(Optional.empty());
    when(recensioneRepository.findOneByMedia("media789")).thenReturn(Optional.empty());
    when(segnalazioniRepository.findOneByMedia("media789")).thenReturn(Optional.of(segnalazione));
    doNothing().when(archiviazioneService).store(eq("media789"), any(MockMultipartFile.class));

    mockMvc.perform(multipart("/api/file")
            .file(file)
            .param("media", "media789")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(attivitaRepository).findOneByMedia("media789");
    verify(recensioneRepository).findOneByMedia("media789");
    verify(segnalazioniRepository).findOneByMedia("media789");
    verify(archiviazioneService).store(eq("media789"), any(MockMultipartFile.class));
  }

  @Test
  void testGestisciUploadFile_Amministratore() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
        "file", "test.jpg", "image/jpeg", "test content".getBytes());

    doNothing().when(archiviazioneService).store(eq("mediaQualsiasi"), any(MockMultipartFile.class));

    mockMvc.perform(multipart("/api/file")
            .file(file)
            .param("media", "mediaQualsiasi")
            .with(user(amministratore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(archiviazioneService).store(eq("mediaQualsiasi"), any(MockMultipartFile.class));
  }

  @Test
  void testGestisciUploadFile_NonAutorizzato() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
        "file", "test.jpg", "image/jpeg", "test content".getBytes());

    when(attivitaRepository.findOneByMedia("media123")).thenReturn(Optional.of(attivita));

    mockMvc.perform(multipart("/api/file")
            .file(file)
            .param("media", "media123")
            .with(user(altroUtente))
            .with(csrf()))
        .andExpect(status().isForbidden());

    verify(attivitaRepository).findOneByMedia("media123");
    verify(archiviazioneService, never()).store(any(), any());
  }

  @Test
  void testGestisciUploadFile_MediaNonEsiste() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
        "file", "test.jpg", "image/jpeg", "test content".getBytes());

    when(attivitaRepository.findOneByMedia("mediaNonEsiste")).thenReturn(Optional.empty());
    when(recensioneRepository.findOneByMedia("mediaNonEsiste")).thenReturn(Optional.empty());
    when(segnalazioniRepository.findOneByMedia("mediaNonEsiste")).thenReturn(Optional.empty());

    mockMvc.perform(multipart("/api/file")
            .file(file)
            .param("media", "mediaNonEsiste")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isForbidden());

    verify(attivitaRepository).findOneByMedia("mediaNonEsiste");
    verify(recensioneRepository).findOneByMedia("mediaNonEsiste");
    verify(segnalazioniRepository).findOneByMedia("mediaNonEsiste");
    verify(archiviazioneService, never()).store(any(), any());
  }

  @Test
  void testCancellaMedia_AttivitaProprietario() throws Exception {
    when(attivitaRepository.findOneByMedia("media123")).thenReturn(Optional.of(attivita));
    doNothing().when(archiviazioneService).delete("media123", "test.jpg");

    mockMvc.perform(delete("/api/file/media123/test.jpg")
            .with(user(gestore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(attivitaRepository).findOneByMedia("media123");
    verify(archiviazioneService).delete("media123", "test.jpg");
  }

  @Test
  void testCancellaMedia_RecensioneProprietario() throws Exception {
    when(attivitaRepository.findOneByMedia("media456")).thenReturn(Optional.empty());
    when(recensioneRepository.findOneByMedia("media456")).thenReturn(Optional.of(recensione));
    doNothing().when(archiviazioneService).delete("media456", "test.jpg");

    mockMvc.perform(delete("/api/file/media456/test.jpg")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(attivitaRepository).findOneByMedia("media456");
    verify(recensioneRepository).findOneByMedia("media456");
    verify(archiviazioneService).delete("media456", "test.jpg");
  }

  @Test
  void testCancellaMedia_SegnalazioneProprietario() throws Exception {
    when(attivitaRepository.findOneByMedia("media789")).thenReturn(Optional.empty());
    when(recensioneRepository.findOneByMedia("media789")).thenReturn(Optional.empty());
    when(segnalazioniRepository.findOneByMedia("media789")).thenReturn(Optional.of(segnalazione));
    doNothing().when(archiviazioneService).delete("media789", "test.jpg");

    mockMvc.perform(delete("/api/file/media789/test.jpg")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(attivitaRepository).findOneByMedia("media789");
    verify(recensioneRepository).findOneByMedia("media789");
    verify(segnalazioniRepository).findOneByMedia("media789");
    verify(archiviazioneService).delete("media789", "test.jpg");
  }

  @Test
  void testCancellaMedia_Amministratore() throws Exception {
    doNothing().when(archiviazioneService).delete("mediaQualsiasi", "test.jpg");

    mockMvc.perform(delete("/api/file/mediaQualsiasi/test.jpg")
            .with(user(amministratore))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(archiviazioneService).delete("mediaQualsiasi", "test.jpg");
  }

  @Test
  void testCancellaMedia_NonAutorizzato() throws Exception {
    when(attivitaRepository.findOneByMedia("media123")).thenReturn(Optional.of(attivita));

    mockMvc.perform(delete("/api/file/media123/test.jpg")
            .with(user(altroUtente))
            .with(csrf()))
        .andExpect(status().isForbidden());

    verify(attivitaRepository).findOneByMedia("media123");
    verify(archiviazioneService, never()).delete(any(), any());
  }

  @Test
  void testCancellaMedia_MediaNonEsiste() throws Exception {
    when(attivitaRepository.findOneByMedia("mediaNonEsiste")).thenReturn(Optional.empty());
    when(recensioneRepository.findOneByMedia("mediaNonEsiste")).thenReturn(Optional.empty());
    when(segnalazioniRepository.findOneByMedia("mediaNonEsiste")).thenReturn(Optional.empty());

    mockMvc.perform(delete("/api/file/mediaNonEsiste/test.jpg")
            .with(user(visitatore))
            .with(csrf()))
        .andExpect(status().isForbidden());

    verify(attivitaRepository).findOneByMedia("mediaNonEsiste");
    verify(recensioneRepository).findOneByMedia("mediaNonEsiste");
    verify(segnalazioniRepository).findOneByMedia("mediaNonEsiste");
    verify(archiviazioneService, never()).delete(any(), any());
  }
}


