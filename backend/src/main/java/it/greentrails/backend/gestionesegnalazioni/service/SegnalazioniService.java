package it.greentrails.backend.gestionesegnalazioni.service;

import it.greentrails.backend.entities.Segnalazione;
import it.greentrails.backend.enums.StatoSegnalazione;
import java.util.List;

public interface SegnalazioniService {

  Segnalazione saveSegnalazione(Segnalazione segnalazione) throws Exception;

  List<Segnalazione> getAllSegnalazioniByTipo(boolean isForRecensione);

  Segnalazione findById(Long id) throws Exception;

  List<Segnalazione> getSegnalazioniByStato(StatoSegnalazione stato);
}
