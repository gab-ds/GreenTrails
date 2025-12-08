package it.greentrails.backend.gestioneprenotazioni.service;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Camera;
import it.greentrails.backend.entities.Itinerario;
import it.greentrails.backend.entities.PrenotazioneAlloggio;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.enums.RuoloUtente;
import it.greentrails.backend.enums.StatoPrenotazione;
import it.greentrails.backend.gestioneattivita.service.CameraService;
import it.greentrails.backend.gestioneprenotazioni.repository.PrenotazioneAlloggioRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrenotazioneAlloggioServiceImpl implements PrenotazioneAlloggioService {

  private final PrenotazioneAlloggioRepository repository;
  private final CameraService cameraService;

  @Override
  public PrenotazioneAlloggio savePrenotazioneAlloggio(Camera camera,
      PrenotazioneAlloggio prenotazioneAlloggio) throws Exception {
    if (prenotazioneAlloggio == null) {
      throw new Exception("La prenotazione dell'attività è vuota.");
    }
    if (camera == null) {
      throw new Exception("La camera è vuota.");
    }
    if (camera.getAlloggio() == null) {
      throw new Exception("La camera non ha un alloggio collegato.");
    }
    prenotazioneAlloggio.setCamera(camera);
    return repository.save(prenotazioneAlloggio);
  }

  @Override
  public boolean deletePrenotazioneAlloggio(PrenotazioneAlloggio prenotazioneAlloggio)
      throws Exception {
    if (prenotazioneAlloggio == null) {
      throw new Exception("La prenotazione dell'alloggio è vuota.");
    }
    repository.delete(prenotazioneAlloggio);
    repository.flush();
    return repository.findById(prenotazioneAlloggio.getId()).isEmpty();
  }

  @Override
  public List<PrenotazioneAlloggio> getAllPrenotazioniAlloggio() {
    return repository.findAll();
  }

  @Override
  public List<PrenotazioneAlloggio> getPrenotazioniAlloggioByStato(StatoPrenotazione stato)
      throws Exception {
    if (stato == null) {
      throw new Exception("Lo stato della prenotazione dell'alloggio è inesistente.");
    }
    List<PrenotazioneAlloggio> risultato = new ArrayList<>();
    getAllPrenotazioniAlloggio().forEach(p -> {
      if (p.getStato().equals(stato)) {
        risultato.add(p);
      }
    });
    return risultato;
  }

  @Override
  public PrenotazioneAlloggio findById(Long id) throws Exception {
    if (id == null || id < 0) {
      throw new Exception("L'id non è valido.");
    }
    Optional<PrenotazioneAlloggio> prenotazioneAlloggio = repository.findById(id);
    if (prenotazioneAlloggio.isEmpty()) {
      throw new Exception("La prenotazione non è stata trovata.");
    }
    return prenotazioneAlloggio.get();
  }


  @Override
  public List<PrenotazioneAlloggio> getPrenotazioniByAlloggio(Attivita attivita) throws Exception {
    if (attivita == null) {
      throw new Exception("L'attività è vuota.");
    }
    if (!attivita.isAlloggio()) {
      throw new Exception("L'attività non può essere un'attività turistica.");
    }
    return repository.findByAlloggio(attivita.getId(), Pageable.unpaged()).toList();
  }

  @Override
  public List<PrenotazioneAlloggio> getPrenotazioniByVisitatore(Utente visitatore)
      throws Exception {
    if (visitatore == null) {
      throw new Exception("L'utente è vuoto.");
    }
    if (visitatore.getRuolo() != RuoloUtente.VISITATORE) {
      throw new Exception("L'utente non è un visitatore.");
    }
    return repository.findByVisitatore(visitatore.getId(), Pageable.unpaged()).toList();
  }

  @Override
  public List<PrenotazioneAlloggio> getPrenotazioniByItinerario(Itinerario itinerario)
      throws Exception {
    if (itinerario == null) {
      throw new Exception("L'itinerario è vuoto.");
    }
    return repository.findByItinerario(itinerario.getId(), Pageable.unpaged()).toList();
  }

  @Override
  public int controllaDisponibilitaAlloggio(Attivita alloggio, Date dataInizio, Date dataFine)
      throws Exception {
    if (alloggio == null) {
      throw new Exception("L'alloggio è vuoto.");
    }
    if (!alloggio.isAlloggio()) {
      throw new Exception("L'attività non è un alloggio.");
    }
    if (dataFine.before(dataInizio)) {
      throw new Exception("La data di fine non può essere precedente alla data di inizio.");
    }
    return cameraService
        .getCamereByAlloggio(alloggio)
        .stream()
        .mapToInt(Camera::getDisponibilita)
        .sum() - repository.getPostiOccupatiAlloggioTra(alloggio.getId(), dataInizio, dataFine);
  }

  @Override
  public int controllaDisponibilitaCamera(Camera camera, Date dataInizio, Date dataFine)
      throws Exception {
    if (camera == null) {
      throw new Exception("La camera è vuota.");
    }
    if (dataFine.before(dataInizio)) {
      throw new Exception("La data di fine non può essere precedente alla data di inizio.");
    }
    return camera.getDisponibilita() - repository.getPostiOccupatiCameraTra(camera.getId(),
        dataInizio, dataFine);
  }
}
