package it.greentrails.backend.gestioneattivita.service;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Recensione;
import it.greentrails.backend.entities.Utente;
import java.util.List;

public interface RecensioneService {

  Recensione saveRecensione(Recensione recensione) throws Exception;

  Recensione findById(Long id) throws Exception;

  boolean deleteRecensione(Recensione recensione) throws Exception;

  List<Recensione> getRecensioniByAttivita(Attivita attivita) throws Exception;

  List<Recensione> getAllRecensioniByVisitatore(Utente utente) throws Exception;
}
