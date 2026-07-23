package it.greentrails.backend.gestioneprenotazioni.service;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Camera;
import it.greentrails.backend.entities.Itinerario;
import it.greentrails.backend.entities.PrenotazioneAlloggio;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.enums.StatoPrenotazione;
import java.util.Date;
import java.util.List;

/*@ nullable_by_default @*/
public interface PrenotazioneAlloggioService {

  /*@
    @ ensures \result != null;
    @*/
  PrenotazioneAlloggio savePrenotazioneAlloggio(/*@ nullable @*/ Camera camera,
      /*@ nullable @*/ PrenotazioneAlloggio prenotazioneAlloggio) throws Exception;

  boolean deletePrenotazioneAlloggio(PrenotazioneAlloggio prenotazioneAlloggio)
      throws Exception;

  /*@ ensures \result != null; @*/
  List<PrenotazioneAlloggio> getAllPrenotazioniAlloggio();

  /*@
    @ ensures \result != null;
    @*/
  List<PrenotazioneAlloggio> getPrenotazioniAlloggioByStato(StatoPrenotazione stato)
      throws Exception;

  /*@
    @ ensures \result != null;
    @*/
  PrenotazioneAlloggio findById(/*@ nullable @*/ Long id) throws Exception;

  /*@
    @ ensures \result != null;
    @*/
  List<PrenotazioneAlloggio> getPrenotazioniByAlloggio(Attivita attivita) throws Exception;

  /*@
    @ ensures \result != null;
    @*/
  List<PrenotazioneAlloggio> getPrenotazioniByVisitatore(Utente visitatore)
      throws Exception;

  /*@
    @ ensures \result != null;
    @*/
  List<PrenotazioneAlloggio> getPrenotazioniByItinerario(Itinerario itinerario)
      throws Exception;

  int controllaDisponibilitaAlloggio(Attivita alloggio, /*@ nullable @*/ Date dataInizio,
      /*@ nullable @*/ Date dataFine) throws Exception;

  int controllaDisponibilitaCamera(Camera camera, /*@ nullable @*/ Date dataInizio,
      /*@ nullable @*/ Date dataFine) throws Exception;
}
