package it.greentrails.backend.gestioneitinerari.adapter;

import it.greentrails.backend.entities.Itinerario;
import it.greentrails.backend.entities.Preferenze;

/*@ nullable_by_default @*/
public interface ItinerariAdapter {

  /*@
    @ requires preferenze != null;
    @ ensures \result != null;
    @*/
  Itinerario pianificazioneAutomatica(Preferenze preferenze);
}
