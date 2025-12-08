package it.greentrails.backend.gestionesegnalazioni.controller;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Recensione;
import it.greentrails.backend.entities.Segnalazione;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.enums.RuoloUtente;
import it.greentrails.backend.enums.StatoSegnalazione;
import it.greentrails.backend.gestioneattivita.service.AttivitaService;
import it.greentrails.backend.gestioneattivita.service.RecensioneService;
import it.greentrails.backend.gestionesegnalazioni.service.SegnalazioniService;
import it.greentrails.backend.gestioneupload.service.ArchiviazioneService;
import it.greentrails.backend.utils.service.ResponseGenerator;
import java.util.Date;
import java.util.UUID;
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
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/segnalazioni")
@RequiredArgsConstructor
public class SegnalazioniController {

  private final SegnalazioniService segnalazioniService;
  private final AttivitaService attivitaService;
  private final RecensioneService recensioneService;
  private final ArchiviazioneService archiviazioneService;

  @PostMapping
  private ResponseEntity<Object> creaSegnalazione(
      @AuthenticationPrincipal Utente utente,
      @RequestParam("descrizione") final String descrizione,
      @RequestParam(value = "immagine", required = false) final MultipartFile immagine,
      @RequestParam(value = "idAttivita", required = false) final Long idAttivita,
      @RequestParam(value = "idRecensione", required = false) final Long idRecensione
  ) {
    try {
      Segnalazione segnalazione = new Segnalazione();
      segnalazione.setUtente(utente);
      segnalazione.setDataSegnalazione(new Date());
      segnalazione.setDescrizione(descrizione);
      if (immagine != null && !immagine.isEmpty()) {
        String media = UUID.randomUUID().toString();
        segnalazione.setMedia(media);
        archiviazioneService.store(media, immagine);
      }
      if (utente.getRuolo() == RuoloUtente.VISITATORE && idAttivita != null) {
        Attivita attivita = attivitaService.findById(idAttivita);
        segnalazione.setAttivita(attivita);
        return ResponseGenerator.generateResponse(HttpStatus.OK,
            segnalazioniService.saveSegnalazione(segnalazione));
      } else if (utente.getRuolo() == RuoloUtente.GESTORE_ATTIVITA && idRecensione != null) {
        Recensione recensione = recensioneService.findById(idRecensione);
        segnalazione.setRecensione(recensione);
        return ResponseGenerator.generateResponse(HttpStatus.OK,
            segnalazioniService.saveSegnalazione(segnalazione));
      }
      return ResponseGenerator.generateResponse(HttpStatus.BAD_REQUEST,
          "Questo utente non può creare questo tipo di segnalazione.");
    } catch (Exception e) {
      return ResponseGenerator.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

  @GetMapping("{id}")
  private ResponseEntity<Object> visualizzaSegnalazione(
      @PathVariable("id") final long id
  ) {
    try {
      return ResponseGenerator.generateResponse(HttpStatus.OK, segnalazioniService.findById(id));
    } catch (Exception e) {
      return ResponseGenerator.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

  @GetMapping
  private ResponseEntity<Object> visualizzaSegnalazioniPerTipo(
      @RequestParam("isForRecensione") final boolean isForRecensione
  ) {
    return ResponseGenerator.generateResponse(HttpStatus.OK,
        segnalazioniService.getAllSegnalazioniByTipo(isForRecensione));
  }

  @DeleteMapping("{id}")
  private ResponseEntity<Object> chiudiSegnalazione(
      @PathVariable("id") final long id,
      @RequestParam(value = "chiarimenti", required = false) final String chiarimenti
  ) {
    try {
      Segnalazione segnalazione = segnalazioniService.findById(id);
      segnalazione.setStato(StatoSegnalazione.RISOLTA);
      return ResponseGenerator.generateResponse(HttpStatus.OK,
          segnalazioniService.saveSegnalazione(segnalazione));
    } catch (Exception e) {
      return ResponseGenerator.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

}
