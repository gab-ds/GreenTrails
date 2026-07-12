package it.greentrails.backend.gestioneprenotazioni.service;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Itinerario;
import it.greentrails.backend.entities.PrenotazioneAttivitaTuristica;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.enums.StatoPrenotazione;
import java.util.Date;
import java.util.List;

/*@ nullable_by_default @*/
public interface PrenotazioneAttivitaTuristicaService {

  /*@
    @ ensures \result != null;
    @*/
  PrenotazioneAttivitaTuristica savePrenotazioneAttivitaTuristica(/*@ nullable @*/ Attivita attivita,
      /*@ nullable @*/ PrenotazioneAttivitaTuristica prenotazioneAttivitaTuristica) throws Exception;

  boolean deletePrenotazioneAttivitaTuristica(
      /*@ nullable @*/ PrenotazioneAttivitaTuristica prenotazioneAttivitaTuristica) throws Exception;

  /*@ ensures \result != null; @*/
  List<PrenotazioneAttivitaTuristica> getAllPrenotazioniAttivitaTuristica();

  /*@
    @ ensures \result != null;
    @*/
  List<PrenotazioneAttivitaTuristica> getPrenotazioniAttivitaTuristicaByStato(
      /*@ nullable @*/ StatoPrenotazione stato) throws Exception;

  /*@
    @ ensures \result != null;
    @*/
  PrenotazioneAttivitaTuristica findById(/*@ nullable @*/ Long id) throws Exception;

  /*@
    @ ensures \result != null;
    @*/
  List<PrenotazioneAttivitaTuristica> getPrenotazioniByAttivitaTuristica(/*@ nullable @*/ Attivita attivita)
      throws Exception;

  /*@
    @ ensures \result != null;
    @*/
  List<PrenotazioneAttivitaTuristica> getPrenotazioniByVisitatore(/*@ nullable @*/ Utente visitatore)
      throws Exception;

  /*@
    @ ensures \result != null;
    @*/
  List<PrenotazioneAttivitaTuristica> getPrenotazioniByItinerario(/*@ nullable @*/ Itinerario itinerario)
      throws Exception;

  int controllaDisponibilitaAttivitaTuristica(/*@ nullable @*/ Attivita attivita, /*@ nullable @*/ Date dataInizio)
      throws Exception;
}
