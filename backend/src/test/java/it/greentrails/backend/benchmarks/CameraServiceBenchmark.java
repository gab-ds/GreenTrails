package it.greentrails.backend.benchmarks;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Camera;
import it.greentrails.backend.gestioneattivita.repository.CameraRepository;
import it.greentrails.backend.gestioneattivita.service.CameraService;
import it.greentrails.backend.gestioneattivita.service.CameraServiceImpl;
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
public class CameraServiceBenchmark {

    @Param({"1000", "10000", "50000"})
    private int listSize;

    private CameraService service;
    private Attivita targetAlloggio;

    @Setup(Level.Trial)
    public void setup() {
        CameraRepository repository = mock(CameraRepository.class);
        List<Camera> mockData = generateMockData(listSize);
        when(repository.findAll()).thenReturn(mockData);

        service = new CameraServiceImpl(repository);
    }

    @Benchmark
    public void benchmarkGetCamereByAlloggio(Blackhole bh) throws Exception {
        List<Camera> result = service.getCamereByAlloggio(targetAlloggio);
        bh.consume(result);
    }

    private List<Camera> generateMockData(int size) {
        List<Camera> data = new ArrayList<>(size);
        Random random = new Random(42);

        targetAlloggio = new Attivita();
        targetAlloggio.setId(1L);
        targetAlloggio.setAlloggio(true);

        Attivita otherAlloggio = new Attivita();
        otherAlloggio.setId(2L);
        otherAlloggio.setAlloggio(true);

        for (int i = 0; i < size; i++) {
            Camera c = new Camera();
            c.setId((long) i);
            // 10% chance to belong to target Alloggio
            if (random.nextDouble() < 0.1) {
                c.setAlloggio(targetAlloggio);
            } else {
                c.setAlloggio(otherAlloggio);
            }
            data.add(c);
        }
        return data;
    }
}

