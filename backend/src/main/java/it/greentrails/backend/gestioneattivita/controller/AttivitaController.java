package it.greentrails.backend.gestioneattivita.controller;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.enums.CategorieAlloggio;
import it.greentrails.backend.enums.CategorieAttivitaTuristica;
import it.greentrails.backend.gestioneattivita.service.AttivitaService;
import it.greentrails.backend.gestioneattivita.service.ValoriEcosostenibilitaService;
import it.greentrails.backend.gestioneupload.service.ArchiviazioneService;
import it.greentrails.backend.gestioneutenze.service.GestioneUtenzeService;
import it.greentrails.backend.utils.service.ResponseGenerator;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
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
@RequestMapping(path = "api/attivita")
@RequiredArgsConstructor
public class AttivitaController {

  private final AttivitaService attivitaService;
  private final GestioneUtenzeService gestioneUtenzeService;
  private final ValoriEcosostenibilitaService valoriEcosostenibilitaService;
  private final ArchiviazioneService archiviazioneService;

  @PostMapping
  private ResponseEntity<Object> creaAttivita(
      @AuthenticationPrincipal Utente utente,
      @RequestParam("alloggio") final boolean isAlloggio,
      @RequestParam("nome") final String nome,
      @RequestParam("indirizzo") final String indirizzo,
      @RequestParam("cap") final String cap,
      @RequestParam("citta") final String citta,
      @RequestParam("provincia") final String provincia,
      @RequestParam("latitudine") final Double latitudine,
      @RequestParam("longitudine") final Double longitudine,
      @RequestParam("descrizioneBreve") final String descrizioneBreve,
      @RequestParam("descrizioneLunga") final String descrizioneLunga,
      @RequestParam("valori") final long idValori,
      @RequestParam("immagine") final MultipartFile immagine,
      @RequestParam(value = "prezzo", required = false) final Double prezzo,
      @RequestParam(value = "disponibilita", required = false) final Integer disponibilita,
      @RequestParam(value = "categoriaAlloggio", required = false) final Integer categoriaAlloggio,
      @RequestParam(value = "categoriaAttivitaTuristica", required = false)
      final Integer categoriaAttivitaTuristica
  ) {
    try {
      Utente gestore = gestioneUtenzeService.findById(utente.getId());
      Attivita attivita = new Attivita();
      attivita.setAlloggio(isAlloggio);
      attivita.setGestore(gestore);
      attivita.setNome(nome);
      attivita.setIndirizzo(indirizzo);
      attivita.setCap(cap);
      attivita.setCitta(citta);
      attivita.setProvincia(provincia);
      attivita.setCoordinate(new Point(latitudine, longitudine));
      attivita.setDescrizioneBreve(descrizioneBreve);
      attivita.setDescrizioneLunga(descrizioneLunga);
      attivita.setValoriEcosostenibilita(valoriEcosostenibilitaService.findById(idValori));
      attivita.setPrezzo(prezzo);
      String media = UUID.randomUUID().toString();
      attivita.setMedia(media);
      archiviazioneService.store(media, immagine);
      if (isAlloggio) {
        if (categoriaAlloggio == null) {
          return ResponseGenerator.generateResponse(HttpStatus.BAD_REQUEST,
              "Categoria per alloggio non presente.");
        }
        attivita.setCategoriaAlloggio(CategorieAlloggio.values()[categoriaAlloggio]);

      } else {
        if (disponibilita == null) {
          return ResponseGenerator.generateResponse(HttpStatus.BAD_REQUEST,
              "Disponibilità per attività turistica non presente.");
        }
        attivita.setDisponibilita(disponibilita);
        if (prezzo == null) {
          return ResponseGenerator.generateResponse(HttpStatus.BAD_REQUEST,
              "Prezzo per attività turistica non presente.");
        }
        attivita.setPrezzo(prezzo);
        if (categoriaAttivitaTuristica == null) {
          return ResponseGenerator.generateResponse(HttpStatus.BAD_REQUEST,
              "Categoria per attività turistica non presente.");
        }
        attivita.setCategoriaAttivitaTuristica(
            CategorieAttivitaTuristica.values()[categoriaAttivitaTuristica]);
      }
      attivita = attivitaService.saveAttivita(attivita);
      return ResponseGenerator.generateResponse(HttpStatus.OK, attivita);
    } catch (Exception e) {
      return ResponseGenerator.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

  @GetMapping("{id}")
  private ResponseEntity<Object> visualizzaAttivita(
      @PathVariable("id") final Long id
  ) {
    try {
      Attivita attivita = attivitaService.findById(id);
      return ResponseGenerator.generateResponse(HttpStatus.OK, attivita);
    } catch (Exception e) {
      return ResponseGenerator.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

  @GetMapping("perGestore")
  private ResponseEntity<Object> visualizzaAttivitaPerGestore(
      @AuthenticationPrincipal Utente utente
  ) {
    try {
      return ResponseGenerator.generateResponse(HttpStatus.OK,
          attivitaService.findAllAttivitaByGestore(utente.getId()));
    } catch (Exception e) {
      return ResponseGenerator.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

  @GetMapping("perPrezzo")
  private ResponseEntity<Object> visualizzaAttivitaPerPrezzo(
      @RequestParam(value = "limite", required = false) Integer limite
  ) {
    if (limite == null) {
      limite = 10;
    }
    return ResponseGenerator.generateResponse(HttpStatus.OK,
        attivitaService.getAttivitaTuristicheEconomiche(limite));
  }

  @PostMapping("{id}")
  private ResponseEntity<Object> modificaAttivita(
      @AuthenticationPrincipal Utente utente,
      @PathVariable("id") final long idAttivita,
      @RequestParam("nome") final String nome,
      @RequestParam("indirizzo") final String indirizzo,
      @RequestParam("cap") final String cap,
      @RequestParam("citta") final String citta,
      @RequestParam("provincia") final String provincia,
      @RequestParam("latitudine") final Double latitudine,
      @RequestParam("longitudine") final Double longitudine,
      @RequestParam("descrizioneBreve") final String descrizioneBreve,
      @RequestParam("descrizioneLunga") final String descrizioneLunga,
      @RequestParam("valori") final long idValori,
      @RequestParam(value = "prezzo", required = false) final Double prezzo,
      @RequestParam(value = "disponibilita", required = false) final Integer disponibilita,
      @RequestParam(value = "categoriaAlloggio", required = false) final Integer categoriaAlloggio,
      @RequestParam(value = "categoriaAttivitaTuristica", required = false)
      final Integer categoriaAttivitaTuristica
  ) {
    try {
      Utente gestore = gestioneUtenzeService.findById(utente.getId());
      Attivita attivita = attivitaService.findById(idAttivita);
      if (!gestore.getId().equals(attivita.getGestore().getId()) || attivita.isEliminata()) {
        return ResponseGenerator.generateResponse(HttpStatus.BAD_REQUEST,
            "Attività non trovata.");
      }
      attivita.setGestore(gestore);
      attivita.setNome(nome);
      attivita.setIndirizzo(indirizzo);
      attivita.setCap(cap);
      attivita.setCitta(citta);
      attivita.setProvincia(provincia);
      attivita.setCoordinate(new Point(latitudine, longitudine));
      attivita.setDescrizioneBreve(descrizioneBreve);
      attivita.setDescrizioneLunga(descrizioneLunga);
      attivita.setValoriEcosostenibilita(valoriEcosostenibilitaService.findById(idValori));
      if (attivita.isAlloggio()) {
        if (categoriaAlloggio == null) {
          return ResponseGenerator.generateResponse(HttpStatus.BAD_REQUEST,
              "Categoria per alloggio non presente.");
        }
        attivita.setCategoriaAlloggio(CategorieAlloggio.values()[categoriaAlloggio]);

      } else {
        if (disponibilita == null) {
          return ResponseGenerator.generateResponse(HttpStatus.BAD_REQUEST,
              "Disponibilità per attività turistica non presente.");
        }
        attivita.setDisponibilita(disponibilita);
        if (prezzo == null) {
          return ResponseGenerator.generateResponse(HttpStatus.BAD_REQUEST,
              "Prezzo per attività turistica non presente.");
        }
        attivita.setPrezzo(prezzo);
        if (categoriaAttivitaTuristica == null) {
          return ResponseGenerator.generateResponse(HttpStatus.BAD_REQUEST,
              "Categoria per attività turistica non presente.");
        }
        attivita.setCategoriaAttivitaTuristica(
            CategorieAttivitaTuristica.values()[categoriaAttivitaTuristica]);
      }
      attivita = attivitaService.saveAttivita(attivita);
      return ResponseGenerator.generateResponse(HttpStatus.OK, attivita);
    } catch (Exception e) {
      return ResponseGenerator.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

  @DeleteMapping("{id}")
  private ResponseEntity<Object> cancellaAttivita(
      @AuthenticationPrincipal Utente utente,
      @PathVariable("id") final Long id
  ) {
    try {
      Attivita attivita = attivitaService.findById(id);
      if (!attivita.getGestore().getId().equals(utente.getId())) {
        return ResponseGenerator.generateResponse(HttpStatus.NOT_FOUND, "Attività non trovata");
      }
      return ResponseGenerator.generateResponse(HttpStatus.OK,
          attivitaService.deleteAttivita(attivita));
    } catch (Exception e) {
      return ResponseGenerator.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

  @GetMapping("alloggi")
  private ResponseEntity<Object> getAlloggi(
      @RequestParam(value = "limite", required = false) Integer limite
  ) {
    if (limite == null) {
      limite = 5;
    }
    return ResponseGenerator.generateResponse(HttpStatus.OK,
        attivitaService.getAlloggi(limite));
  }

  @GetMapping("attivitaTuristiche")
  private ResponseEntity<Object> getAttivitaTuristiche(
      @RequestParam(value = "limite", required = false) Integer limite
  ) {
    if (limite == null) {
      limite = 5;
    }
    return ResponseGenerator.generateResponse(HttpStatus.OK,
        attivitaService.getAttivitaTuristiche(limite));
  }

  @GetMapping("all")
  private ResponseEntity<Object> findAll() {
    return ResponseGenerator.generateResponse(HttpStatus.OK,
        attivitaService.findAll());
  }

}
