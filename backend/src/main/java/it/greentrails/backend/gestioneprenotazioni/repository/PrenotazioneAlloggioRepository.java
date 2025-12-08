package it.greentrails.backend.gestioneprenotazioni.repository;

import it.greentrails.backend.entities.PrenotazioneAlloggio;
import java.util.Date;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PrenotazioneAlloggioRepository extends JpaRepository<PrenotazioneAlloggio, Long> {

  @Query("SELECT p FROM PrenotazioneAlloggio p JOIN p.camera.alloggio a WHERE a.id = ?1")
  Page<PrenotazioneAlloggio> findByAlloggio(Long idAlloggio, Pageable pageable);

  @Query("SELECT p FROM PrenotazioneAlloggio p WHERE p.itinerario.visitatore.id = ?1")
  Page<PrenotazioneAlloggio> findByVisitatore(Long idVisitatore, Pageable pageable);

  @Query("SELECT p FROM PrenotazioneAlloggio p WHERE p.itinerario.id = ?1")
  Page<PrenotazioneAlloggio> findByItinerario(Long idItinerario, Pageable pageable);

  @Query("""
      SELECT COALESCE(SUM(p.numCamere), 0) FROM Camera c
      LEFT JOIN PrenotazioneAlloggio p ON p.camera = c
      WHERE c.alloggio.id = ?1
      AND (
      ?2 BETWEEN p.dataInizio AND p.dataFine OR
      ?3 BETWEEN p.dataInizio AND p.dataFine OR
      p.dataInizio BETWEEN ?2 AND ?3
      OR p.dataFine BETWEEN ?2 AND ?3)
      """)
  int getPostiOccupatiAlloggioTra(long idAttivita, Date dataInizio, Date dataFine);

  @Query("""
      SELECT COALESCE(SUM(p.numCamere), 0) FROM Camera c
      LEFT JOIN PrenotazioneAlloggio p ON p.camera = c
      WHERE c.id = ?1
      AND (
      ?2 BETWEEN p.dataInizio AND p.dataFine OR
      ?3 BETWEEN p.dataInizio AND p.dataFine OR
      p.dataInizio BETWEEN ?2 AND ?3
      OR p.dataFine BETWEEN ?2 AND ?3)
      """)
  int getPostiOccupatiCameraTra(long idCamera, Date dataInizio, Date dataFine);
}