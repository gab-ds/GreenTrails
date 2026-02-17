package it.greentrails.backend.gestioneattivita.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import it.greentrails.backend.entities.Categoria;
import it.greentrails.backend.gestioneattivita.repository.CategoriaRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CategoriaServiceImplTest {

  @Mock
  private CategoriaRepository categoriaRepository;

  @InjectMocks
  private CategoriaServiceImpl categoriaService;

  private Validator validator;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void saveCategoriaSuccess() throws Exception {
    Categoria categoria = new Categoria();
    categoria.setNome("Relax e Benessere");
    categoria.setDescrizione(
        "Attività e alloggi per il relax e il benessere."
            + " Include spa, centri benessere, o alloggi con servizi di spa e benessere.");

    when(categoriaRepository.save(any())).thenReturn(categoria);

    Categoria savedCategoria = categoriaService.saveCategoria(categoria);

    assertNotNull(savedCategoria);

  }

  @Test
  void saveCategoriaNullCategoriaExceptionThrown() {
    Categoria nullCategoria = null;

    assertThrows(Exception.class, () -> categoriaService.saveCategoria(nullCategoria));

  }

  @Test
  void saveCategoriaNullNameExceptionThrown() {
    Categoria categoria = new Categoria();
    categoria.setNome(null);
    categoria.setDescrizione(
        "Attività e alloggi per il relax e il benessere."
            + " Include spa, centri benessere, o alloggi con servizi di spa e benessere."
    );

    Set<ConstraintViolation<Categoria>> violations = validator.validate(categoria);
    assertFalse(violations.isEmpty());
    assertEquals("Il nome non può essere vuoto.",
        violations.iterator().next().getMessage());
  }

  @Test
  void saveCategoriaNullDescriptionExceptionThrown() {
    Categoria categoria = new Categoria();
    categoria.setNome("Relax e Benessere");
    categoria.setDescrizione(null);

    Set<ConstraintViolation<Categoria>> violations = validator.validate(categoria);
    assertFalse(violations.isEmpty());
    assertEquals("La descrizione non può essere vuota.",
        violations.iterator().next().getMessage());
  }

  @Test
  void deleteCategoriaSuccess() throws Exception {
    Categoria categoria = new Categoria();
    categoria.setId(1L);

    // configura il mock per restituire empty quando viene chiamato findById
    // (simulando che la categoria è stata cancellata con successo)
    when(categoriaRepository.findById(1L))
        .thenReturn(java.util.Optional.empty());

    boolean result = categoriaService.deleteCategoria(categoria);

    assertTrue(result);

    // verifica che delete e flush siano stati chiamati
    org.mockito.Mockito.verify(categoriaRepository).delete(categoria);
    org.mockito.Mockito.verify(categoriaRepository).flush();
  }

  @Test
  void deleteCategoriaNullCategoriaExceptionThrown() {
    assertThrows(Exception.class, () -> categoriaService.deleteCategoria(null));
  }

  @Test
  void deleteCategoriaFailureReturnsFalse() throws Exception {
    Categoria categoria = new Categoria();
    categoria.setId(1L);

    // simula che l'entità non venga cancellata (ancora presente dopo delete/flush)
    when(categoriaRepository.findById(1L))
        .thenReturn(java.util.Optional.of(categoria));

    boolean result = categoriaService.deleteCategoria(categoria);

    assertFalse(result);
  }

  @Test
  void findByIdSuccess() throws Exception {
    Categoria categoria = new Categoria();
    categoria.setId(2L);

    when(categoriaRepository.findById(2L)).thenReturn(java.util.Optional.of(categoria));

    Categoria found = categoriaService.findById(2L);

    assertNotNull(found);
    assertEquals(2L, found.getId());
  }

  @Test
  void findByIdInvalidIdExceptionThrown() {
    assertThrows(Exception.class, () -> categoriaService.findById(-1L));
    assertThrows(Exception.class, () -> categoriaService.findById(null));
  }

  @Test
  void findByIdNotFoundExceptionThrown() {
    when(categoriaRepository.findById(3L)).thenReturn(java.util.Optional.empty());

    assertThrows(Exception.class, () -> categoriaService.findById(3L));
  }

}
