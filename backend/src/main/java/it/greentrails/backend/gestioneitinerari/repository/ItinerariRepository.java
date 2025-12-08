package it.greentrails.backend.gestioneitinerari.repository;

import it.greentrails.backend.entities.Itinerario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ItinerariRepository extends JpaRepository<Itinerario, Long> {

  @Query("SELECT i FROM Itinerario i WHERE i.visitatore.id = ?1")
  Page<Itinerario> findByVisitatore(Long idVisitatore, Pageable pageable);

}