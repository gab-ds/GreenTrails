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
public class ItinerariServiceImpl implements ItinerariService {

  private final ItinerariRepository repository;
  private final PrenotazioneAlloggioService prenotazioneAlloggioService;
  private final PrenotazioneAttivitaTuristicaService prenotazioneAttivitaTuristicaService;
  private final PrenotazioneAlloggioRepository prenotazioneAlloggioRepository;
  private final PrenotazioneAttivitaTuristicaRepository prenotazioneAttivitaTuristicaRepository;
  private final ItinerariAdapter itinerariStubAdapter;

  @Override
  public Itinerario saveItinerario(Itinerario itinerario) throws Exception {
    if (itinerario == null) {
      throw new Exception("L'itinerario è vuoto.");
    }
    return repository.save(itinerario);
  }


  @Override
  public Itinerario createByPreferenze(Preferenze preferenze) throws Exception {
    if (preferenze == null) {
      throw new Exception("Le preferenze sono vuote.");
    }
    return itinerariStubAdapter.pianificazioneAutomatica(preferenze);
  }

  @Override
  public List<Itinerario> findItinerariByUtente(Utente utente) throws Exception {
    if (utente == null) {
      throw new Exception("L'utente è vuoto.");
    }
    if (utente.getRuolo() != RuoloUtente.VISITATORE) {
      throw new Exception("L'utente non è un visitatore.");
    }
    return repository.findByVisitatore(utente.getId(), Pageable.unpaged()).toList();
  }

  @Override
  public boolean deleteItinerario(Itinerario itinerario) throws Exception {
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
  public Itinerario findById(Long id) throws Exception {
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
