package it.greentrails.backend.gestioneattivita.service;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Recensione;
import it.greentrails.backend.entities.Utente;
import java.util.List;

/*@ nullable_by_default @*/
public interface RecensioneService {

  /*@
    @ ensures \result != null;
    @*/
  Recensione saveRecensione(/*@ nullable @*/ Recensione recensione) throws Exception;

  /*@
    @ ensures \result != null;
    @*/
  Recensione findById(/*@ nullable @*/ Long id) throws Exception;

  boolean deleteRecensione(/*@ nullable @*/ Recensione recensione) throws Exception;

  /*@
    @ ensures \result != null;
    @*/
  List<Recensione> getRecensioniByAttivita(/*@ nullable @*/ Attivita attivita) throws Exception;

  /*@
    @ ensures \result != null;
    @*/
  List<Recensione> getAllRecensioniByVisitatore(/*@ nullable @*/ Utente utente) throws Exception;
}
