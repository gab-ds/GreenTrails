package it.greentrails.backend.benchmarks;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.greentrails.backend.entities.ValoriEcosostenibilita;
import it.greentrails.backend.gestioneattivita.repository.ValoriEcosostenibilitaRepository;
import it.greentrails.backend.gestioneattivita.service.ValoriEcosostenibilitaService;
import it.greentrails.backend.gestioneattivita.service.ValoriEcosostenibilitaServiceImpl;
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
public class ValoriEcosostenibilitaServiceImplBenchmark {

  private ValoriEcosostenibilitaService service;
  private ValoriEcosostenibilita testValori;
  private long existingId = 1L;

  @Setup(Level.Trial)
  public void setup() {
    ValoriEcosostenibilitaRepository repository = mock(ValoriEcosostenibilitaRepository.class);

    testValori = new ValoriEcosostenibilita();
    testValori.setId(existingId);
    testValori.setPoliticheAntispreco(true);
    testValori.setProdottiLocali(true);
    testValori.setEnergiaVerde(true);
    testValori.setRaccoltaDifferenziata(true);
    testValori.setLimiteEmissioneCO2(false);
    testValori.setContattoConNatura(true);

    when(repository.save(testValori)).thenReturn(testValori);
    when(repository.findById(existingId)).thenReturn(Optional.of(testValori));

    service = new ValoriEcosostenibilitaServiceImpl(repository);
  }

  @Benchmark
  public void benchmarkSaveValori(Blackhole bh) throws Exception {
    ValoriEcosostenibilita result = service.saveValori(testValori);
    bh.consume(result);
  }

  @Benchmark
  public void benchmarkFindById(Blackhole bh) throws Exception {
    ValoriEcosostenibilita result = service.findById(existingId);
    bh.consume(result);
  }
}
