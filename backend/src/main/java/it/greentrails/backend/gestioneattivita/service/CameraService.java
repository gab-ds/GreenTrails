package it.greentrails.backend.gestioneattivita.service;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Camera;
import java.util.List;

/*@ nullable_by_default @*/
public interface CameraService {

  /*@
    @ ensures \result != null;
    @*/
  Camera saveCamera(/*@ nullable @*/ Camera camera) throws Exception;

  /*@
    @ ensures \result != null;
    @*/
  Camera findById(/*@ nullable @*/ Long id) throws Exception;

  /*@
    @ ensures \result != null;
    @*/
  List<Camera> getCamereByAlloggio(/*@ nullable @*/ Attivita alloggio) throws Exception;

  boolean deleteCamera(/*@ nullable @*/ Camera camera) throws Exception;
}
