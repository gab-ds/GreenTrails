package it.greentrails.backend.gestioneattivita.service;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.ValoriEcosostenibilita;
import java.util.List;
import java.util.Optional;

/*@ nullable_by_default @*/
public interface AttivitaService {

  /*@ ensures \result != null; @*/
  Attivita saveAttivita(/*@ nullable @*/ Attivita attivita) throws Exception;

  /*@ ensures \result != null; @*/
  Attivita findById(/*@ nullable @*/ Long id) throws Exception;

  /*@ ensures \result != null; @*/
  List<Attivita> findAllAttivitaByGestore(/*@ nullable @*/ Long idGestore) throws Exception;

  /*@ ensures \result != null; @*/
  Optional<Attivita> findByValori(/*@ nullable @*/ ValoriEcosostenibilita valoriEcosostenibilita)
      throws Exception;

  /*@ ensures \result != null; @*/
  List<Attivita> getAttivitaTuristicheEconomiche(int limite);

  boolean deleteAttivita(/*@ nullable @*/ Attivita attivita) throws Exception;

  /*@ ensures \result != null; @*/
  List<Attivita> getAttivitaTuristiche(int limite);

  /*@ ensures \result != null; @*/
  List<Attivita> getAlloggi(int limite);

  /*@ ensures \result != null; @*/
  List<Attivita> findAll();
}