package it.greentrails.backend.benchmarks;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.gestioneattivita.repository.AttivitaRepository;
import it.greentrails.backend.gestionericerca.service.RicercaService;
import it.greentrails.backend.gestionericerca.service.RicercaServiceImpl;
import java.util.ArrayList;
import java.util.List;
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
import org.openjdk.jmh.annotations.DynamicHalt;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class RicercaTestualeBenchmark {

  @Param({"100", "1000", "5000", "10000"})
  private int listSize;

  private RicercaService service;
  private String query = "mare";

  @Setup(Level.Trial)
  public void setup() {
    AttivitaRepository repository = mock(AttivitaRepository.class);
    List<Attivita> mockData = generateMockData(listSize);
    when(repository.findByQuery(query)).thenReturn(mockData);

    service = new RicercaServiceImpl(repository);
  }

  @Benchmark
  public void benchmarkRicercaTestuale(Blackhole bh) {
    List<Attivita> result = service.findAttivita(query);
    bh.consume(result);
  }

  private List<Attivita> generateMockData(int size) {
    List<Attivita> data = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      Attivita a = new Attivita();
      a.setId((long) i);
      a.setNome("Attività " + i + " al mare");
      data.add(a);
    }
    return data;
  }
}
