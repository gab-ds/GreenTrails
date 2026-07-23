package it.greentrails.backend.benchmarks;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.greentrails.backend.entities.Recensione;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.gestioneattivita.repository.RecensioneRepository;
import it.greentrails.backend.gestioneattivita.service.RecensioneService;
import it.greentrails.backend.gestioneattivita.service.RecensioneServiceImpl;
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
import org.openjdk.jmh.annotations.DynamicHalt;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class RecensioneServiceBenchmark {

    @Param({"1000", "10000", "50000"})
    private int listSize;

    private RecensioneService service;
    private Utente targetUtente;

    @Setup(Level.Trial)
    public void setup() {
        RecensioneRepository repository = mock(RecensioneRepository.class);
        List<Recensione> mockData = generateMockData(listSize);
        when(repository.findAll()).thenReturn(mockData);

        service = new RecensioneServiceImpl(repository);
    }

    @Benchmark
    public void benchmarkFiltroRecensioni(Blackhole bh) throws Exception {
        List<Recensione> result = service.getAllRecensioniByVisitatore(targetUtente);
        bh.consume(result);
    }

    private List<Recensione> generateMockData(int size) {
        List<Recensione> data = new ArrayList<>(size);
        Random random = new Random(42);

        targetUtente = new Utente();
        targetUtente.setId(1L);

        Utente otherUtente = new Utente();
        otherUtente.setId(2L);

        for (int i = 0; i < size; i++) {
            Recensione r = new Recensione();
            r.setId((long) i);
            // 10% di probabilità che sia dell'utente target
            if (random.nextDouble() < 0.1) {
                r.setVisitatore(targetUtente);
            } else {
                r.setVisitatore(otherUtente);
            }
            data.add(r);
        }
        return data;
    }
}

