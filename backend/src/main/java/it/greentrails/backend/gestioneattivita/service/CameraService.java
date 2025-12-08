package it.greentrails.backend.gestioneattivita.service;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Camera;
import java.util.List;

public interface CameraService {

  Camera saveCamera(Camera camera) throws Exception;

  Camera findById(Long id) throws Exception;

  List<Camera> getCamereByAlloggio(Attivita alloggio) throws Exception;

  boolean deleteCamera(Camera camera) throws Exception;
}
