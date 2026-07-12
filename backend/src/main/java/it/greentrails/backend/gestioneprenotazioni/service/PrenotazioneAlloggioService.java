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

  boolean deletePrenotazioneAlloggio(/*@ nullable @*/ PrenotazioneAlloggio prenotazioneAlloggio)
      throws Exception;

  /*@ ensures \result != null; @*/
  List<PrenotazioneAlloggio> getAllPrenotazioniAlloggio();

  /*@
    @ ensures \result != null;
    @*/
  List<PrenotazioneAlloggio> getPrenotazioniAlloggioByStato(/*@ nullable @*/ StatoPrenotazione stato)
      throws Exception;

  /*@
    @ ensures \result != null;
    @*/
  PrenotazioneAlloggio findById(/*@ nullable @*/ Long id) throws Exception;

  /*@
    @ ensures \result != null;
    @*/
  List<PrenotazioneAlloggio> getPrenotazioniByAlloggio(/*@ nullable @*/ Attivita attivita) throws Exception;

  /*@
    @ ensures \result != null;
    @*/
  List<PrenotazioneAlloggio> getPrenotazioniByVisitatore(/*@ nullable @*/ Utente visitatore)
      throws Exception;

  /*@
    @ ensures \result != null;
    @*/
  List<PrenotazioneAlloggio> getPrenotazioniByItinerario(/*@ nullable @*/ Itinerario itinerario)
      throws Exception;

  int controllaDisponibilitaAlloggio(/*@ nullable @*/ Attivita alloggio, /*@ nullable @*/ Date dataInizio,
      /*@ nullable @*/ Date dataFine) throws Exception;

  int controllaDisponibilitaCamera(/*@ nullable @*/ Camera camera, /*@ nullable @*/ Date dataInizio,
      /*@ nullable @*/ Date dataFine) throws Exception;
}
