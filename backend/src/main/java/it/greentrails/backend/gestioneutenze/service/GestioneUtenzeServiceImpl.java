package it.greentrails.backend.gestioneutenze.service;

import it.greentrails.backend.entities.Preferenze;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.gestioneutenze.repository.PreferenzeRepository;
import it.greentrails.backend.gestioneutenze.repository.UtenteRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
/*@ nullable_by_default @*/
public class GestioneUtenzeServiceImpl implements GestioneUtenzeService {

  /*@ spec_public non_null @*/
  private final UtenteRepository repository;
  /*@ spec_public non_null @*/
  private final PreferenzeRepository preferenzeRepository;

  // repository and preferenzeRepository are injected by Spring — non-null by @RequiredArgsConstructor

  /*@
    @ also
    @ ensures \result != null;
    @*/
  @Override
  public Utente findById(/*@ nullable @*/ Long id) throws Exception {
    if (id == null || id < 0) {
      throw new Exception("L'id non è valido.");
    }
    Optional<Utente> utente = repository.findById(id);
    if (utente.isEmpty()) {
      throw new Exception("L'utente non è stato trovato.");
    }
    return utente.get();
  }

  /*@
    @ also
    @ ensures \result != null;
    @*/
  @Override
  public Utente saveUtente(/*@ nullable @*/ Utente utente) throws Exception {
    if (utente == null) {
      throw new Exception("L'utente è vuoto.");
    }
    return repository.save(utente);
  }

  /*@
    @ also
    @ ensures \result != null;
    @*/
  @Override
  public Preferenze savePreferenze(/*@ nullable @*/ Preferenze preferenze) throws Exception {
    if (preferenze == null) {
      throw new Exception("Le preferenze sono vuote.");
    }
    return preferenzeRepository.save(preferenze);
  }

  /*@
    @ also
    @ requires email != null;
    @ ensures \result != null;
    @*/
  @Override
  public Optional<Utente> findByEmail(String email) {
    return repository.findOneByEmail(email);
  }

  @Override
  public boolean deleteUtente(/*@ nullable @*/ Utente utente) throws Exception {
    if (utente == null) {
      throw new Exception("L'utente è vuoto.");
    }
    repository.delete(utente);
    repository.flush();
    return repository.findById(utente.getId()).isEmpty();
  }

  /*@
    @ also
    @ ensures \result != null;
    @*/
  @Override
  public Preferenze getPreferenzeById(/*@ nullable @*/ Long id) throws Exception {
    if (id == null || id < 0) {
      throw new Exception("L'id non è valido.");
    }
    Optional<Preferenze> preferenze = preferenzeRepository.findById(id);
    if (preferenze.isEmpty()) {
      throw new Exception("Le preferenze non sono state trovate.");
    }
    return preferenze.get();
  }

  /*@
    @ also
    @ requires username != null;
    @*/
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    if (username == null) {
      throw new UsernameNotFoundException("Username is null");
    }
    Optional<Utente> utente = findByEmail(username);
    if (utente.isEmpty()) {
      throw new UsernameNotFoundException(username);
    }
    return utente.get();
  }
}
