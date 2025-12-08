package it.greentrails.backend.gestioneitinerari.service;

import it.greentrails.backend.entities.Itinerario;
import it.greentrails.backend.entities.Preferenze;
import it.greentrails.backend.entities.Utente;
import java.util.List;

public interface ItinerariService {

  Itinerario saveItinerario(Itinerario itinerario) throws Exception;

  Itinerario createByPreferenze(Preferenze preferenze) throws Exception;

  List<Itinerario> findItinerariByUtente(Utente utente) throws Exception;

  boolean deleteItinerario(Itinerario itinerario) throws Exception;

  Itinerario findById(Long id) throws Exception;
}
