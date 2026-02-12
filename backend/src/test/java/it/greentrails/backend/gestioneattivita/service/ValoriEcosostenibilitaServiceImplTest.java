package it.greentrails.backend.gestioneattivita.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.greentrails.backend.entities.ValoriEcosostenibilita;
import it.greentrails.backend.gestioneattivita.repository.ValoriEcosostenibilitaRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ValoriEcosostenibilitaServiceImplTest {

  @Mock
  private ValoriEcosostenibilitaRepository repository;

  @InjectMocks
  private ValoriEcosostenibilitaServiceImpl valoriEcosostenibilitaService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void saveValoriSuccess() throws Exception {
    ValoriEcosostenibilita valori = new ValoriEcosostenibilita();
    valori.setId(1L);
    valori.setPoliticheAntispreco(true);
    valori.setProdottiLocali(true);
    valori.setEnergiaVerde(false);
    valori.setRaccoltaDifferenziata(true);
    valori.setLimiteEmissioneCO2(true);
    valori.setContattoConNatura(true);

    when(repository.save(any(ValoriEcosostenibilita.class))).thenReturn(valori);

    ValoriEcosostenibilita savedValori = valoriEcosostenibilitaService.saveValori(valori);

    assertNotNull(savedValori);
    assertEquals(1L, savedValori.getId());
    assertTrue(savedValori.getPoliticheAntispreco());
    verify(repository).save(valori);
  }

  @Test
  void saveValoriNullExceptionThrown() {
    ValoriEcosostenibilita nullValori = null;

    Exception exception = assertThrows(Exception.class, () -> {
      valoriEcosostenibilitaService.saveValori(nullValori);
    });

    assertEquals("Non è possibile salvare questo valore di ecosostenibilità.",
        exception.getMessage());
  }

  @Test
  void deleteValoriSuccess() throws Exception {
    ValoriEcosostenibilita valori = new ValoriEcosostenibilita();
    valori.setId(1L);

    when(repository.findById(1L)).thenReturn(Optional.empty());

    boolean result = valoriEcosostenibilitaService.deleteValori(valori);

    assertTrue(result);
    verify(repository).delete(valori);
    verify(repository).flush();
    verify(repository).findById(1L);
  }

  @Test
  void deleteValoriNullExceptionThrown() {
    ValoriEcosostenibilita nullValori = null;

    Exception exception = assertThrows(Exception.class, () -> {
      valoriEcosostenibilitaService.deleteValori(nullValori);
    });

    assertEquals("Non è possibile cancellare questo valore di ecosostenibilità.",
        exception.getMessage());
  }

  @Test
  void deleteValoriFailure() throws Exception {
    ValoriEcosostenibilita valori = new ValoriEcosostenibilita();
    valori.setId(1L);

    when(repository.findById(1L)).thenReturn(Optional.of(valori));

    boolean result = valoriEcosostenibilitaService.deleteValori(valori);

    assertFalse(result);
    verify(repository).delete(valori);
    verify(repository).flush();
    verify(repository).findById(1L);
  }

  @Test
  void findByIdSuccess() throws Exception {
    ValoriEcosostenibilita valori = new ValoriEcosostenibilita();
    valori.setId(1L);
    valori.setPoliticheAntispreco(true);
    valori.setProdottiLocali(false);

    when(repository.findById(1L)).thenReturn(Optional.of(valori));

    ValoriEcosostenibilita foundValori = valoriEcosostenibilitaService.findById(1L);

    assertNotNull(foundValori);
    assertEquals(1L, foundValori.getId());
    assertTrue(foundValori.getPoliticheAntispreco());
    verify(repository).findById(1L);
  }

  @Test
  void findByIdNullIdExceptionThrown() {
    Long nullId = null;

    Exception exception = assertThrows(Exception.class, () -> {
      valoriEcosostenibilitaService.findById(nullId);
    });

    assertEquals("L'id non è valido.", exception.getMessage());
  }

  @Test
  void findByIdNegativeIdExceptionThrown() {
    Long negativeId = -1L;

    Exception exception = assertThrows(Exception.class, () -> {
      valoriEcosostenibilitaService.findById(negativeId);
    });

    assertEquals("L'id non è valido.", exception.getMessage());
  }

  @Test
  void findByIdNotFoundExceptionThrown() {
    Long id = 999L;

    when(repository.findById(id)).thenReturn(Optional.empty());

    Exception exception = assertThrows(Exception.class, () -> {
      valoriEcosostenibilitaService.findById(id);
    });

    assertEquals("I valori non sono stati trovati.", exception.getMessage());
  }

}