package it.greentrails.backend.gestioneattivita.controller;


import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.entities.ValoriEcosostenibilita;
import it.greentrails.backend.enums.RuoloUtente;
import it.greentrails.backend.gestioneattivita.service.AttivitaService;
import it.greentrails.backend.gestioneattivita.service.ValoriEcosostenibilitaService;
import it.greentrails.backend.utils.service.ResponseGenerator;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/valori")
@RequiredArgsConstructor
public class ValoriEcosostenibilitaController {

  private final ValoriEcosostenibilitaService valoriEcosostenibilitaService;
  private final AttivitaService attivitaService;

  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  @PostMapping
  private ResponseEntity<Object> creaValoriEcosostenibilita(
      @RequestParam(value = "politicheAntispreco", required = false)
      final Boolean politicheAntispreco,
      @RequestParam(value = "prodottiLocali", required = false)
      final Boolean prodottiLocali,
      @RequestParam(value = "energiaVerde", required = false)
      final Boolean energiaVerde,
      @RequestParam(value = "raccoltaDifferenziata", required = false)
      final Boolean raccoltaDifferenziata,
      @RequestParam(value = "limiteEmissioneCO2", required = false)
      final Boolean limiteEmissioneCO2,
      @RequestParam(value = "contattoConNatura", required = false)
      final Boolean contattoConNatura
  ) {
    try {
      ValoriEcosostenibilita valori = new ValoriEcosostenibilita();
      valori.setPoliticheAntispreco(politicheAntispreco);
      valori.setProdottiLocali(prodottiLocali);
      valori.setEnergiaVerde(energiaVerde);
      valori.setRaccoltaDifferenziata(raccoltaDifferenziata);
      valori.setLimiteEmissioneCO2(limiteEmissioneCO2);
      valori.setContattoConNatura(contattoConNatura);
      valori = valoriEcosostenibilitaService.saveValori(valori);
      return ResponseGenerator.generateResponse(HttpStatus.OK, valori);
    } catch (Exception e) {
      return ResponseGenerator.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

  @GetMapping("{id}")
  private ResponseEntity<Object> visualizzaValoriEcosostenibilita(
      @PathVariable("id") final Long id
  ) {
    try {
      return ResponseGenerator.generateResponse(HttpStatus.OK,
          valoriEcosostenibilitaService.findById(id));
    } catch (Exception e) {
      return ResponseGenerator.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  @PostMapping("{id}")
  private ResponseEntity<Object> modificaValoriEcosostenibilita(
      @AuthenticationPrincipal Utente utente,
      @PathVariable("id") final Long id,
      @RequestParam(value = "politicheAntispreco", required = false)
      final Boolean politicheAntispreco,
      @RequestParam(value = "prodottiLocali", required = false)
      final Boolean prodottiLocali,
      @RequestParam(value = "energiaVerde", required = false)
      final Boolean energiaVerde,
      @RequestParam(value = "raccoltaDifferenziata", required = false)
      final Boolean raccoltaDifferenziata,
      @RequestParam(value = "limiteEmissioneCO2", required = false)
      final Boolean limiteEmissioneCO2,
      @RequestParam(value = "contattoConNatura", required = false)
      final Boolean contattoConNatura
  ) {

    try {
      ValoriEcosostenibilita valori = valoriEcosostenibilitaService.findById(id);
      Optional<Attivita> risultato = attivitaService.findByValori(valori);
      if (risultato.isEmpty() || !risultato.get().getGestore().getId().equals(utente.getId())
          && utente.getRuolo() != RuoloUtente.AMMINISTRATORE) {
        return ResponseGenerator.generateResponse(HttpStatus.NOT_FOUND,
            "Valori da modificare non trovati.");
      }
      valori.setPoliticheAntispreco(politicheAntispreco);
      valori.setProdottiLocali(prodottiLocali);
      valori.setEnergiaVerde(energiaVerde);
      valori.setRaccoltaDifferenziata(raccoltaDifferenziata);
      valori.setLimiteEmissioneCO2(limiteEmissioneCO2);
      valori.setContattoConNatura(contattoConNatura);
      valori = valoriEcosostenibilitaService.saveValori(valori);
      return ResponseGenerator.generateResponse(HttpStatus.OK, valori);
    } catch (Exception e) {
      return ResponseGenerator.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }

  }

}
