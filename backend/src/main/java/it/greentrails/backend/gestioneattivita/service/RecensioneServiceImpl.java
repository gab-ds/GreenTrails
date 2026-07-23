package it.greentrails.backend.gestioneattivita.service;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Recensione;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.gestioneattivita.repository.RecensioneRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
/*@ nullable_by_default @*/
public class RecensioneServiceImpl implements RecensioneService {

  /*@ spec_public non_null @*/
  private final RecensioneRepository repository;

  // repository is guaranteed non-null by Spring constructor injection

  /*@
    @ also
    @ ensures \result != null;
    @*/
  @Override
  public Recensione saveRecensione(/*@ nullable @*/ Recensione recensione) throws Exception {
    if (recensione == null) {
      throw new Exception("La recensione è vuota.");
    }
    return repository.save(recensione);
  }

  /*@
    @ also
    @ ensures \result != null;
    @*/
  @Override
  public Recensione findById(/*@ nullable @*/ Long id) throws Exception {
    if (id == null || id < 0) {
      throw new Exception("L'id non è valido.");
    }
    Optional<Recensione> recensione = repository.findById(id);
    if (recensione.isEmpty()) {
      throw new Exception("La recensione non è stata trovata.");
    }
    return recensione.get();
  }

  /*@
    @ requires recensione != null;
    @*/
  @Override
  public boolean deleteRecensione(Recensione recensione) throws Exception {
    if (recensione == null) {
      throw new Exception("La recensione è vuota.");
    }
    repository.delete(recensione);
    repository.flush();
    return repository.findById(recensione.getId()).isEmpty();
  }

  /*@
    @ also
    @ requires attivita != null;
    @ ensures \result != null;
    @*/
  @Override
  public List<Recensione> getRecensioniByAttivita(Attivita attivita) throws Exception {
    if (attivita == null) {
      throw new Exception("L'attività è vuota.");
    }
    return repository.findByAttivita(attivita.getId(), Pageable.unpaged()).toList();
  }

  /*@
    @ also
    @ requires utente != null;
    @ ensures \result != null;
    @*/
  @Override
  public List<Recensione> getAllRecensioniByVisitatore(Utente utente) throws Exception {
    if (utente == null) {
      throw new Exception("L'utente è vuoto.");
    }
    return repository.findByVisitatore(utente.getId());
  }
}
