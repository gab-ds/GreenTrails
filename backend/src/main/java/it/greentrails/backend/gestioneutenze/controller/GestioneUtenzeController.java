package it.greentrails.backend.gestioneutenze.controller;


import it.greentrails.backend.entities.Preferenze;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.enums.PreferenzeAlimentari;
import it.greentrails.backend.enums.PreferenzeAlloggio;
import it.greentrails.backend.enums.PreferenzeAttivita;
import it.greentrails.backend.enums.PreferenzeBudget;
import it.greentrails.backend.enums.PreferenzeStagione;
import it.greentrails.backend.enums.PreferenzeViaggio;
import it.greentrails.backend.enums.RuoloUtente;
import it.greentrails.backend.gestioneutenze.service.GestioneUtenzeService;
import it.greentrails.backend.utils.service.ResponseGenerator;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/utenti")
@RequiredArgsConstructor
public class GestioneUtenzeController {

  private final GestioneUtenzeService service;
  private final PasswordEncoder passwordEncoder;

  @PutMapping
  private ResponseEntity<Object> registrazione(
      @RequestParam final boolean isGestore,
      @RequestBody final Utente utente
  ) {
    Utente u;
    if (service.findByEmail(utente.getEmail()).isPresent()) {
      return ResponseGenerator.generateResponse(HttpStatus.BAD_REQUEST, "L'utente esiste già.");
    }
    RuoloUtente ruoloUtente = isGestore ? RuoloUtente.GESTORE_ATTIVITA : RuoloUtente.VISITATORE;
    utente.setPassword(passwordEncoder.encode(utente.getPassword()));
    utente.setRuolo(ruoloUtente);
    try {
      u = service.saveUtente(utente);
      return ResponseGenerator.generateResponse(HttpStatus.OK, u);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @GetMapping("preferenze")
  private ResponseEntity<Object> visualizzaPreferenze(
      @AuthenticationPrincipal Utente utente
  ) {
    try {
      return ResponseGenerator.generateResponse(HttpStatus.OK,
          service.getPreferenzeById(utente.getId()));
    } catch (Exception e) {
      return ResponseGenerator.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

  @PostMapping("questionario")
  private ResponseEntity<Object> compilaQuestionario(
      @AuthenticationPrincipal Utente utente,
      @RequestParam("viaggioPreferito") final PreferenzeViaggio viaggioPreferito,
      @RequestParam("alloggioPreferito") final PreferenzeAlloggio alloggioPreferito,
      @RequestParam("attivitaPreferita") final PreferenzeAttivita attivitaPreferita,
      @RequestParam("preferenzaAlimentare") final PreferenzeAlimentari preferenzaAlimentare,
      @RequestParam("animaleDomestico") final boolean animaleDomestico,
      @RequestParam("budgetPreferito") final PreferenzeBudget budgetPreferito,
      @RequestParam("souvenir") final boolean souvenir,
      @RequestParam("stagioniPreferite") final PreferenzeStagione stagioniPreferite
  ) {
    Preferenze preferenze;
    try {
      preferenze = service.getPreferenzeById(utente.getId());
    } catch (Exception e) {
      preferenze = new Preferenze();
      preferenze.setId(utente.getId());
      preferenze.setVisitatore(utente);
    }
    try {
      preferenze.setViaggioPreferito(viaggioPreferito);
      preferenze.setAlloggioPreferito(alloggioPreferito);
      preferenze.setAttivitaPreferita(attivitaPreferita);
      preferenze.setPreferenzaAlimentare(preferenzaAlimentare);
      preferenze.setAnimaleDomestico(animaleDomestico);
      preferenze.setBudgetPreferito(budgetPreferito);
      preferenze.setSouvenir(souvenir);
      preferenze.setStagioniPreferite(stagioniPreferite);
      return ResponseGenerator.generateResponse(HttpStatus.OK, service.savePreferenze(preferenze));
    } catch (Exception e) {
      return ResponseGenerator.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

  @GetMapping
  private ResponseEntity<Object> visualizzaInfo(
      @AuthenticationPrincipal final Utente utente
  ) {
    Optional<Utente> u = service.findByEmail(utente.getEmail());
    if (u.isEmpty()) {
      return ResponseGenerator.generateResponse(HttpStatus.BAD_REQUEST,
          "L'utente non esiste.");
    }
    return ResponseGenerator.generateResponse(HttpStatus.OK, u);
  }

}
