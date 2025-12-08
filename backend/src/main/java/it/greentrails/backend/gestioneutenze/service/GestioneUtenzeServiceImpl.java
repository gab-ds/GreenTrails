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
public class GestioneUtenzeServiceImpl implements GestioneUtenzeService {

  private final UtenteRepository repository;
  private final PreferenzeRepository preferenzeRepository;

  @Override
  public Utente findById(Long id) throws Exception {
    if (id == null || id < 0) {
      throw new Exception("L'id non è valido.");
    }
    Optional<Utente> utente = repository.findById(id);
    if (utente.isEmpty()) {
      throw new Exception("L'utente non è stato trovato.");
    }
    return utente.get();
  }

  @Override
  public Utente saveUtente(Utente utente) throws Exception {
    if (utente == null) {
      throw new Exception("L'utente è vuoto.");
    }
    return repository.save(utente);
  }

  @Override
  public Preferenze savePreferenze(Preferenze preferenze) throws Exception {
    if (preferenze == null) {
      throw new Exception("Le preferenze sono vuote.");
    }
    return preferenzeRepository.save(preferenze);
  }

  @Override
  public Optional<Utente> findByEmail(String email) {
    return repository.findOneByEmail(email);
  }

  @Override
  public boolean deleteUtente(Utente utente) throws Exception {
    if (utente == null) {
      throw new Exception("L'utente è vuoto.");
    }
    repository.delete(utente);
    repository.flush();
    return repository.findById(utente.getId()).isEmpty();
  }

  @Override
  public Preferenze getPreferenzeById(Long id) throws Exception {
    if (id == null || id < 0) {
      throw new Exception("L'id non è valido.");
    }
    Optional<Preferenze> preferenze = preferenzeRepository.findById(id);
    if (preferenze.isEmpty()) {
      throw new Exception("Le preferenze non sono state trovate.");
    }
    return preferenze.get();
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<Utente> utente = findByEmail(username);
    if (utente.isEmpty()) {
      throw new UsernameNotFoundException(username);
    }
    return utente.get();
  }
}
