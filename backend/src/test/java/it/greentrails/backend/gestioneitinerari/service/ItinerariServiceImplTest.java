package it.greentrails.backend.gestioneitinerari.service;

import static org.mockito.Mockito.when;

import it.greentrails.backend.entities.Itinerario;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.enums.RuoloUtente;
import it.greentrails.backend.gestioneitinerari.repository.ItinerariRepository;
import java.util.Collections;
import java.util.List;
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
}
