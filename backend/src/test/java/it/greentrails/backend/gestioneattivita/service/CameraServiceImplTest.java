package it.greentrails.backend.gestioneattivita.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Camera;
import it.greentrails.backend.gestioneattivita.repository.CameraRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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

  @Test
  public void testGetCamereByAlloggio_ValidAlloggio_MultipleCamere() throws Exception {
    Attivita alloggio1 = new Attivita();
    alloggio1.setAlloggio(true);
    alloggio1.setId(1L);

    Attivita alloggio2 = new Attivita();
    alloggio2.setAlloggio(true);
    alloggio2.setId(2L);

    Camera camera1 = new Camera();
    camera1.setAlloggio(alloggio1);

    Camera camera2 = new Camera();
    camera2.setAlloggio(alloggio2);

    Camera camera3 = new Camera();
    camera3.setAlloggio(alloggio1);

    List<Camera> camere = Arrays.asList(camera1, camera2, camera3);
    when(cameraRepository.findAll()).thenReturn(camere);

    List<Camera> foundCamere = cameraService.getCamereByAlloggio(alloggio1);

    assertNotNull(foundCamere);
    assertEquals(2, foundCamere.size());
    assertTrue(foundCamere.contains(camera1));
    assertTrue(foundCamere.contains(camera3));
    assertFalse(foundCamere.contains(camera2));
  }

  // Test per saveCamera
  @Test
  public void testSaveCamera_NullCamera() {
    assertThrows(Exception.class, () -> {
      cameraService.saveCamera(null);
    });
  }

  @Test
  public void testSaveCamera_ValidCamera() throws Exception {
    Camera camera = new Camera();
    camera.setId(1L);
    camera.setTipoCamera("Doppia");
    camera.setPrezzo(100.0);

    when(cameraRepository.save(any(Camera.class))).thenReturn(camera);

    Camera savedCamera = cameraService.saveCamera(camera);

    assertNotNull(savedCamera);
    assertEquals(1L, savedCamera.getId());
    assertEquals("Doppia", savedCamera.getTipoCamera());
    verify(cameraRepository).save(camera);
  }

  // Test per findById
  @Test
  public void testFindById_NullId() {
    assertThrows(Exception.class, () -> {
      cameraService.findById(null);
    });
  }

  @Test
  public void testFindById_NegativeId() {
    assertThrows(Exception.class, () -> {
      cameraService.findById(-1L);
    });
  }

  @Test
  public void testFindById_NotFound() {
    when(cameraRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(Exception.class, () -> {
      cameraService.findById(1L);
    });
  }

  @Test
  public void testFindById_ValidId() throws Exception {
    Camera camera = new Camera();
    camera.setId(1L);
    camera.setTipoCamera("Doppia");

    when(cameraRepository.findById(1L)).thenReturn(Optional.of(camera));

    Camera foundCamera = cameraService.findById(1L);

    assertNotNull(foundCamera);
    assertEquals(1L, foundCamera.getId());
    assertEquals("Doppia", foundCamera.getTipoCamera());
  }

  // Test per deleteCamera
  @Test
  public void testDeleteCamera_NullCamera() {
    assertThrows(Exception.class, () -> {
      cameraService.deleteCamera(null);
    });
  }

  @Test
  public void testDeleteCamera_Successful() throws Exception {
    Camera camera = new Camera();
    camera.setId(1L);

    when(cameraRepository.findById(1L)).thenReturn(Optional.empty());

    boolean result = cameraService.deleteCamera(camera);

    assertTrue(result);
    verify(cameraRepository).delete(camera);
    verify(cameraRepository).flush();
  }

  @Test
  public void testDeleteCamera_Failed() throws Exception {
    Camera camera = new Camera();
    camera.setId(1L);

    when(cameraRepository.findById(1L)).thenReturn(Optional.of(camera));

    boolean result = cameraService.deleteCamera(camera);

    assertFalse(result);
    verify(cameraRepository).delete(camera);
    verify(cameraRepository).flush();
  }

}
