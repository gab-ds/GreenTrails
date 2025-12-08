package it.greentrails.backend.gestioneattivita.controller;


import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Recensione;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.entities.ValoriEcosostenibilita;
import it.greentrails.backend.enums.RuoloUtente;
import it.greentrails.backend.gestioneattivita.service.AttivitaService;
import it.greentrails.backend.gestioneattivita.service.RecensioneService;
import it.greentrails.backend.gestioneattivita.service.ValoriEcosostenibilitaService;
import it.greentrails.backend.gestioneupload.service.ArchiviazioneService;
import it.greentrails.backend.gestioneutenze.service.GestioneUtenzeService;
import it.greentrails.backend.utils.service.ResponseGenerator;
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
@RequestMapping(path = "api/recensioni")
@RequiredArgsConstructor
public class RecensioneController {

  private final RecensioneService recensioneService;
  private final AttivitaService attivitaService;
  private final GestioneUtenzeService gestioneUtenzeService;
  private final ValoriEcosostenibilitaService valoriEcosostenibilitaService;
  private final ArchiviazioneService archiviazioneService;

  @PostMapping
  private ResponseEntity<Object> creaRecensione(
      @AuthenticationPrincipal Utente utente,
      @RequestParam("idAttivita") final Long idAttivita,
      @RequestParam("valutazioneStelleEsperienza") final int valutazioneStelleEsperienza,
      @RequestParam("descrizione") final String descrizione,
      @RequestParam(value = "immagine", required = false) final MultipartFile immagine,
      @RequestParam("idValori") final Long idValori
  ) {
    try {
      Attivita attivita = attivitaService.findById(idAttivita);
      Utente visitatore = gestioneUtenzeService.findById(utente.getId());
      Recensione recensione = new Recensione();
      recensione.setVisitatore(visitatore);
      recensione.setAttivita(attivita);
      recensione.setValutazioneStelleEsperienza(valutazioneStelleEsperienza);
      recensione.setDescrizione(descrizione);
      if (immagine != null && !immagine.isEmpty()) {
        String media = UUID.randomUUID().toString();
        recensione.setMedia(media);
        archiviazioneService.store(media, immagine);
      }
      ValoriEcosostenibilita valori = valoriEcosostenibilitaService.findById(idValori);
      recensione.setValoriEcosostenibilita(valori);
      recensione = recensioneService.saveRecensione(recensione);
      return ResponseGenerator.generateResponse(HttpStatus.OK, recensione);
    } catch (Exception e) {
      return ResponseGenerator.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

  @GetMapping("{id}")
  private ResponseEntity<Object> visualizzaRecensione(
      @PathVariable("id") final Long id
  ) {
    try {
      return ResponseGenerator.generateResponse(HttpStatus.OK,
          recensioneService.findById(id));
    } catch (Exception e) {
      return ResponseGenerator.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

  @GetMapping("perAttivita/{idAttivita}")
  private ResponseEntity<Object> visualizzaRecensioniPerAttivita(
      @PathVariable("idAttivita") final Long idAttivita
  ) {
    try {
      Attivita attivita = attivitaService.findById(idAttivita);
      return ResponseGenerator.generateResponse(HttpStatus.OK,
          recensioneService.getRecensioniByAttivita(attivita));
    } catch (Exception e) {
      return ResponseGenerator.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }


  @DeleteMapping("{id}")
  private ResponseEntity<Object> cancellaRecensione(
      @AuthenticationPrincipal Utente utente,
      @PathVariable("id") final Long id
  ) {
    try {
      Recensione recensione = recensioneService.findById(id);
      if (!recensione.getVisitatore().getId().equals(utente.getId())
          && utente.getRuolo() != RuoloUtente.AMMINISTRATORE) {
        return ResponseGenerator.generateResponse(HttpStatus.FORBIDDEN,
            "Impossibile eliminare la recensione.");
      }
      return ResponseGenerator.generateResponse(HttpStatus.OK,
          recensioneService.deleteRecensione(recensione));
    } catch (Exception e) {
      return ResponseGenerator.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }

  }
}
