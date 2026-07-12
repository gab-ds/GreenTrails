package it.greentrails.backend.gestioneutenze.service;

import it.greentrails.backend.entities.Preferenze;
import it.greentrails.backend.entities.Utente;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetailsService;

/*@ nullable_by_default @*/
public interface GestioneUtenzeService extends UserDetailsService {

  /*@ ensures \result != null; @*/
  Utente findById(/*@ nullable @*/ Long id) throws Exception;

  /*@ ensures \result != null; @*/
  Utente saveUtente(/*@ nullable @*/ Utente utente) throws Exception;

  /*@ ensures \result != null; @*/
  Preferenze savePreferenze(/*@ nullable @*/ Preferenze preferenze) throws Exception;

  /*@
    @ assignable \nothing;
    @ ensures \result != null;
    @*/
  Optional<Utente> findByEmail(/*@ nullable @*/ String email);

  boolean deleteUtente(/*@ nullable @*/ Utente utente) throws Exception;

  /*@ ensures \result != null; @*/
  Preferenze getPreferenzeById(/*@ nullable @*/ Long id) throws Exception;
}
