package it.greentrails.backend.gestioneattivita.repository;

import it.greentrails.backend.entities.Attivita;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AttivitaRepository extends JpaRepository<Attivita, Long> {

  @Query("SELECT a FROM Attivita a WHERE a.gestore.id = ?1")
  Page<Attivita> findByGestore(Long idGestore, Pageable pageable);

  @Query("SELECT a FROM Attivita a WHERE a.valoriEcosostenibilita.id = ?1")
  Optional<Attivita> findByValori(Long idValori);

  @Query("SELECT a FROM Attivita a WHERE a.nome ILIKE %?1% OR a.citta ILIKE %?1%")
  List<Attivita> findByQuery(String query);

  @Query("SELECT a FROM Attivita a JOIN Categoria c WHERE c.id = ?1")
  List<Attivita> findByCategoria(long idCategoria);

  @Query("SELECT a FROM Attivita a WHERE a.media = ?1")
  Optional<Attivita> findOneByMedia(String media);

  @Query("SELECT a FROM Attivita a WHERE a.prezzo IS NOT NULL ORDER BY a.prezzo ASC")
  Page<Attivita> getAllByPrezzo(Pageable pageable);

  @Query("SELECT a FROM Attivita a WHERE a.isAlloggio = true")
  Page<Attivita> getAlloggi(Pageable pageable);

  @Query("SELECT a FROM Attivita a WHERE a.isAlloggio = false")
  Page<Attivita> getAttivitaTuristiche(Pageable pageable);

  @Query("SELECT a FROM Attivita a")
  List<Attivita> findAll();

}