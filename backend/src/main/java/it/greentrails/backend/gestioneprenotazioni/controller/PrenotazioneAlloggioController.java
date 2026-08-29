package it.greentrails.backend.gestioneprenotazioni.controller;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Camera;
import it.greentrails.backend.entities.Itinerario;
import it.greentrails.backend.entities.PrenotazioneAlloggio;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.enums.StatoPrenotazione;
import it.greentrails.backend.gestioneattivita.service.AttivitaService;
import it.greentrails.backend.gestioneattivita.service.CameraService;
import it.greentrails.backend.gestioneitinerari.service.ItinerariService;
import it.greentrails.backend.gestioneprenotazioni.service.PrenotazioneAlloggioService;
import it.greentrails.backend.utils.service.ResponseGenerator;
import java.time.Duration;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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
@RequestMapping(path = "api/prenotazioni-alloggio")
@RequiredArgsConstructor
public class PrenotazioneAlloggioController {

  private final ItinerariService itinerariService;
  private final AttivitaService attivitaService;
  private final CameraService cameraService;
  private final PrenotazioneAlloggioService prenotazioneAlloggioService;


  @PostMapping
  private ResponseEntity<Object> creaPrenotazioneAlloggio(
      @AuthenticationPrincipal Utente utente,
      @RequestParam("idItinerario") final Long idItinerario,
      @RequestParam("idCamera") final Long idCamera,
      @RequestParam("numAdulti") final int adulti,
      @RequestParam(value = "numBambini", defaultValue = "0", required = false) final int bambini,
      @RequestParam("dataInizio") @DateTimeFormat(pattern = "yyyy-MM-dd") final Date dataInizio,
      @RequestParam("dataFine") @DateTimeFormat(pattern = "yyyy-MM-dd") final Date dataFine,
      @RequestParam("numCamere") final int numCamere
  ) {
    try {
      Itinerario itinerario = itinerariService.findById(idItinerario);
      if (!itinerario.getVisitatore().getId().equals(utente.getId())) {
        return ResponseGenerator.generateResponse(HttpStatus.NOT_FOUND,
            "Itinerario non trovato");
      }
      Camera camera = cameraService.findById(idCamera);
      if (adulti + bambini > camera.getCapienza() * numCamere) {
        return ResponseGenerator.generateResponse(HttpStatus.BAD_REQUEST,
            "Numero camere non sufficienti");
      }
      PrenotazioneAlloggio prenotazioneAlloggio = new PrenotazioneAlloggio();
      prenotazioneAlloggio.setCamera(camera);
      prenotazioneAlloggio.setItinerario(itinerario);
      prenotazioneAlloggio.setNumAdulti(adulti);
      prenotazioneAlloggio.setNumBambini(bambini);
      prenotazioneAlloggio.setNumCamere(numCamere);
      prenotazioneAlloggio.setDataInizio(dataInizio);
      prenotazioneAlloggio.setStato(StatoPrenotazione.CREATA);
      double prezzo = numCamere * camera.getPrezzo();
      prenotazioneAlloggio.setDataFine(dataFine);
      if (prenotazioneAlloggioService.controllaDisponibilitaCamera(camera,
          dataInizio, dataFine) < numCamere) {
        return ResponseGenerator.generateResponse(HttpStatus.BAD_REQUEST,
            "Camera non disponibile");
      }
      long durataOre = Duration.between(dataInizio.toInstant(), dataFine.toInstant()).toHours();
      if (durataOre > 24) {
        prezzo = prezzo * Math.ceil((double) durataOre / 24);
      }
      prenotazioneAlloggio.setPrezzo(prezzo);
      prenotazioneAlloggio = prenotazioneAlloggioService.savePrenotazioneAlloggio(camera,
          prenotazioneAlloggio);
      itinerario.setTotale(prezzo + itinerario.getTotale());
      itinerariService.saveItinerario(itinerario);
      return ResponseGenerator.generateResponse(HttpStatus.OK, prenotazioneAlloggio);
    } catch (Exception e) {
      return ResponseGenerator.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

  @PostMapping("{id}")
  private ResponseEntity<Object> confermaPrenotazioneAlloggio(
      @AuthenticationPrincipal Utente utente,
      @PathVariable("id") final long id,
      @RequestParam("numAdulti") final int adulti,
      @RequestParam(value = "numBambini", defaultValue = "0", required = false) final int bambini,
      @RequestParam("dataInizio") @DateTimeFormat(pattern = "yyyy-MM-dd") final Date dataInizio,
      @RequestParam("dataFine") @DateTimeFormat(pattern = "yyyy-MM-dd") final Date dataFine,
      @RequestParam("numCamere") final int numCamere
  ) {
    try {
      PrenotazioneAlloggio prenotazione = prenotazioneAlloggioService.findById(id);
      Itinerario itinerario = prenotazione.getItinerario();
      if (!itinerario.getVisitatore().getId().equals(utente.getId())) {
        return ResponseGenerator.generateResponse(HttpStatus.NOT_FOUND,
            "Prenotazione non trovata");
      }
      if (prenotazione.getStato() != StatoPrenotazione.NON_CONFERMATA) {
        return ResponseGenerator.generateResponse(HttpStatus.BAD_REQUEST,
            "Prenotazione non modificabile");
      }
      Camera camera = prenotazione.getCamera();
      if (adulti + bambini > camera.getCapienza() * numCamere) {
        return ResponseGenerator.generateResponse(HttpStatus.BAD_REQUEST,
            "Numero camere non sufficienti");
      }
      prenotazione.setNumAdulti(adulti);
      prenotazione.setNumBambini(bambini);
      prenotazione.setNumCamere(numCamere);
      prenotazione.setDataInizio(dataInizio);
      prenotazione.setStato(StatoPrenotazione.CREATA);
      double prezzo = numCamere * camera.getPrezzo();
      prenotazione.setDataFine(dataFine);
      if (prenotazioneAlloggioService.controllaDisponibilitaCamera(camera,
          dataInizio, dataFine) < numCamere) {
        return ResponseGenerator.generateResponse(HttpStatus.BAD_REQUEST,
            "Camera non disponibile");
      }
      long durataOre = Duration.between(dataInizio.toInstant(), dataFine.toInstant()).toHours();
      if (durataOre > 24) {
        prezzo = prezzo * Math.ceil((double) durataOre / 24);
      }
      prenotazione.setPrezzo(prezzo);
      prenotazione = prenotazioneAlloggioService.savePrenotazioneAlloggio(camera,
          prenotazione);
      itinerario.setTotale(prezzo + itinerario.getTotale());
      itinerariService.saveItinerario(itinerario);
      return ResponseGenerator.generateResponse(HttpStatus.OK, prenotazione);
    } catch (Exception e) {
      return ResponseGenerator.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

  @GetMapping("{id}")
  private ResponseEntity<Object> visualizzaPrenotazioneAlloggio(
      @AuthenticationPrincipal Utente utente,
      @PathVariable("id") final Long id
  ) {
    try {
      PrenotazioneAlloggio prenotazioneAlloggio = prenotazioneAlloggioService.findById(id);
      if (!prenotazioneAlloggio.getItinerario().getVisitatore().getId().equals(utente.getId())) {
        return ResponseGenerator.generateResponse(HttpStatus.NOT_FOUND, "Prenotazione non trovata");
      }
      return ResponseGenerator.generateResponse(HttpStatus.OK, prenotazioneAlloggio);
    } catch (Exception e) {
      return ResponseGenerator.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

  @GetMapping("perAttivita/{idAttivita}")
  private ResponseEntity<Object> visualizzaPrenotazioniAlloggioPerAttivita(
      @AuthenticationPrincipal Utente utente,
      @PathVariable("idAttivita") final long idAttivita
  ) {
    try {
      Attivita attivita = attivitaService.findById(idAttivita);
      if (!attivita.getGestore().getId().equals(utente.getId())) {
        return ResponseGenerator.generateResponse(HttpStatus.NOT_FOUND, "Alloggio non trovato");
      }
      return ResponseGenerator.generateResponse(HttpStatus.OK,
          prenotazioneAlloggioService.getPrenotazioniByAlloggio(attivita));
    } catch (Exception e) {
      return ResponseGenerator.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

  @GetMapping("perAttivita/{idAttivita}/disponibilita")
  private ResponseEntity<Object> visualizzaDisponibilitaPerAlloggio(
      @PathVariable("idAttivita") final long idAttivita,
      @RequestParam("dataInizio") @DateTimeFormat(pattern = "yyyy-MM-dd") final Date dataInizio,
      @RequestParam("dataFine") @DateTimeFormat(pattern = "yyyy-MM-dd") final Date dataFine
  ) {
    try {
      Attivita attivita = attivitaService.findById(idAttivita);
      return ResponseGenerator.generateResponse(HttpStatus.OK,
          prenotazioneAlloggioService.controllaDisponibilitaAlloggio(attivita, dataInizio,
              dataFine));
    } catch (Exception e) {
      return ResponseGenerator.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

  @GetMapping("perCamera/{idCamera}/disponibilita")
  private ResponseEntity<Object> visualizzaDisponibilitaPerCamera(
      @PathVariable("idCamera") final long idCamera,
      @RequestParam("dataInizio") @DateTimeFormat(pattern = "yyyy-MM-dd") final Date dataInizio,
      @RequestParam("dataFine") @DateTimeFormat(pattern = "yyyy-MM-dd") final Date dataFine
  ) {
    try {
      Camera camera = cameraService.findById(idCamera);
      return ResponseGenerator.generateResponse(HttpStatus.OK,
          prenotazioneAlloggioService.controllaDisponibilitaCamera(camera, dataInizio, dataFine));
    } catch (Exception e) {
      return ResponseGenerator.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

  @GetMapping
  private ResponseEntity<Object> visualizzaPrenotazioniAlloggioPerVisitatore(
      @AuthenticationPrincipal Utente utente
  ) {
    try {
      return ResponseGenerator.generateResponse(HttpStatus.OK,
          prenotazioneAlloggioService.getPrenotazioniByVisitatore(utente));
    } catch (Exception e) {
      return ResponseGenerator.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

  @DeleteMapping("{id}")
  private ResponseEntity<Object> cancellaPrenotazioneAlloggio(
      @AuthenticationPrincipal Utente utente,
      @PathVariable("id") final Long id
  ) {
    try {
      PrenotazioneAlloggio prenotazioneAlloggio = prenotazioneAlloggioService.findById(id);
      if (!prenotazioneAlloggio.getItinerario().getVisitatore().getId().equals(utente.getId())) {
        return ResponseGenerator.generateResponse(HttpStatus.NOT_FOUND, "Prenotazione non trovata");
      }
      Itinerario itinerario = prenotazioneAlloggio.getItinerario();
      itinerario.setTotale(itinerario.getTotale() - prenotazioneAlloggio.getPrezzo());
      itinerariService.saveItinerario(itinerario);
      return ResponseGenerator.generateResponse(HttpStatus.OK,
          prenotazioneAlloggioService.deletePrenotazioneAlloggio(prenotazioneAlloggio));
    } catch (Exception e) {
      return ResponseGenerator.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }


}
