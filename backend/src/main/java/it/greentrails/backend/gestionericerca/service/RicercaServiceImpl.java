package it.greentrails.backend.gestionericerca.service;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Categoria;
import it.greentrails.backend.gestioneattivita.repository.AttivitaRepository;
import it.greentrails.backend.utils.DistanceCalculator;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
    return categorie.stream().map(c -> repository.findByCategoria(c.getId())).reduce(
        (l1, l2) -> l1.stream()
            .filter(l2::contains)
            .collect(Collectors.toList())
    ).orElse(new ArrayList<>());
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
    return repository
        .findAll()
        .stream()
        .filter(a -> DistanceCalculator.distance(coordinate, a.getCoordinate()) <= raggio)
        .toList();
  }
}
