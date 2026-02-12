package it.greentrails.backend.gestioneprenotazioni.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Camera;
import it.greentrails.backend.entities.Itinerario;
import it.greentrails.backend.entities.PrenotazioneAlloggio;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.enums.RuoloUtente;
import it.greentrails.backend.enums.StatoPrenotazione;
import it.greentrails.backend.gestioneattivita.service.CameraService;
import it.greentrails.backend.gestioneprenotazioni.repository.PrenotazioneAlloggioRepository;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class PrenotazioneAlloggioServiceImplTest {

  @Mock
  private PrenotazioneAlloggioRepository repository;

  @Mock
  private CameraService cameraService;

  @InjectMocks
  private PrenotazioneAlloggioServiceImpl service;

  private final Date dataInizio = new GregorianCalendar(2024, Calendar.MAY, 15)
      .getTime();
  private final Date dataFine = new GregorianCalendar(2024, Calendar.MAY, 18)
      .getTime();

  private PrenotazioneAlloggio prenotazione;
  private Camera camera;
  private Attivita alloggio;
  private Utente visitatore;
  private Itinerario itinerario;

  @BeforeEach
  void setUp() {
    // Setup Alloggio
    alloggio = new Attivita();
    alloggio.setId(1L);
    alloggio.setAlloggio(true);
    alloggio.setNome("Hotel Test");

    // Setup Camera
    camera = new Camera();
    camera.setId(1L);
    camera.setAlloggio(alloggio);
    camera.setDisponibilita(10);

    // Setup Visitatore
    visitatore = new Utente();
    visitatore.setId(1L);
    visitatore.setRuolo(RuoloUtente.VISITATORE);
    visitatore.setEmail("visitatore@test.com");

    // Setup Itinerario
    itinerario = new Itinerario();
    itinerario.setId(1L);
    itinerario.setVisitatore(visitatore);

    // Setup Prenotazione
    prenotazione = new PrenotazioneAlloggio();
    prenotazione.setId(1L);
    prenotazione.setCamera(camera);
    prenotazione.setItinerario(itinerario);
    prenotazione.setDataInizio(dataInizio);
    prenotazione.setDataFine(dataFine);
    prenotazione.setStato(StatoPrenotazione.CREATA);
    prenotazione.setNumCamere(2);
  }

  // Test savePrenotazioneAlloggio
  @Test
  void testSavePrenotazioneAlloggio_Success() throws Exception {
    // Given
    when(repository.save(prenotazione)).thenReturn(prenotazione);

    // When
    PrenotazioneAlloggio result = service.savePrenotazioneAlloggio(camera, prenotazione);

    // Then
    assertNotNull(result);
    assertEquals(camera, result.getCamera());
    verify(repository, times(1)).save(prenotazione);
  }

  @Test
  void testSavePrenotazioneAlloggio_PrenotazioneNull() {
    // When & Then
    Exception exception = assertThrows(Exception.class, () ->
        service.savePrenotazioneAlloggio(camera, null));
    assertEquals("La prenotazione dell'attività è vuota.", exception.getMessage());
  }

  @Test
  void testSavePrenotazioneAlloggio_CameraNull() {
    // When & Then
    Exception exception = assertThrows(Exception.class, () ->
        service.savePrenotazioneAlloggio(null, prenotazione));
    assertEquals("La camera è vuota.", exception.getMessage());
  }

  @Test
  void testSavePrenotazioneAlloggio_CameraSenzaAlloggio() {
    // Given
    Camera cameraSenzaAlloggio = new Camera();
    cameraSenzaAlloggio.setId(2L);
    cameraSenzaAlloggio.setAlloggio(null);

    // When & Then
    Exception exception = assertThrows(Exception.class, () ->
        service.savePrenotazioneAlloggio(cameraSenzaAlloggio, prenotazione));
    assertEquals("La camera non ha un alloggio collegato.", exception.getMessage());
  }

  // Test deletePrenotazioneAlloggio
  @Test
  void testDeletePrenotazioneAlloggio_Success() throws Exception {
    // Given
    when(repository.findById(prenotazione.getId())).thenReturn(Optional.empty());

    // When
    boolean result = service.deletePrenotazioneAlloggio(prenotazione);

    // Then
    assertTrue(result);
    verify(repository, times(1)).delete(prenotazione);
    verify(repository, times(1)).flush();
  }

  @Test
  void testDeletePrenotazioneAlloggio_NotDeleted() throws Exception {
    // Given
    when(repository.findById(prenotazione.getId())).thenReturn(Optional.of(prenotazione));

    // When
    boolean result = service.deletePrenotazioneAlloggio(prenotazione);

    // Then
    assertFalse(result);
    verify(repository, times(1)).delete(prenotazione);
  }

  @Test
  void testDeletePrenotazioneAlloggio_PrenotazioneNull() {
    // When & Then
    Exception exception = assertThrows(Exception.class, () ->
        service.deletePrenotazioneAlloggio(null));
    assertEquals("La prenotazione dell'alloggio è vuota.", exception.getMessage());
  }

  // Test getAllPrenotazioniAlloggio
  @Test
  void testGetAllPrenotazioniAlloggio_Success() {
    // Given
    List<PrenotazioneAlloggio> prenotazioni = List.of(prenotazione);
    when(repository.findAll()).thenReturn(prenotazioni);

    // When
    List<PrenotazioneAlloggio> result = service.getAllPrenotazioniAlloggio();

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(prenotazione, result.get(0));
  }

  @Test
  void testGetAllPrenotazioniAlloggio_EmptyList() {
    // Given
    when(repository.findAll()).thenReturn(new ArrayList<>());

    // When
    List<PrenotazioneAlloggio> result = service.getAllPrenotazioniAlloggio();

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  // Test getPrenotazioniAlloggioByStato
  @Test
  void testGetPrenotazioniAlloggioByStato_Success() throws Exception {
    // Given
    when(repository.findAll()).thenReturn(List.of(prenotazione));

    // When
    List<PrenotazioneAlloggio> result = service.getPrenotazioniAlloggioByStato(
        StatoPrenotazione.CREATA);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(StatoPrenotazione.CREATA, result.get(0).getStato());
  }

  @Test
  void testGetPrenotazioniAlloggioByStato_NessunRisultato() throws Exception {
    // Given
    when(repository.findAll()).thenReturn(List.of(prenotazione));

    // When
    List<PrenotazioneAlloggio> result = service.getPrenotazioniAlloggioByStato(
        StatoPrenotazione.COMPLETATA);

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void testGetPrenotazioniAlloggioByStato_StatoNull() {
    // When & Then
    Exception exception = assertThrows(Exception.class, () ->
        service.getPrenotazioniAlloggioByStato(null));
    assertEquals("Lo stato della prenotazione dell'alloggio è inesistente.", exception.getMessage());
  }

  // Test findById
  @Test
  void testFindById_Success() throws Exception {
    // Given
    when(repository.findById(1L)).thenReturn(Optional.of(prenotazione));

    // When
    PrenotazioneAlloggio result = service.findById(1L);

    // Then
    assertNotNull(result);
    assertEquals(prenotazione, result);
  }

  @Test
  void testFindById_NotFound() {
    // Given
    when(repository.findById(1L)).thenReturn(Optional.empty());

    // When & Then
    Exception exception = assertThrows(Exception.class, () -> service.findById(1L));
    assertEquals("La prenotazione non è stata trovata.", exception.getMessage());
  }

  @Test
  void testFindById_IdNull() {
    // When & Then
    Exception exception = assertThrows(Exception.class, () -> service.findById(null));
    assertEquals("L'id non è valido.", exception.getMessage());
  }

  @Test
  void testFindById_IdNegativo() {
    // When & Then
    Exception exception = assertThrows(Exception.class, () -> service.findById(-1L));
    assertEquals("L'id non è valido.", exception.getMessage());
  }

  // Test getPrenotazioniByAlloggio
  @Test
  void testGetPrenotazioniByAlloggio_Success() throws Exception {
    // Given
    Page<PrenotazioneAlloggio> page = new PageImpl<>(List.of(prenotazione));
    when(repository.findByAlloggio(eq(1L), any(Pageable.class))).thenReturn(page);

    // When
    List<PrenotazioneAlloggio> result = service.getPrenotazioniByAlloggio(alloggio);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(prenotazione, result.get(0));
  }

  @Test
  void testGetPrenotazioniByAlloggio_AttivitaNull() {
    // When & Then
    Exception exception = assertThrows(Exception.class, () ->
        service.getPrenotazioniByAlloggio(null));
    assertEquals("L'attività è vuota.", exception.getMessage());
  }

  @Test
  void testGetPrenotazioniByAlloggio_AttivitaTuristica() {
    // Given
    Attivita attivitaTuristica = new Attivita();
    attivitaTuristica.setId(2L);
    attivitaTuristica.setAlloggio(false);

    // When & Then
    Exception exception = assertThrows(Exception.class, () ->
        service.getPrenotazioniByAlloggio(attivitaTuristica));
    assertEquals("L'attività non può essere un'attività turistica.", exception.getMessage());
  }

  // Test getPrenotazioniByVisitatore
  @Test
  void testGetPrenotazioniByVisitatore_Success() throws Exception {
    // Given
    Page<PrenotazioneAlloggio> page = new PageImpl<>(List.of(prenotazione));
    when(repository.findByVisitatore(eq(1L), any(Pageable.class))).thenReturn(page);

    // When
    List<PrenotazioneAlloggio> result = service.getPrenotazioniByVisitatore(visitatore);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(prenotazione, result.get(0));
  }

  @Test
  void testGetPrenotazioniByVisitatore_UtenteNull() {
    // When & Then
    Exception exception = assertThrows(Exception.class, () ->
        service.getPrenotazioniByVisitatore(null));
    assertEquals("L'utente è vuoto.", exception.getMessage());
  }

  @Test
  void testGetPrenotazioniByVisitatore_UtenteNonVisitatore() {
    // Given
    Utente gestore = new Utente();
    gestore.setId(2L);
    gestore.setRuolo(RuoloUtente.GESTORE_ATTIVITA);

    // When & Then
    Exception exception = assertThrows(Exception.class, () ->
        service.getPrenotazioniByVisitatore(gestore));
    assertEquals("L'utente non è un visitatore.", exception.getMessage());
  }

  // Test getPrenotazioniByItinerario
  @Test
  void testGetPrenotazioniByItinerario_Success() throws Exception {
    // Given
    Page<PrenotazioneAlloggio> page = new PageImpl<>(List.of(prenotazione));
    when(repository.findByItinerario(eq(1L), any(Pageable.class))).thenReturn(page);

    // When
    List<PrenotazioneAlloggio> result = service.getPrenotazioniByItinerario(itinerario);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(prenotazione, result.get(0));
  }

  @Test
  void testGetPrenotazioniByItinerario_ItinerarioNull() {
    // When & Then
    Exception exception = assertThrows(Exception.class, () ->
        service.getPrenotazioniByItinerario(null));
    assertEquals("L'itinerario è vuoto.", exception.getMessage());
  }

  // Test controllaDisponibilitaAlloggio
  @Test
  void testControllaDisponibilitaAlloggio_Success() throws Exception {
    // Given
    Camera camera1 = new Camera();
    camera1.setDisponibilita(10);
    Camera camera2 = new Camera();
    camera2.setDisponibilita(5);

    when(cameraService.getCamereByAlloggio(alloggio)).thenReturn(List.of(camera1, camera2));
    when(repository.getPostiOccupatiAlloggioTra(alloggio.getId(), dataInizio, dataFine))
        .thenReturn(3);

    // When
    int result = service.controllaDisponibilitaAlloggio(alloggio, dataInizio, dataFine);

    // Then
    assertEquals(12, result); // 10 + 5 - 3 = 12
  }

  @Test
  void testControllaDisponibilitaAlloggio_AlloggioNull() {
    // When & Then
    Exception exception = assertThrows(Exception.class, () ->
        service.controllaDisponibilitaAlloggio(null, dataInizio, dataFine));
    assertEquals("L'alloggio è vuoto.", exception.getMessage());
  }

  @Test
  void testControllaDisponibilitaAlloggio_AttivitaTuristica() {
    // Given
    Attivita attivitaTuristica = new Attivita();
    attivitaTuristica.setId(2L);
    attivitaTuristica.setAlloggio(false);

    // When & Then
    Exception exception = assertThrows(Exception.class, () ->
        service.controllaDisponibilitaAlloggio(attivitaTuristica, dataInizio, dataFine));
    assertEquals("L'attività non è un alloggio.", exception.getMessage());
  }

  @Test
  void testControllaDisponibilitaAlloggio_DataFinePrecedenteDataInizio() {
    // When & Then
    Exception exception = assertThrows(Exception.class, () ->
        service.controllaDisponibilitaAlloggio(alloggio, dataFine, dataInizio));
    assertEquals("La data di fine non può essere precedente alla data di inizio.",
        exception.getMessage());
  }

  // Test controllaDisponibilitaCamera
  @Test
  void testControllaDisponibilitaCamera_Success() throws Exception {
    // Given
    when(repository.getPostiOccupatiCameraTra(camera.getId(), dataInizio, dataFine))
        .thenReturn(4);

    // When
    int result = service.controllaDisponibilitaCamera(camera, dataInizio, dataFine);

    // Then
    assertEquals(6, result); // 10 - 4 = 6
  }

  @Test
  void testControllaDisponibilitaCamera_CameraNull() {
    // When & Then
    Exception exception = assertThrows(Exception.class, () ->
        service.controllaDisponibilitaCamera(null, dataInizio, dataFine));
    assertEquals("La camera è vuota.", exception.getMessage());
  }

  @Test
  void testControllaDisponibilitaCamera_DataFinePrecedenteDataInizio() {
    // When & Then
    Exception exception = assertThrows(Exception.class, () ->
        service.controllaDisponibilitaCamera(camera, dataFine, dataInizio));
    assertEquals("La data di fine non può essere precedente alla data di inizio.",
        exception.getMessage());
  }
}