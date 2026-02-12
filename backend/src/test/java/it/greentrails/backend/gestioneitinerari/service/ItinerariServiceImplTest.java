package it.greentrails.backend.gestioneitinerari.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.greentrails.backend.entities.Itinerario;
import it.greentrails.backend.entities.Preferenze;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.enums.RuoloUtente;
import it.greentrails.backend.gestioneitinerari.adapter.ItinerariAdapter;
import it.greentrails.backend.gestioneitinerari.repository.ItinerariRepository;
import it.greentrails.backend.gestioneprenotazioni.repository.PrenotazioneAlloggioRepository;
import it.greentrails.backend.gestioneprenotazioni.repository.PrenotazioneAttivitaTuristicaRepository;
import it.greentrails.backend.gestioneprenotazioni.service.PrenotazioneAlloggioService;
import it.greentrails.backend.gestioneprenotazioni.service.PrenotazioneAttivitaTuristicaService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class ItinerariServiceImplTest {

  @Mock
  private ItinerariRepository repository;

  @Mock
  private PrenotazioneAlloggioService prenotazioneAlloggioService;

  @Mock
  private PrenotazioneAttivitaTuristicaService prenotazioneAttivitaTuristicaService;

  @Mock
  private PrenotazioneAlloggioRepository prenotazioneAlloggioRepository;

  @Mock
  private PrenotazioneAttivitaTuristicaRepository prenotazioneAttivitaTuristicaRepository;

  @Mock
  private ItinerariAdapter itinerariStubAdapter;

  @InjectMocks
  private ItinerariServiceImpl itinerariService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  void testFindItinerariByUtente() throws Exception {
    Utente utente = new Utente();
    utente.setId(1L);
    utente.setRuolo(RuoloUtente.VISITATORE);

    when(repository.findByVisitatore(utente.getId(), Pageable.unpaged()))
        .thenReturn(new PageImpl<>(Collections.emptyList()));

    List<Itinerario> result = itinerariService.findItinerariByUtente(utente);

    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  void testFindItinerariByNullUtente() {
    Assertions.assertThrows(Exception.class, () -> {
      itinerariService.findItinerariByUtente(null);
    });
  }

  @Test
  void testFindItinerariByNonVisitorUtente() {
    Utente utenteNonVisitatore = new Utente();
    utenteNonVisitatore.setId(1L);
    utenteNonVisitatore.setRuolo(RuoloUtente.AMMINISTRATORE);

    Assertions.assertThrows(Exception.class, () -> {
      itinerariService.findItinerariByUtente(utenteNonVisitatore);
    });
  }

  // Test per saveItinerario
  @Test
  void testSaveItinerarioWithNull() {
    Assertions.assertThrows(Exception.class, () -> {
      itinerariService.saveItinerario(null);
    });
  }

  @Test
  void testSaveItinerarioWithValidItinerario() throws Exception {
    Itinerario itinerario = new Itinerario();
    itinerario.setId(1L);

    when(repository.save(any(Itinerario.class))).thenReturn(itinerario);

    Itinerario result = itinerariService.saveItinerario(itinerario);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1L, result.getId());
    verify(repository).save(itinerario);
  }

  // Test per createByPreferenze
  @Test
  void testCreateByPreferenzeWithNull() {
    Assertions.assertThrows(Exception.class, () -> {
      itinerariService.createByPreferenze(null);
    });
  }

  @Test
  void testCreateByPreferenzeWithValidPreferenze() throws Exception {
    Preferenze preferenze = new Preferenze();
    Itinerario itinerario = new Itinerario();
    itinerario.setId(1L);

    when(itinerariStubAdapter.pianificazioneAutomatica(any(Preferenze.class)))
        .thenReturn(itinerario);

    Itinerario result = itinerariService.createByPreferenze(preferenze);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1L, result.getId());
    verify(itinerariStubAdapter).pianificazioneAutomatica(preferenze);
  }

  // Test per deleteItinerario
  @Test
  void testDeleteItinerarioWithNull() {
    Assertions.assertThrows(Exception.class, () -> {
      itinerariService.deleteItinerario(null);
    });
  }

  @Test
  void testDeleteItinerarioSuccessful() throws Exception {
    Itinerario itinerario = new Itinerario();
    itinerario.setId(1L);

    when(prenotazioneAlloggioService.getPrenotazioniByItinerario(any(Itinerario.class)))
        .thenReturn(Collections.emptyList());
    when(prenotazioneAttivitaTuristicaService.getPrenotazioniByItinerario(any(Itinerario.class)))
        .thenReturn(Collections.emptyList());
    when(repository.findById(1L)).thenReturn(Optional.empty());

    boolean result = itinerariService.deleteItinerario(itinerario);

    Assertions.assertTrue(result);
    verify(prenotazioneAlloggioRepository).deleteAllInBatch(any());
    verify(prenotazioneAttivitaTuristicaRepository).deleteAllInBatch(any());
    verify(repository).delete(itinerario);
    verify(prenotazioneAlloggioRepository).flush();
    verify(prenotazioneAttivitaTuristicaRepository).flush();
    verify(repository).flush();
  }

  @Test
  void testDeleteItinerarioFailed() throws Exception {
    Itinerario itinerario = new Itinerario();
    itinerario.setId(1L);

    when(prenotazioneAlloggioService.getPrenotazioniByItinerario(any(Itinerario.class)))
        .thenReturn(Collections.emptyList());
    when(prenotazioneAttivitaTuristicaService.getPrenotazioniByItinerario(any(Itinerario.class)))
        .thenReturn(Collections.emptyList());
    when(repository.findById(1L)).thenReturn(Optional.of(itinerario));

    boolean result = itinerariService.deleteItinerario(itinerario);

    Assertions.assertFalse(result);
  }

  // Test per findById
  @Test
  void testFindByIdWithNull() {
    Assertions.assertThrows(Exception.class, () -> {
      itinerariService.findById(null);
    });
  }

  @Test
  void testFindByIdWithNegativeId() {
    Assertions.assertThrows(Exception.class, () -> {
      itinerariService.findById(-1L);
    });
  }

  @Test
  void testFindByIdNotFound() {
    when(repository.findById(1L)).thenReturn(Optional.empty());

    Assertions.assertThrows(Exception.class, () -> {
      itinerariService.findById(1L);
    });
  }

  @Test
  void testFindByIdWithValidId() throws Exception {
    Itinerario itinerario = new Itinerario();
    itinerario.setId(1L);

    when(repository.findById(1L)).thenReturn(Optional.of(itinerario));

    Itinerario result = itinerariService.findById(1L);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1L, result.getId());
  }
}
