package it.greentrails.backend.gestioneprenotazioni.service;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Itinerario;
import it.greentrails.backend.entities.PrenotazioneAttivitaTuristica;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.enums.RuoloUtente;
import it.greentrails.backend.enums.StatoPrenotazione;
import it.greentrails.backend.gestioneprenotazioni.repository.PrenotazioneAttivitaTuristicaRepository;
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

  /*@ spec_public non_null @*/
  private final PrenotazioneAttivitaTuristicaRepository repository;

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

  /*@ requires prenotazioneAttivitaTuristica != null; @*/
  @Override
  public boolean deletePrenotazioneAttivitaTuristica(
      PrenotazioneAttivitaTuristica prenotazioneAttivitaTuristica) throws Exception {
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
    @ requires stato != null;
    @ ensures \result != null;
    @*/
  @Override
  public List<PrenotazioneAttivitaTuristica> getPrenotazioniAttivitaTuristicaByStato(
      StatoPrenotazione stato) throws Exception {
    if (stato == null) {
      throw new Exception("Lo stato della prenotazione dell'attività è inesistente.");
    }
    return repository.findByStato(stato);
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
  /*@ requires attivita != null; @*/
  public List<PrenotazioneAttivitaTuristica> getPrenotazioniByAttivitaTuristica(Attivita attivita)
      throws Exception {
    if (attivita == null) {
      throw new Exception("L'attività è vuota.");
    }
    return repository.findByAttivitaTuristica(attivita.getId(), Pageable.unpaged()).toList();
  }

  /*@
    @ also
    @ requires visitatore != null;
    @ ensures \result != null;
    @*/
  @Override
  public List<PrenotazioneAttivitaTuristica> getPrenotazioniByVisitatore(Utente visitatore)
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
    @ requires itinerario != null;
    @ ensures \result != null;
    @*/
  @Override
  public List<PrenotazioneAttivitaTuristica> getPrenotazioniByItinerario(Itinerario itinerario)
      throws Exception {
    if (itinerario == null) {
      throw new Exception("L'itinerario è vuoto.");
    }
    return repository.findByItinerario(itinerario.getId(), Pageable.unpaged()).toList();
  }

  /*@
    @ requires attivita != null;
    @ requires attivita.getDisponibilita() != null;
    @ requires attivita.getDisponibilita() >= 0;
    @*/
  @Override
  public int controllaDisponibilitaAttivitaTuristica(Attivita attivita, /*@ nullable @*/ Date dataInizio)
      throws Exception {
    if (attivita == null) {
      throw new Exception("L'attività è vuota.");
    }
    if (attivita.isAlloggio()) {
      throw new Exception("L'attività non può essere un alloggio.");
    }
    if (dataInizio == null) {
      throw new Exception("Data non valida.");
    }
    return Math.max(0, attivita.getDisponibilita() - repository.getPostiOccupatiIn(
        attivita.getId(), dataInizio));
  }

}
