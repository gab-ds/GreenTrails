package it.greentrails.backend.gestioneitinerari.service;

import it.greentrails.backend.entities.Itinerario;
import it.greentrails.backend.entities.Preferenze;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.enums.RuoloUtente;
import it.greentrails.backend.gestioneitinerari.adapter.ItinerariAdapter;
import it.greentrails.backend.gestioneitinerari.repository.ItinerariRepository;
import it.greentrails.backend.gestioneprenotazioni.repository.PrenotazioneAlloggioRepository;
import it.greentrails.backend.gestioneprenotazioni.repository.PrenotazioneAttivitaTuristicaRepository;
import it.greentrails.backend.gestioneprenotazioni.service.PrenotazioneAlloggioService;
import it.greentrails.backend.gestioneprenotazioni.service.PrenotazioneAttivitaTuristicaService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
/*@ nullable_by_default @*/
public class ItinerariServiceImpl implements ItinerariService {

  /*@ spec_public @*/
  private final ItinerariRepository repository;
  /*@ spec_public @*/
  private final PrenotazioneAlloggioService prenotazioneAlloggioService;
  /*@ spec_public @*/
  private final PrenotazioneAttivitaTuristicaService prenotazioneAttivitaTuristicaService;
  /*@ spec_public @*/
  private final PrenotazioneAlloggioRepository prenotazioneAlloggioRepository;
  /*@ spec_public @*/
  private final PrenotazioneAttivitaTuristicaRepository prenotazioneAttivitaTuristicaRepository;
  /*@ spec_public @*/
  private final ItinerariAdapter itinerariStubAdapter;

  //@ public invariant repository != null;
  //@ public invariant prenotazioneAlloggioService != null;
  //@ public invariant prenotazioneAttivitaTuristicaService != null;
  //@ public invariant prenotazioneAlloggioRepository != null;
  //@ public invariant prenotazioneAttivitaTuristicaRepository != null;
  //@ public invariant itinerariStubAdapter != null;

  @Override
  public Itinerario saveItinerario(/*@ nullable @*/ Itinerario itinerario) throws Exception {
    if (itinerario == null) {
      throw new Exception("L'itinerario è vuoto.");
    }
    return repository.save(itinerario);
  }

  @Override
  public Itinerario createByPreferenze(/*@ nullable @*/ Preferenze preferenze) throws Exception {
    if (preferenze == null) {
      throw new Exception("Le preferenze sono vuote.");
    }
    return itinerariStubAdapter.pianificazioneAutomatica(preferenze);
  }

  @Override
  public List<Itinerario> findItinerariByUtente(/*@ nullable @*/ Utente utente) throws Exception {
    if (utente == null) {
      throw new Exception("L'utente è vuoto.");
    }
    if (utente.getRuolo() != RuoloUtente.VISITATORE) {
      throw new Exception("L'utente non è un visitatore.");
    }
    return repository.findByVisitatore(utente.getId(), Pageable.unpaged()).toList();
  }

  @Override
  public boolean deleteItinerario(/*@ nullable @*/ Itinerario itinerario) throws Exception {
    if (itinerario == null) {
      throw new Exception("L'itinerario è vuoto.");
    }
    prenotazioneAlloggioRepository.deleteAllInBatch(
        prenotazioneAlloggioService.getPrenotazioniByItinerario(itinerario));
    prenotazioneAttivitaTuristicaRepository.deleteAllInBatch(
        prenotazioneAttivitaTuristicaService.getPrenotazioniByItinerario(itinerario));
    repository.delete(itinerario);
    prenotazioneAlloggioRepository.flush();
    prenotazioneAttivitaTuristicaRepository.flush();
    repository.flush();
    return repository.findById(itinerario.getId()).isEmpty();
  }

  @Override
  public Itinerario findById(/*@ nullable @*/ Long id) throws Exception {
    if (id == null || id < 0) {
      throw new Exception("L'id non è valido.");
    }
    Optional<Itinerario> itinerario = repository.findById(id);
    if (itinerario.isEmpty()) {
      throw new Exception("L'itinerario non è stato trovato.");
    }
    return itinerario.get();
  }

}
