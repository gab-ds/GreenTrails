package it.greentrails.backend.gestioneattivita.service;

import it.greentrails.backend.entities.ValoriEcosostenibilita;

/*@ nullable_by_default @*/
public interface ValoriEcosostenibilitaService {

  /*@
    @ ensures \result != null;
    @*/
  ValoriEcosostenibilita saveValori(/*@ nullable @*/ ValoriEcosostenibilita valori) throws Exception;

  boolean deleteValori(/*@ nullable @*/ ValoriEcosostenibilita valori) throws Exception;

  /*@
    @ ensures \result != null;
    @*/
  ValoriEcosostenibilita findById(/*@ nullable @*/ Long id) throws Exception;
}
