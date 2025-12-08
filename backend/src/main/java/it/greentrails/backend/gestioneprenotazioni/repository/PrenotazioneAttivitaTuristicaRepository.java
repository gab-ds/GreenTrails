package it.greentrails.backend.gestioneprenotazioni.repository;

import it.greentrails.backend.entities.PrenotazioneAttivitaTuristica;
import java.util.Date;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PrenotazioneAttivitaTuristicaRepository extends
    JpaRepository<PrenotazioneAttivitaTuristica, Long> {

  @Query("SELECT p FROM PrenotazioneAttivitaTuristica p JOIN p.attivitaTuristica a WHERE a.id = ?1")
  Page<PrenotazioneAttivitaTuristica> findByAttivitaTuristica(Long idAttivitaTuristica,
      Pageable pageable);

  @Query("SELECT p FROM PrenotazioneAttivitaTuristica p WHERE p.itinerario.visitatore.id = ?1")
  Page<PrenotazioneAttivitaTuristica> findByVisitatore(Long idVisitatore, Pageable pageable);

  @Query("SELECT p FROM PrenotazioneAttivitaTuristica p WHERE p.itinerario.id = ?1")
  Page<PrenotazioneAttivitaTuristica> findByItinerario(Long idItinerario, Pageable pageable);

  @Query("""
      SELECT COALESCE(SUM(p.numAdulti) + SUM(p.numBambini), 0)
      FROM Attivita a
      LEFT JOIN PrenotazioneAttivitaTuristica p ON p.attivitaTuristica = a
      WHERE a.id = ?1
      AND p.dataInizio = ?2
      """)
  int getPostiOccupatiIn(long idAttivita, Date dataInizio);

}