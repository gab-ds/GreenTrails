package it.greentrails.backend.gestioneprenotazioni.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Itinerario;
import it.greentrails.backend.entities.PrenotazioneAttivitaTuristica;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.enums.RuoloUtente;
import it.greentrails.backend.enums.StatoPrenotazione;
import it.greentrails.backend.gestioneprenotazioni.repository.PrenotazioneAttivitaTuristicaRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class PrenotazioneAttivitaTuristicaServiceImplTest {

  @Mock
  PrenotazioneAttivitaTuristicaRepository repositoryMock;

  @InjectMocks
  PrenotazioneAttivitaTuristicaServiceImpl service;

  @Test
  void testGetPrenotazioniByVisitatoreWithNullVisitatore() {
    assertThrows(Exception.class, () -> {
      service.getPrenotazioniByVisitatore(null);
    });
  }

  @Test
  void testGetPrenotazioniByVisitatoreWithNonVisitatore() {
    Utente visitatore = new Utente();
    visitatore.setId(1L);
    visitatore.setRuolo(RuoloUtente.GESTORE_ATTIVITA);

    assertThrows(Exception.class, () -> {
      service.getPrenotazioniByVisitatore(visitatore);
    });
  }

  @Test
  void testGetPrenotazioniByVisitatoreWithValidVisitatore() throws Exception {
    Utente visitatore = new Utente();
    visitatore.setId(1L);
    visitatore.setRuolo(RuoloUtente.VISITATORE);

    List<PrenotazioneAttivitaTuristica> prenotazioni = new ArrayList<>();
    Page<PrenotazioneAttivitaTuristica> page = new PageImpl<>(prenotazioni);

    when(repositoryMock.findByVisitatore(eq(1L), any(Pageable.class))).thenReturn(page);

    List<PrenotazioneAttivitaTuristica> risultato = service.getPrenotazioniByVisitatore(visitatore);

    assertNotNull(risultato);
  }

  // Test per savePrenotazioneAttivitaTuristica
  @Test
  void testSavePrenotazioneAttivitaTuristicaWithNullPrenotazione() {
    Attivita attivita = new Attivita();
    assertThrows(Exception.class, () -> {
      service.savePrenotazioneAttivitaTuristica(attivita, null);
    });
  }

  @Test
  void testSavePrenotazioneAttivitaTuristicaWithNullAttivita() {
    PrenotazioneAttivitaTuristica prenotazione = new PrenotazioneAttivitaTuristica();
    assertThrows(Exception.class, () -> {
      service.savePrenotazioneAttivitaTuristica(null, prenotazione);
    });
  }

  @Test
  void testSavePrenotazioneAttivitaTuristicaWithAlloggio() {
    Attivita attivita = new Attivita();
    attivita.setAlloggio(true);
    PrenotazioneAttivitaTuristica prenotazione = new PrenotazioneAttivitaTuristica();

    assertThrows(Exception.class, () -> {
      service.savePrenotazioneAttivitaTuristica(attivita, prenotazione);
    });
  }

  @Test
  void testSavePrenotazioneAttivitaTuristicaWithValidData() throws Exception {
    Attivita attivita = new Attivita();
    attivita.setId(1L);
    attivita.setAlloggio(false);

    PrenotazioneAttivitaTuristica prenotazione = new PrenotazioneAttivitaTuristica();
    PrenotazioneAttivitaTuristica savedPrenotazione = new PrenotazioneAttivitaTuristica();
    savedPrenotazione.setId(1L);

    when(repositoryMock.save(any(PrenotazioneAttivitaTuristica.class))).thenReturn(savedPrenotazione);

    PrenotazioneAttivitaTuristica risultato = service.savePrenotazioneAttivitaTuristica(attivita, prenotazione);

    assertNotNull(risultato);
    verify(repositoryMock).save(prenotazione);
  }

  // Test per deletePrenotazioneAttivitaTuristica
  @Test
  void testDeletePrenotazioneAttivitaTuristicaWithNull() {
    assertThrows(Exception.class, () -> {
      service.deletePrenotazioneAttivitaTuristica(null);
    });
  }

  @Test
  void testDeletePrenotazioneAttivitaTuristicaSuccessful() throws Exception {
    PrenotazioneAttivitaTuristica prenotazione = new PrenotazioneAttivitaTuristica();
    prenotazione.setId(1L);

    when(repositoryMock.findById(1L)).thenReturn(Optional.empty());

    boolean risultato = service.deletePrenotazioneAttivitaTuristica(prenotazione);

    assertTrue(risultato);
    verify(repositoryMock).delete(prenotazione);
    verify(repositoryMock).flush();
  }

  @Test
  void testDeletePrenotazioneAttivitaTuristicaFailed() throws Exception {
    PrenotazioneAttivitaTuristica prenotazione = new PrenotazioneAttivitaTuristica();
    prenotazione.setId(1L);

    when(repositoryMock.findById(1L)).thenReturn(Optional.of(prenotazione));

    boolean risultato = service.deletePrenotazioneAttivitaTuristica(prenotazione);

    assertFalse(risultato);
  }

  // Test per getAllPrenotazioniAttivitaTuristica
  @Test
  void testGetAllPrenotazioniAttivitaTuristica() {
    List<PrenotazioneAttivitaTuristica> prenotazioni = new ArrayList<>();
    prenotazioni.add(new PrenotazioneAttivitaTuristica());

    when(repositoryMock.findAll()).thenReturn(prenotazioni);

    List<PrenotazioneAttivitaTuristica> risultato = service.getAllPrenotazioniAttivitaTuristica();

    assertNotNull(risultato);
    assertEquals(1, risultato.size());
  }

  // Test per getPrenotazioniAttivitaTuristicaByStato
  @Test
  void testGetPrenotazioniAttivitaTuristicaByStatoWithNull() {
    assertThrows(Exception.class, () -> {
      service.getPrenotazioniAttivitaTuristicaByStato(null);
    });
  }

  @Test
  void testGetPrenotazioniAttivitaTuristicaByStatoWithValidStato() throws Exception {
    PrenotazioneAttivitaTuristica prenotazione1 = new PrenotazioneAttivitaTuristica();
    prenotazione1.setStato(StatoPrenotazione.CREATA);

    PrenotazioneAttivitaTuristica prenotazione2 = new PrenotazioneAttivitaTuristica();
    prenotazione2.setStato(StatoPrenotazione.COMPLETATA);

    List<PrenotazioneAttivitaTuristica> prenotazioni = new ArrayList<>();
    prenotazioni.add(prenotazione1);
    prenotazioni.add(prenotazione2);

    when(repositoryMock.findAll()).thenReturn(prenotazioni);

    List<PrenotazioneAttivitaTuristica> risultato =
        service.getPrenotazioniAttivitaTuristicaByStato(StatoPrenotazione.CREATA);

    assertNotNull(risultato);
    assertEquals(1, risultato.size());
    assertEquals(StatoPrenotazione.CREATA, risultato.get(0).getStato());
  }

  // Test per findById
  @Test
  void testFindByIdWithNull() {
    assertThrows(Exception.class, () -> {
      service.findById(null);
    });
  }

  @Test
  void testFindByIdWithNegativeId() {
    assertThrows(Exception.class, () -> {
      service.findById(-1L);
    });
  }

  @Test
  void testFindByIdNotFound() {
    when(repositoryMock.findById(1L)).thenReturn(Optional.empty());

    assertThrows(Exception.class, () -> {
      service.findById(1L);
    });
  }

  @Test
  void testFindByIdWithValidId() throws Exception {
    PrenotazioneAttivitaTuristica prenotazione = new PrenotazioneAttivitaTuristica();
    prenotazione.setId(1L);

    when(repositoryMock.findById(1L)).thenReturn(Optional.of(prenotazione));

    PrenotazioneAttivitaTuristica risultato = service.findById(1L);

    assertNotNull(risultato);
    assertEquals(1L, risultato.getId());
  }

  // Test per getPrenotazioniByAttivitaTuristica
  @Test
  void testGetPrenotazioniByAttivitaTuristicaWithNull() {
    assertThrows(Exception.class, () -> {
      service.getPrenotazioniByAttivitaTuristica(null);
    });
  }

  @Test
  void testGetPrenotazioniByAttivitaTuristicaWithValidAttivita() throws Exception {
    Attivita attivita = new Attivita();
    attivita.setId(1L);

    List<PrenotazioneAttivitaTuristica> prenotazioni = new ArrayList<>();
    Page<PrenotazioneAttivitaTuristica> page = new PageImpl<>(prenotazioni);

    when(repositoryMock.findByAttivitaTuristica(eq(1L), any(Pageable.class))).thenReturn(page);

    List<PrenotazioneAttivitaTuristica> risultato = service.getPrenotazioniByAttivitaTuristica(attivita);

    assertNotNull(risultato);
  }

  // Test per getPrenotazioniByItinerario
  @Test
  void testGetPrenotazioniByItinerarioWithNull() {
    assertThrows(Exception.class, () -> {
      service.getPrenotazioniByItinerario(null);
    });
  }

  @Test
  void testGetPrenotazioniByItinerarioWithValidItinerario() throws Exception {
    Itinerario itinerario = new Itinerario();
    itinerario.setId(1L);

    List<PrenotazioneAttivitaTuristica> prenotazioni = new ArrayList<>();
    Page<PrenotazioneAttivitaTuristica> page = new PageImpl<>(prenotazioni);

    when(repositoryMock.findByItinerario(eq(1L), any(Pageable.class))).thenReturn(page);

    List<PrenotazioneAttivitaTuristica> risultato = service.getPrenotazioniByItinerario(itinerario);

    assertNotNull(risultato);
  }

  // Test per controllaDisponibilitaAttivitaTuristica
  @Test
  void testControllaDisponibilitaAttivitaTuristicaWithNull() {
    assertThrows(Exception.class, () -> {
      service.controllaDisponibilitaAttivitaTuristica(null, new Date());
    });
  }

  @Test
  void testControllaDisponibilitaAttivitaTuristicaWithAlloggio() {
    Attivita attivita = new Attivita();
    attivita.setAlloggio(true);

    assertThrows(Exception.class, () -> {
      service.controllaDisponibilitaAttivitaTuristica(attivita, new Date());
    });
  }

  @Test
  void testControllaDisponibilitaAttivitaTuristicaWithValidData() throws Exception {
    Attivita attivita = new Attivita();
    attivita.setId(1L);
    attivita.setAlloggio(false);
    attivita.setDisponibilita(10);

    Date dataInizio = new Date();

    when(repositoryMock.getPostiOccupatiIn(1L, dataInizio)).thenReturn(3);

    int risultato = service.controllaDisponibilitaAttivitaTuristica(attivita, dataInizio);

    assertEquals(7, risultato);
  }
}
