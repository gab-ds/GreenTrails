package it.greentrails.backend.gestioneattivita.service;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Camera;
import it.greentrails.backend.gestioneattivita.repository.CameraRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CameraServiceImpl implements CameraService {

  private final CameraRepository repository;

  @Override
  public Camera saveCamera(Camera camera) throws Exception {
    if (camera == null) {
      throw new Exception("La camera è vuota.");
    }
    return repository.save(camera);
  }

  @Override
  public Camera findById(Long id) throws Exception {
    if (id == null || id < 0) {
      throw new Exception("L'id non è valido.");
    }
    Optional<Camera> camera = repository.findById(id);
    if (camera.isEmpty()) {
      throw new Exception("La recensione non è stata trovata.");
    }
    return camera.get();
  }

  @Override
  public List<Camera> getCamereByAlloggio(Attivita alloggio) throws Exception {
    if (alloggio == null) {
      throw new Exception("L'attività è vuota.");
    }
    if (!alloggio.isAlloggio()) {
      throw new Exception("L'attività non può essere un'attività turistica.");
    }
    List<Camera> camere = new ArrayList<>();
    repository.findAll().forEach(c -> {
      if (c.getAlloggio().getId().equals(alloggio.getId())) {
        camere.add(c);
      }
    });
    return camere;
  }

  @Override
  public boolean deleteCamera(Camera camera) throws Exception {
    if (camera == null) {
      throw new Exception("La camera è vuota.");
    }
    repository.delete(camera);
    repository.flush();
    return repository.findById(camera.getId()).isEmpty();
  }
}
