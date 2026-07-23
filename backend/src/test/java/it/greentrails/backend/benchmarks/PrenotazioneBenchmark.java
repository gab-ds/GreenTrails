package it.greentrails.backend.benchmarks;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.greentrails.backend.entities.PrenotazioneAlloggio;
import it.greentrails.backend.enums.StatoPrenotazione;
import it.greentrails.backend.gestioneattivita.service.CameraService;
import it.greentrails.backend.gestioneprenotazioni.repository.PrenotazioneAlloggioRepository;
import it.greentrails.backend.gestioneprenotazioni.service.PrenotazioneAlloggioService;
import it.greentrails.backend.gestioneprenotazioni.service.PrenotazioneAlloggioServiceImpl;
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
public class PrenotazioneBenchmark {

    @Param({"1000", "10000", "50000"})
    private int listSize;

    private PrenotazioneAlloggioService service;

    @Setup(Level.Trial)
    public void setup() {
        PrenotazioneAlloggioRepository repository = mock(PrenotazioneAlloggioRepository.class);
        CameraService cameraService = mock(CameraService.class);
        
        List<PrenotazioneAlloggio> mockData = generateMockData(listSize);
        when(repository.findAll()).thenReturn(mockData);

        service = new PrenotazioneAlloggioServiceImpl(repository, cameraService);
    }

    @Benchmark
    public void benchmarkFiltroStato(Blackhole bh) throws Exception {
        // Filtriamo per CREATA
        List<PrenotazioneAlloggio> result = service.getPrenotazioniAlloggioByStato(StatoPrenotazione.CREATA);
        bh.consume(result);
    }

    private List<PrenotazioneAlloggio> generateMockData(int size) {
        List<PrenotazioneAlloggio> data = new ArrayList<>(size);
        Random random = new Random(42);
        StatoPrenotazione[] stati = StatoPrenotazione.values();

        for (int i = 0; i < size; i++) {
            PrenotazioneAlloggio p = new PrenotazioneAlloggio();
            p.setId((long) i);
            // Assegna uno stato random
            p.setStato(stati[random.nextInt(stati.length)]);
            data.add(p);
        }
        return data;
    }
}

