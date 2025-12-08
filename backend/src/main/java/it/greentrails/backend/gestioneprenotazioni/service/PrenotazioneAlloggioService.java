package it.greentrails.backend.gestioneprenotazioni.service;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Camera;
import it.greentrails.backend.entities.Itinerario;
import it.greentrails.backend.entities.PrenotazioneAlloggio;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.enums.StatoPrenotazione;
import java.util.Date;
import java.util.List;

public interface PrenotazioneAlloggioService {

  PrenotazioneAlloggio savePrenotazioneAlloggio(Camera camera,
      PrenotazioneAlloggio prenotazioneAlloggio) throws Exception;

  boolean deletePrenotazioneAlloggio(PrenotazioneAlloggio prenotazioneAlloggio)
      throws Exception;

  List<PrenotazioneAlloggio> getAllPrenotazioniAlloggio();

  List<PrenotazioneAlloggio> getPrenotazioniAlloggioByStato(StatoPrenotazione stato)
      throws Exception;

  PrenotazioneAlloggio findById(Long id) throws Exception;

  List<PrenotazioneAlloggio> getPrenotazioniByAlloggio(Attivita attivita) throws Exception;

  List<PrenotazioneAlloggio> getPrenotazioniByVisitatore(Utente visitatore)
      throws Exception;

  List<PrenotazioneAlloggio> getPrenotazioniByItinerario(Itinerario itinerario)
      throws Exception;

  int controllaDisponibilitaAlloggio(Attivita alloggio, Date dataInizio, Date dataFine)
      throws Exception;

  int controllaDisponibilitaCamera(Camera camera, Date dataInizio, Date dataFine)
      throws Exception;
}
