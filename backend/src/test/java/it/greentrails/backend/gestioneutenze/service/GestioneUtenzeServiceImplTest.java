package it.greentrails.backend.gestioneutenze.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.greentrails.backend.entities.Preferenze;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.enums.RuoloUtente;
import it.greentrails.backend.gestioneutenze.repository.PreferenzeRepository;
import it.greentrails.backend.gestioneutenze.repository.UtenteRepository;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class GestioneUtenzeServiceImplTest {

  @Mock
  private PreferenzeRepository preferenzeRepository;

  @Mock
  private UtenteRepository repository;

  @InjectMocks
  private GestioneUtenzeServiceImpl service;

  @BeforeEach
  void setUp() {
  }

  @Test
  void getPreferenzeByIdNullId() {
    assertThrows(Exception.class, () -> service.getPreferenzeById(null));
  }

  @Test
  void getPreferenzeByIdNonValidoId() {
    assertThrows(Exception.class, () -> service.getPreferenzeById(-1L));
  }

  @Test
  void getPreferenzeByIdUtenteGestore() {
    when(preferenzeRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(Exception.class, () -> service.getPreferenzeById(1L));
  }

  @Test
  void getPreferenzeByIdSuccess() throws Exception {
    Preferenze p = new Preferenze();
    when(preferenzeRepository.findById(2L)).thenReturn(Optional.of(p));
    assertEquals(p, service.getPreferenzeById(2L));
  }

  // Test per findById
  @Test
  void findByIdNullId() {
    assertThrows(Exception.class, () -> service.findById(null));
  }

  @Test
  void findByIdNonValidoId() {
    assertThrows(Exception.class, () -> service.findById(-1L));
  }

  @Test
  void findByIdUtenteNonTrovato() {
    when(repository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(Exception.class, () -> service.findById(1L));
  }

  @Test
  void findByIdSuccess() throws Exception {
    Utente utente = new Utente();
    utente.setId(1L);
    utente.setNome("Mario");
    utente.setCognome("Rossi");
    utente.setEmail("mario.rossi@example.com");
    when(repository.findById(1L)).thenReturn(Optional.of(utente));
    assertEquals(utente, service.findById(1L));
  }

  // Test per saveUtente
  @Test
  void saveUtenteNullUtente() {
    assertThrows(Exception.class, () -> service.saveUtente(null));
  }

  @Test
  void saveUtenteSuccess() throws Exception {
    Utente utente = new Utente();
    utente.setNome("Mario");
    utente.setCognome("Rossi");
    utente.setEmail("mario.rossi@example.com");
    utente.setPassword("password123");
    utente.setDataNascita(new Date());
    utente.setRuolo(RuoloUtente.VISITATORE);

    when(repository.save(any(Utente.class))).thenReturn(utente);
    Utente saved = service.saveUtente(utente);
    assertEquals(utente, saved);
    verify(repository).save(utente);
  }

  // Test per savePreferenze
  @Test
  void savePreferenzeNullPreferenze() {
    assertThrows(Exception.class, () -> service.savePreferenze(null));
  }

  @Test
  void savePreferenzeSuccess() throws Exception {
    Preferenze preferenze = new Preferenze();
    when(preferenzeRepository.save(any(Preferenze.class))).thenReturn(preferenze);
    Preferenze saved = service.savePreferenze(preferenze);
    assertEquals(preferenze, saved);
    verify(preferenzeRepository).save(preferenze);
  }

  // Test per findByEmail
  @Test
  void findByEmailNonTrovato() {
    when(repository.findOneByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
    Optional<Utente> result = service.findByEmail("nonexistent@example.com");
    assertFalse(result.isPresent());
  }

  @Test
  void findByEmailSuccess() {
    Utente utente = new Utente();
    utente.setEmail("mario.rossi@example.com");
    when(repository.findOneByEmail("mario.rossi@example.com")).thenReturn(Optional.of(utente));
    Optional<Utente> result = service.findByEmail("mario.rossi@example.com");
    assertTrue(result.isPresent());
    assertEquals(utente, result.get());
  }

  // Test per deleteUtente
  @Test
  void deleteUtenteNullUtente() {
    assertThrows(Exception.class, () -> service.deleteUtente(null));
  }

  @Test
  void deleteUtenteSuccess() throws Exception {
    Utente utente = new Utente();
    utente.setId(1L);
    utente.setNome("Mario");
    utente.setCognome("Rossi");
    utente.setEmail("mario.rossi@example.com");

    when(repository.findById(1L)).thenReturn(Optional.empty());
    boolean deleted = service.deleteUtente(utente);
    assertTrue(deleted);
    verify(repository).delete(utente);
    verify(repository).flush();
  }

  @Test
  void deleteUtenteFallimento() throws Exception {
    Utente utente = new Utente();
    utente.setId(1L);

    when(repository.findById(1L)).thenReturn(Optional.of(utente));
    boolean deleted = service.deleteUtente(utente);
    assertFalse(deleted);
    verify(repository).delete(utente);
    verify(repository).flush();
  }

  // Test per loadUserByUsername
  @Test
  void loadUserByUsernameNonTrovato() {
    when(repository.findOneByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
    assertThrows(UsernameNotFoundException.class,
        () -> service.loadUserByUsername("nonexistent@example.com"));
  }

  @Test
  void loadUserByUsernameSuccess() {
    Utente utente = new Utente();
    utente.setEmail("mario.rossi@example.com");
    utente.setPassword("password123");
    utente.setRuolo(RuoloUtente.VISITATORE);

    when(repository.findOneByEmail("mario.rossi@example.com")).thenReturn(Optional.of(utente));
    UserDetails userDetails = service.loadUserByUsername("mario.rossi@example.com");
    assertEquals(utente, userDetails);
    assertEquals("mario.rossi@example.com", userDetails.getUsername());
  }
}