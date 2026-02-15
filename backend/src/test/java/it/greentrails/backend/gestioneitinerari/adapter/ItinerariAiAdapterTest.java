package it.greentrails.backend.gestioneitinerari.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import it.greentrails.backend.entities.Itinerario;
import it.greentrails.backend.entities.Preferenze;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.enums.PreferenzeAlimentari;
import it.greentrails.backend.enums.PreferenzeAlloggio;
import it.greentrails.backend.enums.PreferenzeAttivita;
import it.greentrails.backend.enums.PreferenzeBudget;
import it.greentrails.backend.enums.PreferenzeStagione;
import it.greentrails.backend.enums.PreferenzeViaggio;
import it.greentrails.backend.enums.RuoloUtente;
import it.greentrails.backend.gestioneprenotazioni.repository.PrenotazioneAlloggioRepository;
import it.greentrails.backend.gestioneprenotazioni.repository.PrenotazioneAttivitaTuristicaRepository;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@Disabled("Test disabilitati in attesa della manutenzione evolutiva prevista per ItinerariAiAdapter.")
class ItinerariAiAdapterTest {

  private ItinerariAiAdapter itinerariAiAdapter;

  @Mock
  private PrenotazioneAttivitaTuristicaRepository prenotazioneAttivitaTuristicaRepository;

  @Mock
  private PrenotazioneAlloggioRepository prenotazioneAlloggioRepository;

  private Preferenze preferenze;
  private Utente visitatore;

  @BeforeEach
  void setUp() {
    itinerariAiAdapter = new ItinerariAiAdapter();

    visitatore = new Utente();
    visitatore.setId(1L);
    visitatore.setNome("Mario");
    visitatore.setCognome("Rossi");
    visitatore.setEmail("mario.rossi@example.com");
    visitatore.setRuolo(RuoloUtente.VISITATORE);

    preferenze = new Preferenze();
    preferenze.setId(1L);
    preferenze.setVisitatore(visitatore);
    preferenze.setViaggioPreferito(PreferenzeViaggio.MONTAGNA);
    preferenze.setAlloggioPreferito(PreferenzeAlloggio.HOTEL);
    preferenze.setPreferenzaAlimentare(PreferenzeAlimentari.NESSUNA_PREFERENZA);
    preferenze.setAttivitaPreferita(PreferenzeAttivita.ALL_APERTO);
    preferenze.setAnimaleDomestico(false);
    preferenze.setBudgetPreferito(PreferenzeBudget.MEDIO);
    preferenze.setSouvenir(false);
    preferenze.setStagioniPreferite(PreferenzeStagione.PRIMAVERA_ESTATE);
  }

  @Test
  void testPianificazioneAutomatica_RestituisceItinerarioNonNull() {
    // Act
    Itinerario result = itinerariAiAdapter.pianificazioneAutomatica(preferenze);

    // Assert
    assertNotNull(result, "L'itinerario non deve essere null");
  }

  @Test
  void testPianificazioneAutomatica_ImpostaVisitatore() {
    // Act
    Itinerario result = itinerariAiAdapter.pianificazioneAutomatica(preferenze);

    // Assert
    assertNotNull(result);
    assertEquals(visitatore, result.getVisitatore(), "Il visitatore deve essere impostato correttamente");
  }

  @Test
  void testPianificazioneAutomatica_DeveSalvareItinerarioConId() {
    // Act
    Itinerario result = itinerariAiAdapter.pianificazioneAutomatica(preferenze);

    // Assert
    assertNotNull(result.getId());
  }

  @Test
  void testPianificazioneAutomatica_DeveCreareAlmenoTreAttivitaTuristiche() {
    // Arrange
    Page<it.greentrails.backend.entities.PrenotazioneAttivitaTuristica> emptyPage =
        new PageImpl<>(Collections.emptyList());
    when(prenotazioneAttivitaTuristicaRepository.findByItinerario(any(Long.class), any(Pageable.class)))
        .thenReturn(emptyPage);

    // Act
    Itinerario result = itinerariAiAdapter.pianificazioneAutomatica(preferenze);

    // Assert
    assertNotNull(result);
    assertNotNull(result.getId());
    var prenotazioni = prenotazioneAttivitaTuristicaRepository
        .findByItinerario(result.getId(), Pageable.unpaged()).toList();
    assertTrue(prenotazioni.size() >= 3 && prenotazioni.size() <= 4);
  }

  @Test
  void testPianificazioneAutomatica_DeveCreareEsattamenteUnAlloggio() {
    // Arrange
    Page<it.greentrails.backend.entities.PrenotazioneAlloggio> emptyPage =
        new PageImpl<>(Collections.emptyList());
    when(prenotazioneAlloggioRepository.findByItinerario(any(Long.class), any(Pageable.class)))
        .thenReturn(emptyPage);

    // Act
    Itinerario result = itinerariAiAdapter.pianificazioneAutomatica(preferenze);

    // Assert
    assertNotNull(result);
    assertNotNull(result.getId());
    var prenotazioni = prenotazioneAlloggioRepository
        .findByItinerario(result.getId(), Pageable.unpaged()).toList();
    assertEquals(1, prenotazioni.size());
  }

  @Test
  void testPianificazioneAutomatica_DeveRispettarePreferenze() {
    // Act
    Itinerario result = itinerariAiAdapter.pianificazioneAutomatica(preferenze);

    // Assert
    assertNotNull(result);
    assertNotNull(result.getId());
    assertTrue(result.getTotale() > 0);
  }

  @Test
  void testPianificazioneAutomatica_DeveCalcolareTotale() {
    // Act
    Itinerario result = itinerariAiAdapter.pianificazioneAutomatica(preferenze);

    // Assert
    assertNotNull(result);
    assertTrue(result.getTotale() > 0);
  }

  @Test
  void testPianificazioneAutomatica_ConPreferenzeDiverse_GeneraItinerari() {
    // Arrange
    Preferenze preferenze2 = new Preferenze();
    preferenze2.setId(2L);
    preferenze2.setVisitatore(visitatore);
    preferenze2.setViaggioPreferito(PreferenzeViaggio.MARE);
    preferenze2.setAlloggioPreferito(PreferenzeAlloggio.BED_AND_BREAKFAST);
    preferenze2.setPreferenzaAlimentare(PreferenzeAlimentari.VEGAN);
    preferenze2.setAttivitaPreferita(PreferenzeAttivita.RELAX);
    preferenze2.setAnimaleDomestico(true);
    preferenze2.setBudgetPreferito(PreferenzeBudget.ALTO);
    preferenze2.setSouvenir(true);
    preferenze2.setStagioniPreferite(PreferenzeStagione.AUTUNNO_INVERNO);

    // Act
    Itinerario result1 = itinerariAiAdapter.pianificazioneAutomatica(preferenze);
    Itinerario result2 = itinerariAiAdapter.pianificazioneAutomatica(preferenze2);

    // Assert
    assertNotNull(result1);
    assertNotNull(result2);
    assertEquals(visitatore, result1.getVisitatore());
    assertEquals(visitatore, result2.getVisitatore());
  }
}