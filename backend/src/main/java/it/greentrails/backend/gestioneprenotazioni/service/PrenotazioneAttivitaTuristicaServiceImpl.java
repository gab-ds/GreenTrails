package it.greentrails.backend.gestioneprenotazioni.service;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Itinerario;
import it.greentrails.backend.entities.PrenotazioneAttivitaTuristica;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.enums.RuoloUtente;
import it.greentrails.backend.enums.StatoPrenotazione;
import it.greentrails.backend.gestioneprenotazioni.repository.PrenotazioneAttivitaTuristicaRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
/*@ nullable_by_default @*/
public class PrenotazioneAttivitaTuristicaServiceImpl implements
    PrenotazioneAttivitaTuristicaService {

  /*@ spec_public @*/
  private final PrenotazioneAttivitaTuristicaRepository repository;

  //@ public invariant repository != null;

  /*@
    @ also
    @ ensures \result != null;
    @*/
  @Override
  public PrenotazioneAttivitaTuristica savePrenotazioneAttivitaTuristica(/*@ nullable @*/ Attivita attivita,
      /*@ nullable @*/ PrenotazioneAttivitaTuristica prenotazioneAttivitaTuristica) throws Exception {
    if (prenotazioneAttivitaTuristica == null) {
      throw new Exception("La prenotazione dell'attività è vuota.");
    }
    if (attivita == null) {
      throw new Exception("L'attività è vuota.");
    }
    if (attivita.isAlloggio()) {
      throw new Exception("L'attività non può essere un alloggio.");
    }
    prenotazioneAttivitaTuristica.setAttivitaTuristica(attivita);
    return repository.save(prenotazioneAttivitaTuristica);
  }

  @Override
  public boolean deletePrenotazioneAttivitaTuristica(
      /*@ nullable @*/ PrenotazioneAttivitaTuristica prenotazioneAttivitaTuristica) throws Exception {
    if (prenotazioneAttivitaTuristica == null) {
      throw new Exception("La prenotazione dell'attività è vuota.");
    }
    repository.delete(prenotazioneAttivitaTuristica);
    repository.flush();
    return repository.findById(prenotazioneAttivitaTuristica.getId()).isEmpty();
  }

  @Override
  public List<PrenotazioneAttivitaTuristica> getAllPrenotazioniAttivitaTuristica() {
    return repository.findAll();
  }

  /*@
    @ also
    @ ensures \result != null;
    @*/
  @Override
  public List<PrenotazioneAttivitaTuristica> getPrenotazioniAttivitaTuristicaByStato(
      /*@ nullable @*/ StatoPrenotazione stato) throws Exception {
    if (stato == null) {
      throw new Exception("Lo stato della prenotazione dell'attività è inesistente.");
    }
    List<PrenotazioneAttivitaTuristica> risultato = new ArrayList<>();
    getAllPrenotazioniAttivitaTuristica().forEach(p -> {
      if (p.getStato().equals(stato)) {
        risultato.add(p);
      }
    });
    return risultato;
  }

  /*@
    @ also
    @ ensures \result != null;
    @*/
  @Override
  public PrenotazioneAttivitaTuristica findById(/*@ nullable @*/ Long id) throws Exception {
    if (id == null || id < 0) {
      throw new Exception("L'id non è valido.");
    }
    Optional<PrenotazioneAttivitaTuristica> prenotazioneAttivitaTuristica = repository.findById(id);
    if (prenotazioneAttivitaTuristica.isEmpty()) {
      throw new Exception("La prenotazione non è stata trovata.");
    }
    return prenotazioneAttivitaTuristica.get();
  }

  /*@
    @ also
    @ ensures \result != null;
    @*/
  @Override
  public List<PrenotazioneAttivitaTuristica> getPrenotazioniByAttivitaTuristica(/*@ nullable @*/ Attivita attivita)
      throws Exception {
    if (attivita == null) {
      throw new Exception("L'attività è vuota.");
    }
    return repository.findByAttivitaTuristica(attivita.getId(), Pageable.unpaged()).toList();
  }

  /*@
    @ also
    @ ensures \result != null;
    @*/
  @Override
  public List<PrenotazioneAttivitaTuristica> getPrenotazioniByVisitatore(/*@ nullable @*/ Utente visitatore)
      throws Exception {
    if (visitatore == null) {
      throw new Exception("L'utente è vuoto.");
    }
    if (visitatore.getRuolo() != RuoloUtente.VISITATORE) {
      throw new Exception("L'utente non è un visitatore.");
    }
    return repository.findByVisitatore(visitatore.getId(), Pageable.unpaged()).toList();
  }

  /*@
    @ also
    @ ensures \result != null;
    @*/
  @Override
  public List<PrenotazioneAttivitaTuristica> getPrenotazioniByItinerario(/*@ nullable @*/ Itinerario itinerario)
      throws Exception {
    if (itinerario == null) {
      throw new Exception("L'itinerario è vuoto.");
    }
    return repository.findByItinerario(itinerario.getId(), Pageable.unpaged()).toList();
  }

  @Override
  public int controllaDisponibilitaAttivitaTuristica(/*@ nullable @*/ Attivita attivita, /*@ nullable @*/ Date dataInizio)
      throws Exception {
    if (attivita == null) {
      throw new Exception("L'attività è vuota.");
    }
    if (attivita.isAlloggio()) {
      throw new Exception("L'attività non può essere un alloggio.");
    }
    return attivita.getDisponibilita() - repository.getPostiOccupatiIn(
        attivita.getId(), dataInizio);
  }

}
