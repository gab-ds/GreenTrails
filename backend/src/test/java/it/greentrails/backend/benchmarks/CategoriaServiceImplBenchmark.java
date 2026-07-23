package it.greentrails.backend.benchmarks;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.greentrails.backend.entities.Categoria;
import it.greentrails.backend.gestioneattivita.repository.CategoriaRepository;
import it.greentrails.backend.gestioneattivita.service.CategoriaService;
import it.greentrails.backend.gestioneattivita.service.CategoriaServiceImpl;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
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
public class CategoriaServiceImplBenchmark {

  private CategoriaService service;
  private Categoria testCategoria;
  private long existingId = 1L;

  @Setup(Level.Trial)
  public void setup() {
    CategoriaRepository repository = mock(CategoriaRepository.class);

    testCategoria = new Categoria();
    testCategoria.setId(existingId);
    testCategoria.setNome("Natura");
    testCategoria.setDescrizione("Attività all'aria aperta");

    when(repository.save(testCategoria)).thenReturn(testCategoria);
    when(repository.findById(existingId)).thenReturn(Optional.of(testCategoria));

    service = new CategoriaServiceImpl(repository);
  }

  @Benchmark
  public void benchmarkSaveCategoria(Blackhole bh) throws Exception {
    Categoria result = service.saveCategoria(testCategoria);
    bh.consume(result);
  }

  @Benchmark
  public void benchmarkFindById(Blackhole bh) throws Exception {
    Categoria result = service.findById(existingId);
    bh.consume(result);
  }
}
