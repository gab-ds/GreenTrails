package it.greentrails.backend.gestionericerca.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Categoria;
import it.greentrails.backend.gestioneattivita.repository.AttivitaRepository;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.geo.Point;

class RicercaServiceImplTest {

  @Mock
  private AttivitaRepository repository;

  @InjectMocks
  private RicercaServiceImpl ricercaService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void findAttivitaSuccess() throws InvalidParameterException {
    String query = "Roma";
    Attivita attivita1 = new Attivita();
    attivita1.setId(1L);
    attivita1.setNome("Hotel Roma");
    attivita1.setCitta("Roma");

    Attivita attivita2 = new Attivita();
    attivita2.setId(2L);
    attivita2.setNome("Tour Roma");
    attivita2.setCitta("Roma");

    List<Attivita> expectedList = Arrays.asList(attivita1, attivita2);

    when(repository.findByQuery(query)).thenReturn(expectedList);

    List<Attivita> result = ricercaService.findAttivita(query);

    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("Hotel Roma", result.get(0).getNome());
    assertEquals("Tour Roma", result.get(1).getNome());
    verify(repository).findByQuery(query);
  }

  @Test
  void findAttivitaNullQueryExceptionThrown() {
    String nullQuery = null;

    InvalidParameterException exception = assertThrows(InvalidParameterException.class, () -> {
      ricercaService.findAttivita(nullQuery);
    });

    assertEquals("La query è vuota.", exception.getMessage());
  }

  @Test
  void findAttivitaBlankQueryExceptionThrown() {
    String blankQuery = "   ";

    InvalidParameterException exception = assertThrows(InvalidParameterException.class, () -> {
      ricercaService.findAttivita(blankQuery);
    });

    assertEquals("La query è vuota.", exception.getMessage());
  }

  @Test
  void findAttivitaEmptyQueryExceptionThrown() {
    String emptyQuery = "";

    InvalidParameterException exception = assertThrows(InvalidParameterException.class, () -> {
      ricercaService.findAttivita(emptyQuery);
    });

    assertEquals("La query è vuota.", exception.getMessage());
  }

  @Test
  void findAttivitaByCategorieSuccess() throws InvalidParameterException {
    Categoria categoria1 = new Categoria();
    categoria1.setId(1L);
    categoria1.setNome("Sport");

    Categoria categoria2 = new Categoria();
    categoria2.setId(2L);
    categoria2.setNome("Natura");

    Attivita attivita1 = new Attivita();
    attivita1.setId(1L);
    attivita1.setNome("Escursione");

    Attivita attivita2 = new Attivita();
    attivita2.setId(2L);
    attivita2.setNome("Trekking");

    Attivita attivita3 = new Attivita();
    attivita3.setId(3L);
    attivita3.setNome("Mountain Bike");

    List<Attivita> listCategoria1 = Arrays.asList(attivita1, attivita2, attivita3);
    List<Attivita> listCategoria2 = Arrays.asList(attivita1, attivita2);

    when(repository.findByCategoria(1L)).thenReturn(listCategoria1);
    when(repository.findByCategoria(2L)).thenReturn(listCategoria2);

    List<Categoria> categorie = Arrays.asList(categoria1, categoria2);
    List<Attivita> result = ricercaService.findAttivitaByCategorie(categorie);

    assertNotNull(result);
    assertEquals(2, result.size());
    assertTrue(result.contains(attivita1));
    assertTrue(result.contains(attivita2));
    verify(repository).findByCategoria(1L);
    verify(repository).findByCategoria(2L);
  }

  @Test
  void findAttivitaByCategorieNullListExceptionThrown() {
    List<Categoria> nullList = null;

    InvalidParameterException exception = assertThrows(InvalidParameterException.class, () -> {
      ricercaService.findAttivitaByCategorie(nullList);
    });

    assertEquals("La lista delle categorie è vuota", exception.getMessage());
  }

  @Test
  void findAttivitaByCategorieEmptyListExceptionThrown() {
    List<Categoria> emptyList = new ArrayList<>();

    InvalidParameterException exception = assertThrows(InvalidParameterException.class, () -> {
      ricercaService.findAttivitaByCategorie(emptyList);
    });

    assertEquals("La lista delle categorie è vuota", exception.getMessage());
  }

  @Test
  void findAttivitaByPosizioneSuccess() throws InvalidParameterException {
    Point coordinate = new Point(41.9028, 12.4964); // Roma
    double raggio = 5000.0; // 5 km

    Attivita attivita1 = new Attivita();
    attivita1.setId(1L);
    attivita1.setNome("Colosseo");
    attivita1.setCoordinate(new Point(41.8902, 12.4922));

    Attivita attivita2 = new Attivita();
    attivita2.setId(2L);
    attivita2.setNome("Fontana di Trevi");
    attivita2.setCoordinate(new Point(41.9009, 12.4833));

    Attivita attivita3 = new Attivita();
    attivita3.setId(3L);
    attivita3.setNome("Lontano");
    attivita3.setCoordinate(new Point(45.4642, 9.1900)); // Milano - molto lontano

    List<Attivita> allAttivita = Arrays.asList(attivita1, attivita2, attivita3);

    when(repository.findAll()).thenReturn(allAttivita);

    List<Attivita> result = ricercaService.findAttivitaByPosizione(coordinate, raggio);

    assertNotNull(result);
    assertEquals(2, result.size());
    verify(repository).findAll();
  }

  @Test
  void findAttivitaByPosizioneNullCoordinateExceptionThrown() {
    Point nullCoordinate = null;
    double raggio = 5000.0;

    InvalidParameterException exception = assertThrows(InvalidParameterException.class, () -> {
      ricercaService.findAttivitaByPosizione(nullCoordinate, raggio);
    });

    assertEquals("Le coordinate sono vuote.", exception.getMessage());
  }

  @Test
  void findAttivitaByPosizioneNegativeRaggioExceptionThrown() {
    Point coordinate = new Point(41.9028, 12.4964);
    double negativeRaggio = -100.0;

    InvalidParameterException exception = assertThrows(InvalidParameterException.class, () -> {
      ricercaService.findAttivitaByPosizione(coordinate, negativeRaggio);
    });

    assertEquals("Il raggio non è valido.", exception.getMessage());
  }

  @Test
  void findAttivitaByPosizioneZeroRaggioSuccess() throws InvalidParameterException {
    Point coordinate = new Point(41.9028, 12.4964);
    double zeroRaggio = 0.0;

    Attivita attivita1 = new Attivita();
    attivita1.setId(1L);
    attivita1.setNome("Stessa posizione");
    attivita1.setCoordinate(new Point(41.9028, 12.4964));

    Attivita attivita2 = new Attivita();
    attivita2.setId(2L);
    attivita2.setNome("Posizione diversa");
    attivita2.setCoordinate(new Point(41.9029, 12.4965));

    List<Attivita> allAttivita = Arrays.asList(attivita1, attivita2);

    when(repository.findAll()).thenReturn(allAttivita);

    List<Attivita> result = ricercaService.findAttivitaByPosizione(coordinate, zeroRaggio);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("Stessa posizione", result.get(0).getNome());
    verify(repository).findAll();
  }

  @Test
  void findAttivitaByCategorieNoCommonResults() throws InvalidParameterException {
    Categoria categoria1 = new Categoria();
    categoria1.setId(1L);
    categoria1.setNome("Sport");

    Categoria categoria2 = new Categoria();
    categoria2.setId(2L);
    categoria2.setNome("Cultura");

    Attivita attivita1 = new Attivita();
    attivita1.setId(1L);
    attivita1.setNome("Calcio");

    Attivita attivita2 = new Attivita();
    attivita2.setId(2L);
    attivita2.setNome("Museo");

    List<Attivita> listCategoria1 = Arrays.asList(attivita1);
    List<Attivita> listCategoria2 = Arrays.asList(attivita2);

    when(repository.findByCategoria(1L)).thenReturn(listCategoria1);
    when(repository.findByCategoria(2L)).thenReturn(listCategoria2);

    List<Categoria> categorie = Arrays.asList(categoria1, categoria2);
    List<Attivita> result = ricercaService.findAttivitaByCategorie(categorie);

    assertNotNull(result);
    assertEquals(0, result.size());
    verify(repository).findByCategoria(1L);
    verify(repository).findByCategoria(2L);
  }

  @Test
  void findAttivitaByPosizioneEmptyResults() throws InvalidParameterException {
    Point coordinate = new Point(41.9028, 12.4964);
    double raggio = 1.0; // 1 metro

    Attivita attivita1 = new Attivita();
    attivita1.setId(1L);
    attivita1.setNome("Lontano");
    attivita1.setCoordinate(new Point(45.4642, 9.1900));

    List<Attivita> allAttivita = Arrays.asList(attivita1);

    when(repository.findAll()).thenReturn(allAttivita);

    List<Attivita> result = ricercaService.findAttivitaByPosizione(coordinate, raggio);

    assertNotNull(result);
    assertEquals(0, result.size());
    verify(repository).findAll();
  }

}