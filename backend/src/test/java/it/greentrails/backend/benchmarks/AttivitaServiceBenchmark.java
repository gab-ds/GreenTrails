package it.greentrails.backend.benchmarks;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.gestioneattivita.repository.AttivitaRepository;
import it.greentrails.backend.gestioneattivita.service.AttivitaService;
import it.greentrails.backend.gestioneattivita.service.AttivitaServiceImpl;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class AttivitaServiceBenchmark {

    @Param({"10", "50", "100"})
    private int listSize;

    private AttivitaService service;
    private Long idGestore = 1L;

    @Setup(Level.Trial)
    public void setup() {
        AttivitaRepository repository = mock(AttivitaRepository.class);
        List<Attivita> mockData = generateMockData(listSize);
        Page<Attivita> mockPage = new PageImpl<>(mockData);

        // Mock generic calls
        when(repository.findByGestore(anyLong(), any(Pageable.class))).thenReturn(mockPage);
        when(repository.getAllByPrezzo(any(Pageable.class))).thenReturn(mockPage);
        when(repository.getAttivitaTuristiche(any(Pageable.class))).thenReturn(mockPage);

        service = new AttivitaServiceImpl(repository);
    }

    @Benchmark
    public void benchmarkFindAllAttivitaByGestore(Blackhole bh) throws Exception {
        List<Attivita> result = service.findAllAttivitaByGestore(idGestore);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkGetAttivitaTuristicheEconomiche(Blackhole bh) {
        List<Attivita> result = service.getAttivitaTuristicheEconomiche(listSize);
        bh.consume(result);
    }

    private List<Attivita> generateMockData(int size) {
        List<Attivita> data = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Attivita a = new Attivita();
            a.setId((long) i);
            a.setPrezzo((double) i * 10);
            data.add(a);
        }
        return data;
    }
}

