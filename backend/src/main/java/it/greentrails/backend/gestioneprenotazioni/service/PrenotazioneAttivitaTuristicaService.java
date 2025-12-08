package it.greentrails.backend.gestioneprenotazioni.service;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Itinerario;
import it.greentrails.backend.entities.PrenotazioneAttivitaTuristica;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.enums.StatoPrenotazione;
import java.util.Date;
import java.util.List;

public interface PrenotazioneAttivitaTuristicaService {

  PrenotazioneAttivitaTuristica savePrenotazioneAttivitaTuristica(Attivita attivita,
      PrenotazioneAttivitaTuristica prenotazioneAttivitaTuristica) throws Exception;

  boolean deletePrenotazioneAttivitaTuristica(
      PrenotazioneAttivitaTuristica prenotazioneAttivitaTuristica) throws Exception;

  List<PrenotazioneAttivitaTuristica> getAllPrenotazioniAttivitaTuristica();

  List<PrenotazioneAttivitaTuristica> getPrenotazioniAttivitaTuristicaByStato(
      StatoPrenotazione stato) throws Exception;

  PrenotazioneAttivitaTuristica findById(Long id) throws Exception;

  List<PrenotazioneAttivitaTuristica> getPrenotazioniByAttivitaTuristica(Attivita attivita)
      throws Exception;

  List<PrenotazioneAttivitaTuristica> getPrenotazioniByVisitatore(Utente visitatore)
      throws Exception;

  List<PrenotazioneAttivitaTuristica> getPrenotazioniByItinerario(Itinerario itinerario)
      throws Exception;

  int controllaDisponibilitaAttivitaTuristica(Attivita attivita, Date dataInizio)
      throws Exception;
}
