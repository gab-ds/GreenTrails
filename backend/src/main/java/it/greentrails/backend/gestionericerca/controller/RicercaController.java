package it.greentrails.backend.gestionericerca.controller;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.gestioneattivita.service.CategoriaService;
import it.greentrails.backend.gestionericerca.service.RicercaService;
import it.greentrails.backend.utils.service.ResponseGenerator;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("api/ricerca")
@RequiredArgsConstructor
public class RicercaController {

  private final RicercaService ricercaService;
  private final CategoriaService categoriaService;

  @PostMapping
  private ResponseEntity<Object> cerca(
      @RequestParam(value = "query") final String query,
      @RequestParam(value = "idCategorie", required = false) final Long[] idCategorie,
      @RequestParam(value = "latitudine", required = false) final Long latitudine,
      @RequestParam(value = "longitudine", required = false) final Long longitudine,
      @RequestParam(value = "raggio", required = false) final Double raggio
  ) {
    List<Attivita> risultati = ricercaService.findAttivita(query);
    if (idCategorie != null && idCategorie.length > 0) {
      risultati = ricercaService.findAttivitaByCategorie(
              Arrays.stream(idCategorie).map(idCategoria -> {
                try {
                  return categoriaService.findById(idCategoria);
                } catch (Exception e) {
                  throw new RuntimeException(e);
                }
              }).collect(Collectors.toList()))
          .stream()
          .filter(risultati::contains)
          .collect(Collectors.toList());
    }
    if (latitudine != null && longitudine != null && raggio != null) {
      Point coordinate = new Point(latitudine, longitudine);
      risultati = ricercaService.findAttivitaByPosizione(coordinate, raggio)
          .stream()
          .filter(risultati::contains)
          .collect(Collectors.toList());
    }
    return ResponseGenerator.generateResponse(HttpStatus.OK, risultati);
  }

  @PostMapping("perPosizione")
  private ResponseEntity<Object> cercaSenzaQuery(
      @RequestParam(value = "latitudine") final Double latitudine,
      @RequestParam(value = "longitudine") final Double longitudine,
      @RequestParam(value = "raggio") final Double raggio,
      @RequestParam(value = "idCategorie", required = false) final Long[] idCategorie
  ) {
    Point coordinate = new Point(latitudine, longitudine);
    List<Attivita> risultati = ricercaService.findAttivitaByPosizione(coordinate, raggio);
    if (idCategorie != null && idCategorie.length > 0) {
      risultati = ricercaService.findAttivitaByCategorie(
              Arrays.stream(idCategorie).map(idCategoria -> {
                try {
                  return categoriaService.findById(idCategoria);
                } catch (Exception e) {
                  throw new RuntimeException(e);
                }
              }).collect(Collectors.toList()))
          .stream()
          .filter(risultati::contains)
          .collect(Collectors.toList());
    }
    return ResponseGenerator.generateResponse(HttpStatus.OK, risultati);
  }

}
