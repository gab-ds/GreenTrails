package it.greentrails.backend.benchmarks;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.gestioneattivita.repository.AttivitaRepository;
import it.greentrails.backend.gestionericerca.service.RicercaService;
import it.greentrails.backend.gestionericerca.service.RicercaServiceImpl;
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
import org.springframework.data.geo.Point;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class RicercaServiceBenchmark {

    @Param({"100", "1000", "5000", "10000"})
    private int listSize;

    private RicercaService service;
    private Point targetPoint;

    @Setup(Level.Trial)
    public void setup() {
        AttivitaRepository repository = mock(AttivitaRepository.class);
        List<Attivita> mockData = generateMockData(listSize);
        when(repository.findAll()).thenReturn(mockData);

        service = new RicercaServiceImpl(repository);
        targetPoint = new Point(41.9028, 12.4964); // Roma
    }

    @Benchmark
    public void benchmarkFindAttivitaByPosizione(Blackhole bh) {
        List<Attivita> result = service.findAttivitaByPosizione(targetPoint, 10.0); // 10 km radius
        bh.consume(result);
    }

    private List<Attivita> generateMockData(int size) {
        List<Attivita> data = new ArrayList<>(size);
        Random random = new Random(42); // Seed fisso per riproducibilità

        for (int i = 0; i < size; i++) {
            Attivita a = new Attivita();
            a.setId((long) i);
            // Genera coordinate attorno a Roma (o sparse)
            // +/- 1 grado latitudine/longitudine (ca. 111km)
            double lat = 41.9028 + (random.nextDouble() * 2 - 1);
            double lon = 12.4964 + (random.nextDouble() * 2 - 1);
            a.setCoordinate(new Point(lat, lon));
            data.add(a);
        }
        return data;
    }

}

