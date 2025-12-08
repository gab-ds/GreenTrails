package it.greentrails.backend.gestioneattivita.service;

import it.greentrails.backend.entities.ValoriEcosostenibilita;

public interface ValoriEcosostenibilitaService {

  ValoriEcosostenibilita saveValori(ValoriEcosostenibilita valori) throws Exception;

  boolean deleteValori(ValoriEcosostenibilita valori) throws Exception;

  ValoriEcosostenibilita findById(Long id) throws Exception;
}
