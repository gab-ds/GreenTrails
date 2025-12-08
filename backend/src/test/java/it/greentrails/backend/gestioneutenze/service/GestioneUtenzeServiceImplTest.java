package it.greentrails.backend.gestioneutenze.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import it.greentrails.backend.entities.Preferenze;
import it.greentrails.backend.gestioneutenze.repository.PreferenzeRepository;
import it.greentrails.backend.gestioneutenze.repository.UtenteRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
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
}