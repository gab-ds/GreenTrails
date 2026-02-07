package it.greentrails.backend.gestioneattivita.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Recensione;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.entities.ValoriEcosostenibilita;
import it.greentrails.backend.enums.RuoloUtente;
import it.greentrails.backend.gestioneattivita.repository.RecensioneRepository;
import java.util.ArrayList;
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
class RecensioneServiceImplTest {

  @Mock
  private RecensioneRepository repository;

  @InjectMocks
  private RecensioneServiceImpl service;

  private Recensione recensione;
  private Attivita attivita;
  private Utente visitatore;
  private ValoriEcosostenibilita valoriEcosostenibilita;

  @BeforeEach
  void setUp() {
    // Setup Visitatore
    visitatore = new Utente();
    visitatore.setId(1L);
    visitatore.setRuolo(RuoloUtente.VISITATORE);
    visitatore.setEmail("visitatore@test.com");
    visitatore.setNome("Mario");
    visitatore.setCognome("Rossi");

    // Setup Attività
    attivita = new Attivita();
    attivita.setId(1L);
    attivita.setNome("Hotel Test");
    attivita.setAlloggio(true);

    // Setup Valori Ecosostenibilita
    valoriEcosostenibilita = new ValoriEcosostenibilita();
    valoriEcosostenibilita.setId(1L);

    // Setup Recensione
    recensione = new Recensione();
    recensione.setId(1L);
    recensione.setAttivita(attivita);
    recensione.setVisitatore(visitatore);
    recensione.setDescrizione("Ottima esperienza!");
    recensione.setValutazioneStelleEsperienza(5);
    recensione.setValoriEcosostenibilita(valoriEcosostenibilita);
    recensione.setMedia("https://example.com/photo.jpg");
  }

  // Test saveRecensione
  @Test
  void testSaveRecensione_Success() throws Exception {
    // Given
    when(repository.save(recensione)).thenReturn(recensione);

    // When
    Recensione result = service.saveRecensione(recensione);

    // Then
    assertNotNull(result);
    assertEquals(recensione, result);
    verify(repository, times(1)).save(recensione);
  }

  @Test
  void testSaveRecensione_RecensioneNull() {
    // When & Then
    Exception exception = assertThrows(Exception.class, () ->
        service.saveRecensione(null));
    assertEquals("La recensione è vuota.", exception.getMessage());
    verify(repository, never()).save(any());
  }

  @Test
  void testSaveRecensione_NuovaRecensione() throws Exception {
    // Given
    Recensione nuovaRecensione = new Recensione();
    nuovaRecensione.setAttivita(attivita);
    nuovaRecensione.setVisitatore(visitatore);
    nuovaRecensione.setDescrizione("Bellissimo!");
    nuovaRecensione.setValutazioneStelleEsperienza(5);
    nuovaRecensione.setValoriEcosostenibilita(valoriEcosostenibilita);

    Recensione recensioneSalvata = new Recensione();
    recensioneSalvata.setId(2L);
    recensioneSalvata.setAttivita(attivita);
    recensioneSalvata.setVisitatore(visitatore);
    recensioneSalvata.setDescrizione("Bellissimo!");
    recensioneSalvata.setValutazioneStelleEsperienza(5);
    recensioneSalvata.setValoriEcosostenibilita(valoriEcosostenibilita);

    when(repository.save(nuovaRecensione)).thenReturn(recensioneSalvata);

    // When
    Recensione result = service.saveRecensione(nuovaRecensione);

    // Then
    assertNotNull(result);
    assertEquals(2L, result.getId());
    assertEquals("Bellissimo!", result.getDescrizione());
    verify(repository, times(1)).save(nuovaRecensione);
  }

  // Test findById
  @Test
  void testFindById_Success() throws Exception {
    // Given
    when(repository.findById(1L)).thenReturn(Optional.of(recensione));

    // When
    Recensione result = service.findById(1L);

    // Then
    assertNotNull(result);
    assertEquals(recensione, result);
    assertEquals(1L, result.getId());
    verify(repository, times(1)).findById(1L);
  }

  @Test
  void testFindById_NotFound() {
    // Given
    when(repository.findById(1L)).thenReturn(Optional.empty());

    // When & Then
    Exception exception = assertThrows(Exception.class, () ->
        service.findById(1L));
    assertEquals("La recensione non è stata trovata.", exception.getMessage());
  }

  @Test
  void testFindById_IdNull() {
    // When & Then
    Exception exception = assertThrows(Exception.class, () ->
        service.findById(null));
    assertEquals("L'id non è valido.", exception.getMessage());
    verify(repository, never()).findById(any());
  }

  @Test
  void testFindById_IdNegativo() {
    // When & Then
    Exception exception = assertThrows(Exception.class, () ->
        service.findById(-1L));
    assertEquals("L'id non è valido.", exception.getMessage());
    verify(repository, never()).findById(any());
  }

  // Test deleteRecensione
  @Test
  void testDeleteRecensione_Success() throws Exception {
    // Given
    when(repository.findById(recensione.getId())).thenReturn(Optional.empty());

    // When
    boolean result = service.deleteRecensione(recensione);

    // Then
    assertTrue(result);
    verify(repository, times(1)).delete(recensione);
    verify(repository, times(1)).flush();
    verify(repository, times(1)).findById(recensione.getId());
  }

  @Test
  void testDeleteRecensione_NotDeleted() throws Exception {
    // Given
    when(repository.findById(recensione.getId())).thenReturn(Optional.of(recensione));

    // When
    boolean result = service.deleteRecensione(recensione);

    // Then
    assertFalse(result);
    verify(repository, times(1)).delete(recensione);
    verify(repository, times(1)).flush();
  }

  @Test
  void testDeleteRecensione_RecensioneNull() {
    // When & Then
    Exception exception = assertThrows(Exception.class, () ->
        service.deleteRecensione(null));
    assertEquals("La recensione è vuota.", exception.getMessage());
    verify(repository, never()).delete(any());
  }

  // Test getRecensioniByAttivita
  @Test
  void testGetRecensioniByAttivita_Success() throws Exception {
    // Given
    Recensione recensione2 = new Recensione();
    recensione2.setId(2L);
    recensione2.setAttivita(attivita);
    recensione2.setVisitatore(visitatore);
    recensione2.setDescrizione("Molto bello!");
    recensione2.setValutazioneStelleEsperienza(4);
    recensione2.setValoriEcosostenibilita(valoriEcosostenibilita);

    List<Recensione> recensioniList = List.of(recensione, recensione2);
    Page<Recensione> recensioniPage = new PageImpl<>(recensioniList);

    when(repository.findByAttivita(eq(1L), any(Pageable.class)))
        .thenReturn(recensioniPage);

    // When
    List<Recensione> result = service.getRecensioniByAttivita(attivita);

    // Then
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(recensione, result.get(0));
    assertEquals(recensione2, result.get(1));
    verify(repository, times(1)).findByAttivita(eq(1L), any(Pageable.class));
  }

  @Test
  void testGetRecensioniByAttivita_EmptyList() throws Exception {
    // Given
    Page<Recensione> emptyPage = new PageImpl<>(new ArrayList<>());
    when(repository.findByAttivita(eq(1L), any(Pageable.class)))
        .thenReturn(emptyPage);

    // When
    List<Recensione> result = service.getRecensioniByAttivita(attivita);

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void testGetRecensioniByAttivita_AttivitaNull() {
    // When & Then
    Exception exception = assertThrows(Exception.class, () ->
        service.getRecensioniByAttivita(null));
    assertEquals("L'attività è vuota.", exception.getMessage());
    verify(repository, never()).findByAttivita(any(), any());
  }

  // Test getAllRecensioniByVisitatore
  @Test
  void testGetAllRecensioniByVisitatore_Success() throws Exception {
    // Given
    Recensione recensione2 = new Recensione();
    recensione2.setId(2L);
    recensione2.setAttivita(attivita);
    recensione2.setVisitatore(visitatore);
    recensione2.setDescrizione("Esperienza fantastica!");
    recensione2.setValutazioneStelleEsperienza(5);
    recensione2.setValoriEcosostenibilita(valoriEcosostenibilita);

    // Recensione di un altro visitatore
    Utente altroVisitatore = new Utente();
    altroVisitatore.setId(2L);
    altroVisitatore.setRuolo(RuoloUtente.VISITATORE);

    Recensione recensione3 = new Recensione();
    recensione3.setId(3L);
    recensione3.setAttivita(attivita);
    recensione3.setVisitatore(altroVisitatore);
    recensione3.setDescrizione("Non male");
    recensione3.setValutazioneStelleEsperienza(3);
    recensione3.setValoriEcosostenibilita(valoriEcosostenibilita);

    when(repository.findAll()).thenReturn(List.of(recensione, recensione2, recensione3));

    // When
    List<Recensione> result = service.getAllRecensioniByVisitatore(visitatore);

    // Then
    assertNotNull(result);
    assertEquals(2, result.size());
    assertTrue(result.contains(recensione));
    assertTrue(result.contains(recensione2));
    assertFalse(result.contains(recensione3));
    verify(repository, times(1)).findAll();
  }

  @Test
  void testGetAllRecensioniByVisitatore_NessunRisultato() throws Exception {
    // Given
    Utente altroVisitatore = new Utente();
    altroVisitatore.setId(2L);
    altroVisitatore.setRuolo(RuoloUtente.VISITATORE);

    when(repository.findAll()).thenReturn(List.of(recensione));

    // When
    List<Recensione> result = service.getAllRecensioniByVisitatore(altroVisitatore);

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void testGetAllRecensioniByVisitatore_UtenteNull() {
    // When & Then
    Exception exception = assertThrows(Exception.class, () ->
        service.getAllRecensioniByVisitatore(null));
    assertEquals("L'utente è vuoto.", exception.getMessage());
    verify(repository, never()).findAll();
  }

  @Test
  void testGetAllRecensioniByVisitatore_ListaVuota() throws Exception {
    // Given
    when(repository.findAll()).thenReturn(new ArrayList<>());

    // When
    List<Recensione> result = service.getAllRecensioniByVisitatore(visitatore);

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  // Test aggiuntivi per copertura completa
  @Test
  void testSaveRecensione_AggiornamentoRecensione() throws Exception {
    // Given
    recensione.setDescrizione("Testo aggiornato");
    recensione.setValutazioneStelleEsperienza(4);
    when(repository.save(recensione)).thenReturn(recensione);

    // When
    Recensione result = service.saveRecensione(recensione);

    // Then
    assertNotNull(result);
    assertEquals("Testo aggiornato", result.getDescrizione());
    assertEquals(4, result.getValutazioneStelleEsperienza());
  }

  @Test
  void testGetRecensioniByAttivita_MultiplePages() throws Exception {
    // Given
    List<Recensione> recensioni = new ArrayList<>();
    for (int i = 1; i <= 5; i++) {
      Recensione r = new Recensione();
      r.setId((long) i);
      r.setAttivita(attivita);
      r.setVisitatore(visitatore);
      r.setDescrizione("Recensione " + i);
      r.setValutazioneStelleEsperienza(i % 5 + 1);
      r.setValoriEcosostenibilita(valoriEcosostenibilita);
      recensioni.add(r);
    }

    Page<Recensione> page = new PageImpl<>(recensioni);
    when(repository.findByAttivita(eq(1L), any(Pageable.class))).thenReturn(page);

    // When
    List<Recensione> result = service.getRecensioniByAttivita(attivita);

    // Then
    assertNotNull(result);
    assertEquals(5, result.size());
  }
}