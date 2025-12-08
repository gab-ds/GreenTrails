package it.greentrails.backend.gestioneattivita.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Camera;
import it.greentrails.backend.gestioneattivita.repository.CameraRepository;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CameraServiceImplTest {

  @Mock
  private CameraRepository cameraRepository;

  @InjectMocks
  private CameraServiceImpl cameraService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testGetCamereByAlloggio_NullAlloggio() {
    assertThrows(Exception.class, () -> {
      cameraService.getCamereByAlloggio(null);
    });
  }

  @Test
  public void testGetCamereByAlloggio_NotAlloggio() {
    Attivita alloggio = new Attivita();
    alloggio.setAlloggio(false);

    assertThrows(Exception.class, () -> {
      cameraService.getCamereByAlloggio(alloggio);
    });
  }

  @Test
  public void testGetCamereByAlloggio_ValidAlloggio() throws Exception {
    Attivita alloggio = new Attivita();
    alloggio.setAlloggio(true);
    alloggio.setId(1L);

    Camera camera = new Camera();
    camera.setAlloggio(alloggio);

    List<Camera> camere = Arrays.asList(camera);
    when(cameraRepository.findAll()).thenReturn(camere);

    List<Camera> foundCamere = cameraService.getCamereByAlloggio(alloggio);

    assertNotNull(foundCamere);
    assertEquals(1, foundCamere.size());
    assertTrue(foundCamere.contains(camera));
  }

}
