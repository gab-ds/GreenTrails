package it.greentrails.backend.gestionericerca.service;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Categoria;
import java.security.InvalidParameterException;
import java.util.List;
import org.springframework.data.geo.Point;

public interface RicercaService {

  List<Attivita> findAttivita(String query) throws InvalidParameterException;

  List<Attivita> findAttivitaByCategorie(List<Categoria> categorie)
      throws InvalidParameterException;

  List<Attivita> findAttivitaByPosizione(Point coordinate, double raggio)
      throws InvalidParameterException;

}
