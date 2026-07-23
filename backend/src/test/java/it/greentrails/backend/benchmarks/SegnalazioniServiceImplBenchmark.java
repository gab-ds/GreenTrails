package it.greentrails.backend.benchmarks;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.greentrails.backend.entities.Segnalazione;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.enums.StatoSegnalazione;
import it.greentrails.backend.gestionesegnalazioni.repository.SegnalazioniRepository;
import it.greentrails.backend.gestionesegnalazioni.service.SegnalazioniService;
import it.greentrails.backend.gestionesegnalazioni.service.SegnalazioniServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
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

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class SegnalazioniServiceImplBenchmark {

  @Param({"1000", "10000", "50000"})
  private int listSize;

  private SegnalazioniService service;
  private long existingId = 1L;

  @Setup(Level.Trial)
  public void setup() {
    SegnalazioniRepository repository = mock(SegnalazioniRepository.class);
    List<Segnalazione> mockData = generateMockData(listSize);

    when(repository.findByTipo(true)).thenReturn(
        mockData.stream().filter(Segnalazione::isForRecensione).toList());
    when(repository.findByTipo(false)).thenReturn(
        mockData.stream().filter(s -> !s.isForRecensione()).toList());
    when(repository.findByStato(StatoSegnalazione.CREATA)).thenReturn(
        mockData.stream().filter(s -> s.getStato() == StatoSegnalazione.CREATA).toList());
    when(repository.findById(existingId)).thenReturn(
        mockData.stream().filter(s -> s.getId().equals(existingId)).findFirst());

    service = new SegnalazioniServiceImpl(repository);
  }

  @Benchmark
  public void benchmarkFindByTipo(Blackhole bh) {
    List<Segnalazione> result = service.getAllSegnalazioniByTipo(true);
    bh.consume(result);
  }

  @Benchmark
  public void benchmarkFindByStato(Blackhole bh) {
    List<Segnalazione> result = service.getSegnalazioniByStato(StatoSegnalazione.CREATA);
    bh.consume(result);
  }

  @Benchmark
  public void benchmarkFindById(Blackhole bh) throws Exception {
    Segnalazione result = service.findById(existingId);
    bh.consume(result);
  }

  private List<Segnalazione> generateMockData(int size) {
    List<Segnalazione> data = new ArrayList<>(size);
    Random random = new Random(42);
    Utente utente = new Utente();
    utente.setId(1L);
    StatoSegnalazione[] stati = StatoSegnalazione.values();

    for (int i = 0; i < size; i++) {
      Segnalazione s = new Segnalazione();
      s.setId((long) i);
      s.setUtente(utente);
      s.setStato(stati[random.nextInt(stati.length)]);
      s.setForRecensione(random.nextBoolean());
      s.setDescrizione("Segnalazione di test numero " + i);
      data.add(s);
    }
    return data;
  }
}
