package it.greentrails.backend.gestioneutenze.service;

import it.greentrails.backend.entities.Preferenze;
import it.greentrails.backend.entities.Utente;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface GestioneUtenzeService extends UserDetailsService {

  Utente findById(Long id) throws Exception;

  Utente saveUtente(Utente utente) throws Exception;

  Preferenze savePreferenze(Preferenze preferenze) throws Exception;

  Optional<Utente> findByEmail(String email);

  boolean deleteUtente(Utente utente) throws Exception;

  Preferenze getPreferenzeById(Long id) throws Exception;
}
