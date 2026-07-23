package it.greentrails.backend.gestioneitinerari.service;

import it.greentrails.backend.entities.Itinerario;
import it.greentrails.backend.entities.Preferenze;
import it.greentrails.backend.entities.Utente;
import java.util.List;

/*@ nullable_by_default @*/
public interface ItinerariService {

  /*@ ensures \result != null; @*/
  Itinerario saveItinerario(/*@ nullable @*/ Itinerario itinerario) throws Exception;

  /*@ ensures \result != null; @*/
  Itinerario createByPreferenze(/*@ nullable @*/ Preferenze preferenze) throws Exception;

  /*@ ensures \result != null; @*/
  List<Itinerario> findItinerariByUtente(Utente utente) throws Exception;

  boolean deleteItinerario(Itinerario itinerario) throws Exception;

  /*@ ensures \result != null; @*/
  Itinerario findById(/*@ nullable @*/ Long id) throws Exception;
}
