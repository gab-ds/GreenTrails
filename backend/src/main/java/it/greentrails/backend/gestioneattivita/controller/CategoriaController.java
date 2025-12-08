package it.greentrails.backend.gestioneattivita.controller;


import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Categoria;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.gestioneattivita.service.AttivitaService;
import it.greentrails.backend.gestioneattivita.service.CategoriaService;
import it.greentrails.backend.utils.service.ResponseGenerator;
import java.util.Set;
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
@RequestMapping(path = "api/categorie")
@RequiredArgsConstructor
public class CategoriaController {

  private final CategoriaService categoriaService;
  private final AttivitaService attivitaService;

  @PostMapping("{id}")
  private ResponseEntity<Object> aggiungiCategoria(
      @AuthenticationPrincipal Utente utente,
      @PathVariable("id") final Long id,
      @RequestParam("idAttivita") final Long idAttivita
  ) {
    try {
      Categoria categoria = categoriaService.findById(id);
      Attivita attivita = attivitaService.findById(idAttivita);
      if (!attivita.getGestore().getId().equals(utente.getId())) {
        return ResponseGenerator.generateResponse(HttpStatus.NOT_FOUND, "Attività non trovata");
      }
      Set<Categoria> categorieAttivita = attivita.getCategorie();
      categorieAttivita.add(categoria);
      attivita.setCategorie(categorieAttivita);
      attivita = attivitaService.saveAttivita(attivita);
      return ResponseGenerator.generateResponse(HttpStatus.OK, attivita);
    } catch (Exception e) {
      return ResponseGenerator.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

  @GetMapping("{id}")
  private ResponseEntity<Object> visualizzaCategoria(
          @PathVariable("id") final Long id
  ) {
    try {
      return ResponseGenerator.generateResponse(HttpStatus.OK, categoriaService.findById(id));
    } catch (Exception e) {
      return ResponseGenerator.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

  @DeleteMapping("{id}")
  private ResponseEntity<Object> rimuoviCategoria(
      @AuthenticationPrincipal Utente utente,
      @PathVariable("id") final Long id,
      @RequestParam("idAttivita") final Long idAttivita
  ) {
    try {
      Categoria categoria = categoriaService.findById(id);
      Attivita attivita = attivitaService.findById(idAttivita);
      if (!attivita.getGestore().getId().equals(utente.getId())) {
        return ResponseGenerator.generateResponse(HttpStatus.NOT_FOUND, "Attività non trovata");
      }
      Set<Categoria> categorieAttivita = attivita.getCategorie();
      categorieAttivita.remove(categoria);
      attivita.setCategorie(categorieAttivita);
      attivita = attivitaService.saveAttivita(attivita);
      return ResponseGenerator.generateResponse(HttpStatus.OK, attivita);
    } catch (Exception e) {
      return ResponseGenerator.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

}
