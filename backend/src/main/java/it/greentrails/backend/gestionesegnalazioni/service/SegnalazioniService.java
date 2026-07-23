package it.greentrails.backend.gestionesegnalazioni.service;

import it.greentrails.backend.entities.Segnalazione;
import it.greentrails.backend.enums.StatoSegnalazione;
import java.util.List;

/*@ nullable_by_default @*/
public interface SegnalazioniService {

  /*@
    @ ensures \result != null;
    @*/
  Segnalazione saveSegnalazione(/*@ nullable @*/ Segnalazione segnalazione) throws Exception;

  /*@ ensures \result != null; @*/
  List<Segnalazione> getAllSegnalazioniByTipo(boolean isForRecensione);

  /*@
    @ ensures \result != null;
    @*/
  Segnalazione findById(/*@ nullable @*/ Long id) throws Exception;

  /*@
    @ requires stato != null;
    @ ensures \result != null;
    @*/
  List<Segnalazione> getSegnalazioniByStato(StatoSegnalazione stato);
}
