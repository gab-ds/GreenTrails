package it.greentrails.backend.gestionesegnalazioni.service;

import it.greentrails.backend.entities.Segnalazione;
import it.greentrails.backend.enums.StatoSegnalazione;
import it.greentrails.backend.gestionesegnalazioni.repository.SegnalazioniRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
/*@ nullable_by_default @*/
public class SegnalazioniServiceImpl implements SegnalazioniService {

  /*@ spec_public @*/
  private final SegnalazioniRepository repository;

  //@ public invariant repository != null;

  /*@
    @ also
    @ ensures \result != null;
    @*/
  @Override
  public Segnalazione saveSegnalazione(/*@ nullable @*/ Segnalazione segnalazione) throws Exception {
    if (segnalazione == null) {
      throw new Exception("La segnalazione è vuota.");
    }
    return repository.save(segnalazione);
  }

  @Override
  public List<Segnalazione> getAllSegnalazioniByTipo(boolean isForRecensione) {
    return repository.findByTipo(isForRecensione);
  }

  /*@
    @ also
    @ ensures \result != null;
    @*/
  @Override
  public Segnalazione findById(/*@ nullable @*/ Long id) throws Exception {
    if (id == null || id < 0) {
      throw new Exception("L'id non è valido.");
    }
    Optional<Segnalazione> segnalazione = repository.findById(id);
    if (segnalazione.isEmpty()) {
      throw new Exception("La segnalazione non è stata trovata.");
    }
    return segnalazione.get();
  }

  @Override
  public List<Segnalazione> getSegnalazioniByStato(/*@ nullable @*/ StatoSegnalazione stato) {
    return repository.findByStato(stato);
  }
}
