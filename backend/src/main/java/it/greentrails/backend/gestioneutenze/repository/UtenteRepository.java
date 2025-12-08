package it.greentrails.backend.gestioneutenze.repository;

import it.greentrails.backend.entities.Utente;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UtenteRepository extends JpaRepository<Utente, Long> {

  @Query("SELECT u FROM Utente u WHERE u.email = ?1")
  Optional<Utente> findOneByEmail(String email);
}