package it.greentrails.backend.gestioneitinerari.adapter;

import it.greentrails.backend.entities.Itinerario;
import it.greentrails.backend.entities.Preferenze;
import org.springframework.stereotype.Service;

// TODO: inserire integrazione modulo AI
@Service
public class ItinerariAiAdapter implements ItinerariAdapter {

  @Override
  public Itinerario pianificazioneAutomatica(Preferenze preferenze) {
    Itinerario itinerario = new Itinerario();
    itinerario.setVisitatore(preferenze.getVisitatore());
    return itinerario;
  }

}
