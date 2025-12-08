package it.greentrails.backend.gestionesegnalazioni.repository;

import it.greentrails.backend.entities.Segnalazione;
import it.greentrails.backend.enums.StatoSegnalazione;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SegnalazioniRepository extends JpaRepository<Segnalazione, Long> {

  @Query("Select s FROM Segnalazione s WHERE s.isForRecensione = ?1")
  List<Segnalazione> findByTipo(boolean isForRecensione);

  @Query("Select s FROM Segnalazione s WHERE s.stato = ?1")
  List<Segnalazione> findByStato(StatoSegnalazione stato);

  @Query("SELECT s FROM Segnalazione s WHERE s.media = ?1")
  Optional<Segnalazione> findOneByMedia(String media);

}