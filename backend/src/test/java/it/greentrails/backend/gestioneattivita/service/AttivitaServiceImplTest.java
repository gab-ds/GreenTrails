package it.greentrails.backend.gestioneattivita.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.ValoriEcosostenibilita;
import it.greentrails.backend.gestioneattivita.repository.AttivitaRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class AttivitaServiceImplTest {

  @Mock
  private AttivitaRepository repository;

  @InjectMocks
  private AttivitaServiceImpl service;

  private Attivita attivita;

  @BeforeEach
  void setUp() {
    attivita = new Attivita();
    attivita.setId(1L);
    attivita.setNome("Attività Test");
    attivita.setPrezzo(50.0);
  }

  // Test per saveAttivita
  @Test
  void testSaveAttivitaWithNull() {
    assertThrows(Exception.class, () -> service.saveAttivita(null));
  }

  @Test
  void testSaveAttivitaWithValidAttivita() throws Exception {
    when(repository.save(any(Attivita.class))).thenReturn(attivita);

    Attivita result = service.saveAttivita(attivita);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    verify(repository).save(attivita);
  }

  // Test per findById
  @Test
  void testFindByIdWithNull() {
    assertThrows(Exception.class, () -> service.findById(null));
  }

  @Test
  void testFindByIdWithNegativeId() {
    assertThrows(Exception.class, () -> service.findById(-1L));
  }

  @Test
  void testFindByIdNotFound() {
    when(repository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(Exception.class, () -> service.findById(1L));
  }

  @Test
  void testFindByIdWithValidId() throws Exception {
    when(repository.findById(1L)).thenReturn(Optional.of(attivita));

    Attivita result = service.findById(1L);

    assertNotNull(result);
    assertEquals(1L, result.getId());
  }

  // Test per findAllAttivitaByGestore
  @Test
  void testFindAllAttivitaByGestoreWithNull() {
    assertThrows(Exception.class, () -> service.findAllAttivitaByGestore(null));
  }

  @Test
  void testFindAllAttivitaByGestoreWithNegativeId() {
    assertThrows(Exception.class, () -> service.findAllAttivitaByGestore(-1L));
  }

  @Test
  void testFindAllAttivitaByGestoreWithValidId() throws Exception {
    List<Attivita> attivitaList = new ArrayList<>();
    attivitaList.add(attivita);
    Page<Attivita> page = new PageImpl<>(attivitaList);

    when(repository.findByGestore(eq(1L), any(Pageable.class))).thenReturn(page);

    List<Attivita> result = service.findAllAttivitaByGestore(1L);

    assertNotNull(result);
    assertEquals(1, result.size());
  }

  // Test per findByValori
  @Test
  void testFindByValoriWithNull() {
    assertThrows(Exception.class, () -> service.findByValori(null));
  }

  @Test
  void testFindByValoriWithValidValori() throws Exception {
    ValoriEcosostenibilita valori = new ValoriEcosostenibilita();
    valori.setId(1L);

    when(repository.findByValori(1L)).thenReturn(Optional.of(attivita));

    Optional<Attivita> result = service.findByValori(valori);

    assertTrue(result.isPresent());
    assertEquals(1L, result.get().getId());
  }

  @Test
  void testFindByValoriNotFound() throws Exception {
    ValoriEcosostenibilita valori = new ValoriEcosostenibilita();
    valori.setId(1L);

    when(repository.findByValori(1L)).thenReturn(Optional.empty());

    Optional<Attivita> result = service.findByValori(valori);

    assertFalse(result.isPresent());
  }

  // Test per getAttivitaTuristicheEconomiche
  @Test
  void testGetAttivitaTuristicheEconomiche() {
    List<Attivita> attivitaList = new ArrayList<>();
    attivitaList.add(attivita);
    Page<Attivita> page = new PageImpl<>(attivitaList);

    when(repository.getAllByPrezzo(any(Pageable.class))).thenReturn(page);

    List<Attivita> result = service.getAttivitaTuristicheEconomiche(10);

    assertNotNull(result);
    assertEquals(1, result.size());
  }

  // Test per deleteAttivita
  @Test
  void testDeleteAttivitaWithNull() {
    assertThrows(Exception.class, () -> service.deleteAttivita(null));
  }

  @Test
  void testDeleteAttivitaSuccessful() throws Exception {
    when(repository.save(any(Attivita.class))).thenReturn(attivita);

    boolean result = service.deleteAttivita(attivita);

    assertTrue(result);
    assertTrue(attivita.isEliminata());
  }

  @Test
  void testDeleteAttivitaFailed() throws Exception {
    when(repository.save(any(Attivita.class))).thenThrow(new RuntimeException("Save failed"));

    boolean result = service.deleteAttivita(attivita);

    assertFalse(result);
  }

  // Test per getAttivitaTuristiche
  @Test
  void testGetAttivitaTuristiche() {
    List<Attivita> attivitaList = new ArrayList<>();
    attivitaList.add(attivita);
    Page<Attivita> page = new PageImpl<>(attivitaList);

    when(repository.getAttivitaTuristiche(any(Pageable.class))).thenReturn(page);

    List<Attivita> result = service.getAttivitaTuristiche(10);

    assertNotNull(result);
    assertEquals(1, result.size());
  }

  // Test per getAlloggi
  @Test
  void testGetAlloggi() {
    List<Attivita> attivitaList = new ArrayList<>();
    attivitaList.add(attivita);
    Page<Attivita> page = new PageImpl<>(attivitaList);

    when(repository.getAlloggi(any(Pageable.class))).thenReturn(page);

    List<Attivita> result = service.getAlloggi(10);

    assertNotNull(result);
    assertEquals(1, result.size());
  }

  // Test per findAll
  @Test
  void testFindAll() {
    List<Attivita> attivitaList = new ArrayList<>();
    attivitaList.add(attivita);

    when(repository.findAll()).thenReturn(attivitaList);

    List<Attivita> result = service.findAll();

    assertNotNull(result);
    assertEquals(1, result.size());
  }
}