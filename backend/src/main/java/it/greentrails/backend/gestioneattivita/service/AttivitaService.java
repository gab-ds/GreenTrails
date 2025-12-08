package it.greentrails.backend.gestioneattivita.service;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.ValoriEcosostenibilita;
import java.util.List;
import java.util.Optional;

public interface AttivitaService {

  Attivita saveAttivita(Attivita attivita) throws Exception;

  Attivita findById(Long id) throws Exception;

  List<Attivita> findAllAttivitaByGestore(Long idGestore) throws Exception;

  Optional<Attivita> findByValori(ValoriEcosostenibilita valoriEcosostenibilita)
      throws Exception;

  List<Attivita> getAttivitaTuristicheEconomiche(int limite);

  boolean deleteAttivita(Attivita attivita) throws Exception;

  List<Attivita> getAttivitaTuristiche(int limite);

  List<Attivita> getAlloggi(int limite);

  List<Attivita> findAll();
}