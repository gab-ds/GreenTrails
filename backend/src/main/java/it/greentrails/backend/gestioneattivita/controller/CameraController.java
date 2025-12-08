package it.greentrails.backend.gestioneattivita.controller;


import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Camera;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.gestioneattivita.service.AttivitaService;
import it.greentrails.backend.gestioneattivita.service.CameraService;
import it.greentrails.backend.utils.service.ResponseGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/camere")
@RequiredArgsConstructor
public class CameraController {


  private final AttivitaService attivitaService;
  private final CameraService cameraService;

  @PostMapping
  private ResponseEntity<Object> creaCamera(
      @AuthenticationPrincipal Utente utente,
      @RequestParam(value = "idAlloggio") final Long idAlloggio,
      @RequestParam("tipoCamera") final String tipoCamera,
      @RequestParam("disponibilita") final Integer disponibilita,
      @RequestParam("descrizione") final String descrizione,
      @RequestParam("capienza") final int capienza,
      @RequestParam("prezzo") final double prezzo
  ) {
    try {
      Attivita alloggio = attivitaService.findById(idAlloggio);
      if (!alloggio.getGestore().getId().equals(utente.getId())) {
        return ResponseGenerator.generateResponse(HttpStatus.NOT_FOUND, "Attività non trovata.");
      }
      if (!alloggio.isAlloggio()) {
        return ResponseGenerator.generateResponse(HttpStatus.BAD_REQUEST,
            "L'attività non è un alloggio.");
      }
      Camera camera = new Camera();
      camera.setAlloggio(alloggio);
      camera.setTipoCamera(tipoCamera);
      camera.setDisponibilita(disponibilita);
      camera.setDescrizione(descrizione);
      camera.setCapienza(capienza);
      camera.setPrezzo(prezzo);
      camera = cameraService.saveCamera(camera);
      return ResponseGenerator.generateResponse(HttpStatus.OK, camera);
    } catch (Exception e) {
      return ResponseGenerator.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

  @GetMapping("{id}")
  private ResponseEntity<Object> visualizzaCamera(
      @PathVariable("id") final Long id
  ) {
    try {
      return ResponseGenerator.generateResponse(HttpStatus.OK, cameraService.findById(id));
    } catch (Exception e) {
      return ResponseGenerator.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

  @GetMapping("perAlloggio/{idAlloggio}")
  private ResponseEntity<Object> visualizzaCamerePerAlloggio(
      @PathVariable("idAlloggio") final Long idAlloggio
  ) {
    try {
      Attivita alloggio = attivitaService.findById(idAlloggio);
      if (!alloggio.isAlloggio()) {
        return ResponseGenerator.generateResponse(HttpStatus.BAD_REQUEST,
            "L'attività non è un alloggio.");
      }
      return ResponseGenerator.generateResponse(HttpStatus.OK,
          cameraService.getCamereByAlloggio(alloggio));
    } catch (Exception e) {
      return ResponseGenerator.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

  @DeleteMapping("{id}")
  private ResponseEntity<Object> cancellaCamera(
      @AuthenticationPrincipal Utente utente,
      @PathVariable("id") final Long id
  ) {
    try {
      Camera camera = cameraService.findById(id);
      if (!camera.getAlloggio().getGestore().getId().equals(utente.getId())) {
        return ResponseGenerator.generateResponse(HttpStatus.NOT_FOUND, "Camera non trovata.");
      }
      return ResponseGenerator.generateResponse(HttpStatus.OK,
          cameraService.deleteCamera(camera));
    } catch (Exception e) {
      return ResponseGenerator.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }
}
