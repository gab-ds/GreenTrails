package it.greentrails.backend.gestionericerca.service;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Categoria;
import it.greentrails.backend.gestioneattivita.repository.AttivitaRepository;
import java.security.InvalidParameterException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RicercaServiceImpl implements RicercaService {

  private final AttivitaRepository repository;

  @Override
  public List<Attivita> findAttivita(String query) throws InvalidParameterException {
    if (query == null || query.isBlank()) {
      throw new InvalidParameterException("La query è vuota.");
    }
    return repository.findByQuery(query);
  }

  @Override
  public List<Attivita> findAttivitaByCategorie(List<Categoria> categorie)
      throws InvalidParameterException {
    if (categorie == null || categorie.isEmpty()) {
      throw new InvalidParameterException("La lista delle categorie è vuota");
    }
    return repository.findByCategorie(categorie, categorie.size());
  }

  @Override
  public List<Attivita> findAttivitaByPosizione(Point coordinate, double raggio)
      throws InvalidParameterException {
    if (coordinate == null) {
      throw new InvalidParameterException("Le coordinate sono vuote.");
    }
    if (raggio < 0) {
      throw new InvalidParameterException("Il raggio non è valido.");
    }
    return repository.findByPosizione(coordinate.getY(), coordinate.getX(), raggio);
  }
}
