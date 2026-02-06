package it.greentrails.backend.gestionesegnalazioni.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.greentrails.backend.entities.Segnalazione;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.enums.StatoSegnalazione;
import it.greentrails.backend.gestionesegnalazioni.repository.SegnalazioniRepository;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SegnalazioniServiceImplTest {

  @Mock
  private SegnalazioniRepository repository;

  @InjectMocks
  private SegnalazioniServiceImpl segnalazioniService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void saveSegnalazioneSuccess() throws Exception {
    Segnalazione segnalazione = new Segnalazione();
    segnalazione.setId(1L);
    segnalazione.setDescrizione("Segnalazione di test");
    segnalazione.setDataSegnalazione(new Date());
    segnalazione.setStato(StatoSegnalazione.CREATA);
    segnalazione.setForRecensione(false);

    Utente utente = new Utente();
    utente.setId(1L);
    segnalazione.setUtente(utente);

    when(repository.save(any(Segnalazione.class))).thenReturn(segnalazione);

    Segnalazione savedSegnalazione = segnalazioniService.saveSegnalazione(segnalazione);

    assertNotNull(savedSegnalazione);
    assertEquals(1L, savedSegnalazione.getId());
    assertEquals("Segnalazione di test", savedSegnalazione.getDescrizione());
    assertEquals(StatoSegnalazione.CREATA, savedSegnalazione.getStato());
    verify(repository).save(segnalazione);
  }

  @Test
  void saveSegnalazioneNullExceptionThrown() {
    Segnalazione nullSegnalazione = null;

    Exception exception = assertThrows(Exception.class, () -> {
      segnalazioniService.saveSegnalazione(nullSegnalazione);
    });

    assertEquals("La segnalazione è vuota.", exception.getMessage());
  }

  @Test
  void getAllSegnalazioniByTipoForRecensioneTrue() {
    Segnalazione segnalazione1 = new Segnalazione();
    segnalazione1.setId(1L);
    segnalazione1.setDescrizione("Segnalazione recensione 1");
    segnalazione1.setForRecensione(true);

    Segnalazione segnalazione2 = new Segnalazione();
    segnalazione2.setId(2L);
    segnalazione2.setDescrizione("Segnalazione recensione 2");
    segnalazione2.setForRecensione(true);

    List<Segnalazione> expectedList = Arrays.asList(segnalazione1, segnalazione2);

    when(repository.findByTipo(true)).thenReturn(expectedList);

    List<Segnalazione> result = segnalazioniService.getAllSegnalazioniByTipo(true);

    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("Segnalazione recensione 1", result.get(0).getDescrizione());
    assertEquals("Segnalazione recensione 2", result.get(1).getDescrizione());
    verify(repository).findByTipo(true);
  }

  @Test
  void getAllSegnalazioniByTipoForRecensioneFalse() {
    Segnalazione segnalazione1 = new Segnalazione();
    segnalazione1.setId(3L);
    segnalazione1.setDescrizione("Segnalazione attività 1");
    segnalazione1.setForRecensione(false);

    Segnalazione segnalazione2 = new Segnalazione();
    segnalazione2.setId(4L);
    segnalazione2.setDescrizione("Segnalazione attività 2");
    segnalazione2.setForRecensione(false);

    List<Segnalazione> expectedList = Arrays.asList(segnalazione1, segnalazione2);

    when(repository.findByTipo(false)).thenReturn(expectedList);

    List<Segnalazione> result = segnalazioniService.getAllSegnalazioniByTipo(false);

    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("Segnalazione attività 1", result.get(0).getDescrizione());
    assertEquals("Segnalazione attività 2", result.get(1).getDescrizione());
    verify(repository).findByTipo(false);
  }

  @Test
  void findByIdSuccess() throws Exception {
    Segnalazione segnalazione = new Segnalazione();
    segnalazione.setId(1L);
    segnalazione.setDescrizione("Segnalazione trovata");
    segnalazione.setStato(StatoSegnalazione.IN_ELABORAZIONE);

    when(repository.findById(1L)).thenReturn(Optional.of(segnalazione));

    Segnalazione foundSegnalazione = segnalazioniService.findById(1L);

    assertNotNull(foundSegnalazione);
    assertEquals(1L, foundSegnalazione.getId());
    assertEquals("Segnalazione trovata", foundSegnalazione.getDescrizione());
    assertEquals(StatoSegnalazione.IN_ELABORAZIONE, foundSegnalazione.getStato());
    verify(repository).findById(1L);
  }

  @Test
  void findByIdNullIdExceptionThrown() {
    Long nullId = null;

    Exception exception = assertThrows(Exception.class, () -> {
      segnalazioniService.findById(nullId);
    });

    assertEquals("L'id non è valido.", exception.getMessage());
  }

  @Test
  void findByIdNegativeIdExceptionThrown() {
    Long negativeId = -1L;

    Exception exception = assertThrows(Exception.class, () -> {
      segnalazioniService.findById(negativeId);
    });

    assertEquals("L'id non è valido.", exception.getMessage());
  }

  @Test
  void findByIdNotFoundExceptionThrown() {
    Long id = 999L;

    when(repository.findById(id)).thenReturn(Optional.empty());

    Exception exception = assertThrows(Exception.class, () -> {
      segnalazioniService.findById(id);
    });

    assertEquals("La segnalazione non è stata trovata.", exception.getMessage());
  }

  @Test
  void getSegnalazioniByStatoCreata() {
    Segnalazione segnalazione1 = new Segnalazione();
    segnalazione1.setId(1L);
    segnalazione1.setDescrizione("Segnalazione creata 1");
    segnalazione1.setStato(StatoSegnalazione.CREATA);

    Segnalazione segnalazione2 = new Segnalazione();
    segnalazione2.setId(2L);
    segnalazione2.setDescrizione("Segnalazione creata 2");
    segnalazione2.setStato(StatoSegnalazione.CREATA);

    List<Segnalazione> expectedList = Arrays.asList(segnalazione1, segnalazione2);

    when(repository.findByStato(StatoSegnalazione.CREATA)).thenReturn(expectedList);

    List<Segnalazione> result = segnalazioniService.getSegnalazioniByStato(StatoSegnalazione.CREATA);

    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(StatoSegnalazione.CREATA, result.get(0).getStato());
    assertEquals(StatoSegnalazione.CREATA, result.get(1).getStato());
    verify(repository).findByStato(StatoSegnalazione.CREATA);
  }

  @Test
  void getSegnalazioniByStatoInElaborazione() {
    Segnalazione segnalazione1 = new Segnalazione();
    segnalazione1.setId(3L);
    segnalazione1.setDescrizione("Segnalazione in lavorazione 1");
    segnalazione1.setStato(StatoSegnalazione.IN_ELABORAZIONE);

    List<Segnalazione> expectedList = Arrays.asList(segnalazione1);

    when(repository.findByStato(StatoSegnalazione.IN_ELABORAZIONE)).thenReturn(expectedList);

    List<Segnalazione> result = segnalazioniService.getSegnalazioniByStato(
        StatoSegnalazione.IN_ELABORAZIONE);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(StatoSegnalazione.IN_ELABORAZIONE, result.get(0).getStato());
    verify(repository).findByStato(StatoSegnalazione.IN_ELABORAZIONE);
  }

  @Test
  void getSegnalazioniByStatoRisolta() {
    Segnalazione segnalazione1 = new Segnalazione();
    segnalazione1.setId(4L);
    segnalazione1.setDescrizione("Segnalazione risolta 1");
    segnalazione1.setStato(StatoSegnalazione.RISOLTA);

    Segnalazione segnalazione2 = new Segnalazione();
    segnalazione2.setId(5L);
    segnalazione2.setDescrizione("Segnalazione risolta 2");
    segnalazione2.setStato(StatoSegnalazione.RISOLTA);

    Segnalazione segnalazione3 = new Segnalazione();
    segnalazione3.setId(6L);
    segnalazione3.setDescrizione("Segnalazione risolta 3");
    segnalazione3.setStato(StatoSegnalazione.RISOLTA);

    List<Segnalazione> expectedList = Arrays.asList(segnalazione1, segnalazione2, segnalazione3);

    when(repository.findByStato(StatoSegnalazione.RISOLTA)).thenReturn(expectedList);

    List<Segnalazione> result = segnalazioniService.getSegnalazioniByStato(StatoSegnalazione.RISOLTA);

    assertNotNull(result);
    assertEquals(3, result.size());
    assertEquals(StatoSegnalazione.RISOLTA, result.get(0).getStato());
    assertEquals(StatoSegnalazione.RISOLTA, result.get(1).getStato());
    assertEquals(StatoSegnalazione.RISOLTA, result.get(2).getStato());
    verify(repository).findByStato(StatoSegnalazione.RISOLTA);
  }

  @Test
  void saveSegnalazioneWithAllFields() throws Exception {
    Segnalazione segnalazione = new Segnalazione();
    segnalazione.setId(10L);
    segnalazione.setDescrizione("Segnalazione completa");
    segnalazione.setDataSegnalazione(new Date());
    segnalazione.setStato(StatoSegnalazione.RISOLTA);
    segnalazione.setForRecensione(true);
    segnalazione.setMedia("media-uuid-123");

    Utente utente = new Utente();
    utente.setId(5L);
    segnalazione.setUtente(utente);

    when(repository.save(any(Segnalazione.class))).thenReturn(segnalazione);

    Segnalazione savedSegnalazione = segnalazioniService.saveSegnalazione(segnalazione);

    assertNotNull(savedSegnalazione);
    assertEquals(10L, savedSegnalazione.getId());
    assertEquals("Segnalazione completa", savedSegnalazione.getDescrizione());
    assertEquals(StatoSegnalazione.RISOLTA, savedSegnalazione.getStato());
    assertEquals(true, savedSegnalazione.isForRecensione());
    assertEquals("media-uuid-123", savedSegnalazione.getMedia());
    verify(repository).save(segnalazione);
  }

}