package it.greentrails.backend.benchmarks;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Camera;
import it.greentrails.backend.entities.Categoria;
import it.greentrails.backend.entities.Itinerario;
import it.greentrails.backend.entities.Preferenze;
import it.greentrails.backend.entities.PrenotazioneAlloggio;
import it.greentrails.backend.entities.PrenotazioneAttivitaTuristica;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.enums.StatoPrenotazione;
import it.greentrails.backend.gestioneattivita.repository.AttivitaRepository;
import it.greentrails.backend.gestioneattivita.repository.CameraRepository;
import it.greentrails.backend.gestioneitinerari.adapter.ItinerariAdapter;
import it.greentrails.backend.gestioneitinerari.adapter.ItinerariStubAdapter;
import it.greentrails.backend.gestioneitinerari.repository.ItinerariRepository;
import it.greentrails.backend.gestioneprenotazioni.repository.PrenotazioneAlloggioRepository;
import it.greentrails.backend.gestioneprenotazioni.repository.PrenotazioneAttivitaTuristicaRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS) // Milliseconds because shuffle is heavy
@Fork(1)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class ItinerariAdapterBenchmark {

    @Param({"100", "1000", "5000", "10000"})
    private int listSize;

    private ItinerariAdapter adapter;
    private Preferenze dummyPreferenze;

    @Setup(Level.Trial)
    public void setup() {
        // Prepare fake data
        List<Attivita> attivita = generateAttivita(listSize);
        List<Camera> camere = generateCamere(listSize);

        // Initialize Adapter
        adapter = new ItinerariStubAdapter(
            new FakeAttivitaRepository(attivita),
            new FakeCameraRepository(camere),
            new FakeItinerariRepository(),
            new FakePrenotazioneAlloggioRepository(),
            new FakePrenotazioneAttivitaTuristicaRepository()
        );

        // Dummy Preferenze
        dummyPreferenze = new Preferenze();
        Utente u = new Utente();
        u.setId(1L);
        dummyPreferenze.setVisitatore(u);
    }

    @Benchmark
    public void benchmarkPianificazione(Blackhole bh) {
        Itinerario result = adapter.pianificazioneAutomatica(dummyPreferenze);
        bh.consume(result);
    }

    private List<Attivita> generateAttivita(int size) {
        List<Attivita> list = new ArrayList<>(size);
        Random r = new Random(42);
        for (int i = 0; i < size; i++) {
            Attivita a = new Attivita();
            a.setId((long) i);
            a.setPrezzo(10.0 + r.nextDouble() * 100);
            // Mix of Alloggio (true) and Attivita Turistica (false)
            // Adapter filters for !isAlloggio
            a.setAlloggio(r.nextBoolean());
            list.add(a);
        }
        return list;
    }

    private List<Camera> generateCamere(int size) {
        List<Camera> list = new ArrayList<>(size);
        Random r = new Random(42);
        for (int i = 0; i < size; i++) {
            Camera c = new Camera();
            c.setId((long) i);
            c.setPrezzo(50.0 + r.nextDouble() * 200);
            list.add(c);
        }
        return list;
    }
}

/**
 * Implementazione banale in memoria di {@link JpaRepository}, pensata per i benchmark: nessuna
 * dipendenza da Mockito o reflection nel percorso misurato.
 *
 * @param <T>  tipo dell'entità
 * @param <ID> tipo della chiave primaria
 */
abstract class FakeJpaRepository<T, ID> implements JpaRepository<T, ID> {

  protected final List<T> items = new ArrayList<>();

  @Override
  public <S extends T> S save(S entity) {
    items.add(entity);
    return entity;
  }

  @Override
  public <S extends T> List<S> saveAll(Iterable<S> entities) {
    List<S> saved = new ArrayList<>();
    entities.forEach(e -> saved.add(save(e)));
    return saved;
  }

  @Override
  public Optional<T> findById(ID id) {
    return Optional.empty();
  }

  @Override
  public boolean existsById(ID id) {
    return false;
  }

  @Override
  public List<T> findAll() {
    return new ArrayList<>(items);
  }

  @Override
  public List<T> findAllById(Iterable<ID> ids) {
    return List.of();
  }

  @Override
  public long count() {
    return items.size();
  }

  @Override
  public void deleteById(ID id) {
    // no-op
  }

  @Override
  public void delete(T entity) {
    items.remove(entity);
  }

  @Override
  public void deleteAllById(Iterable<? extends ID> ids) {
    // no-op
  }

  @Override
  public void deleteAll(Iterable<? extends T> entities) {
    // no-op
  }

  @Override
  public void deleteAll() {
    items.clear();
  }

  @Override
  public List<T> findAll(Sort sort) {
    return findAll();
  }

  @Override
  public Page<T> findAll(Pageable pageable) {
    return Page.empty(pageable);
  }

  @Override
  public void flush() {
    // no-op
  }

  @Override
  public <S extends T> S saveAndFlush(S entity) {
    return save(entity);
  }

  @Override
  public <S extends T> List<S> saveAllAndFlush(Iterable<S> entities) {
    return saveAll(entities);
  }

  @Override
  public void deleteAllInBatch(Iterable<T> entities) {
    // no-op
  }

  @Override
  public void deleteAllInBatch() {
    items.clear();
  }

  @Override
  public void deleteAllByIdInBatch(Iterable<ID> ids) {
    // no-op
  }

  @Override
  public T getReferenceById(ID id) {
    return null;
  }

  @Override
  public T getById(ID id) {
    return null;
  }

  @Override
  public T getOne(ID id) {
    return null;
  }

  @Override
  public <S extends T> Optional<S> findOne(Example<S> example) {
    return Optional.empty();
  }

  @Override
  public <S extends T> List<S> findAll(Example<S> example) {
    return List.of();
  }

  @Override
  public <S extends T> List<S> findAll(Example<S> example, Sort sort) {
    return List.of();
  }

  @Override
  public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
    return Page.empty(pageable);
  }

  @Override
  public <S extends T> long count(Example<S> example) {
    return 0;
  }

  @Override
  public <S extends T> boolean exists(Example<S> example) {
    return false;
  }

  @Override
  public <S extends T, R> R findBy(Example<S> example,
      Function<FetchableFluentQuery<S>, R> queryFunction) {
    return null;
  }
}

class FakeAttivitaRepository extends FakeJpaRepository<Attivita, Long>
    implements AttivitaRepository {

  FakeAttivitaRepository(List<Attivita> data) {
    items.addAll(data);
  }

  @Override
  public Page<Attivita> findByGestore(Long idGestore, Pageable pageable) {
    return Page.empty(pageable);
  }

  @Override
  public Optional<Attivita> findByValori(Long idValori) {
    return Optional.empty();
  }

  @Override
  public List<Attivita> findByQuery(String query) {
    return List.of();
  }

  @Override
  public List<Attivita> findByCategoria(long idCategoria) {
    return List.of();
  }

  @Override
  public List<Attivita> findByCategorie(List<Categoria> categorie, long numCategorie) {
    return List.of();
  }

  @Override
  public Optional<Attivita> findOneByMedia(String media) {
    return Optional.empty();
  }

  @Override
  public Page<Attivita> getAllByPrezzo(Pageable pageable) {
    return Page.empty(pageable);
  }

  @Override
  public Page<Attivita> getAlloggi(Pageable pageable) {
    return Page.empty(pageable);
  }

  @Override
  public Page<Attivita> getAttivitaTuristiche(Pageable pageable) {
    return Page.empty(pageable);
  }

  @Override
  public List<Attivita> findByPosizione(double lat, double lon, double raggio) {
    return List.of();
  }
}

class FakeCameraRepository extends FakeJpaRepository<Camera, Long>
    implements CameraRepository {

  FakeCameraRepository(List<Camera> data) {
    items.addAll(data);
  }
}

class FakeItinerariRepository extends FakeJpaRepository<Itinerario, Long>
    implements ItinerariRepository {

  @Override
  public Itinerario save(Itinerario itinerario) {
    itinerario.setId(99L);
    return super.save(itinerario);
  }

  @Override
  public Page<Itinerario> findByVisitatore(Long idVisitatore, Pageable pageable) {
    return Page.empty(pageable);
  }
}

class FakePrenotazioneAlloggioRepository extends FakeJpaRepository<PrenotazioneAlloggio, Long>
    implements PrenotazioneAlloggioRepository {

  @Override
  public Page<PrenotazioneAlloggio> findByAlloggio(Long idAlloggio, Pageable pageable) {
    return Page.empty(pageable);
  }

  @Override
  public Page<PrenotazioneAlloggio> findByVisitatore(Long idVisitatore, Pageable pageable) {
    return Page.empty(pageable);
  }

  @Override
  public Page<PrenotazioneAlloggio> findByItinerario(Long idItinerario, Pageable pageable) {
    return Page.empty(pageable);
  }

  @Override
  public List<PrenotazioneAlloggio> findByStato(StatoPrenotazione stato) {
    return List.of();
  }

  @Override
  public int getPostiOccupatiAlloggioTra(long idAttivita, Date dataInizio, Date dataFine) {
    return 0;
  }

  @Override
  public int getPostiOccupatiCameraTra(long idCamera, Date dataInizio, Date dataFine) {
    return 0;
  }
}

class FakePrenotazioneAttivitaTuristicaRepository
    extends FakeJpaRepository<PrenotazioneAttivitaTuristica, Long>
    implements PrenotazioneAttivitaTuristicaRepository {

  @Override
  public Page<PrenotazioneAttivitaTuristica> findByAttivitaTuristica(Long idAttivitaTuristica,
      Pageable pageable) {
    return Page.empty(pageable);
  }

  @Override
  public Page<PrenotazioneAttivitaTuristica> findByVisitatore(Long idVisitatore,
      Pageable pageable) {
    return Page.empty(pageable);
  }

  @Override
  public Page<PrenotazioneAttivitaTuristica> findByItinerario(Long idItinerario,
      Pageable pageable) {
    return Page.empty(pageable);
  }

  @Override
  public List<PrenotazioneAttivitaTuristica> findByStato(StatoPrenotazione stato) {
    return List.of();
  }

  @Override
  public int getPostiOccupatiIn(long idAttivita, Date dataInizio) {
    return 0;
  }
}
