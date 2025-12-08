package it.greentrails.backend.gestioneattivita.repository;

import it.greentrails.backend.entities.Recensione;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RecensioneRepository extends JpaRepository<Recensione, Long> {

  @Query("SELECT r FROM Recensione r WHERE r.attivita.id = ?1")
  Page<Recensione> findByAttivita(Long idAttivita, Pageable pageable);

  @Query("SELECT r FROM Recensione r WHERE r.media = ?1")
  Optional<Recensione> findOneByMedia(String media);
}