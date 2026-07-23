package it.greentrails.backend.benchmarks;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.greentrails.backend.entities.PrenotazioneAttivitaTuristica;
import it.greentrails.backend.enums.StatoPrenotazione;
import it.greentrails.backend.gestioneprenotazioni.repository.PrenotazioneAttivitaTuristicaRepository;
import it.greentrails.backend.gestioneprenotazioni.service.PrenotazioneAttivitaTuristicaService;
import it.greentrails.backend.gestioneprenotazioni.service.PrenotazioneAttivitaTuristicaServiceImpl;
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
public class PrenotazioneAttivitaTuristicaBenchmark {

    @Param({"1000", "10000", "50000"})
    private int listSize;

    private PrenotazioneAttivitaTuristicaService service;

    @Setup(Level.Trial)
    public void setup() {
        PrenotazioneAttivitaTuristicaRepository repository = mock(PrenotazioneAttivitaTuristicaRepository.class);
        
        List<PrenotazioneAttivitaTuristica> mockData = generateMockData(listSize);
        when(repository.findAll()).thenReturn(mockData);

        service = new PrenotazioneAttivitaTuristicaServiceImpl(repository);
    }

    @Benchmark
    public void benchmarkFiltroStato(Blackhole bh) throws Exception {
        // Filtriamo per CREATA
        List<PrenotazioneAttivitaTuristica> result = service.getPrenotazioniAttivitaTuristicaByStato(StatoPrenotazione.CREATA);
        bh.consume(result);
    }

    private List<PrenotazioneAttivitaTuristica> generateMockData(int size) {
        List<PrenotazioneAttivitaTuristica> data = new ArrayList<>(size);
        Random random = new Random(42);
        StatoPrenotazione[] stati = StatoPrenotazione.values();

        for (int i = 0; i < size; i++) {
            PrenotazioneAttivitaTuristica p = new PrenotazioneAttivitaTuristica();
            p.setId((long) i);
            // Assegna uno stato random
            p.setStato(stati[random.nextInt(stati.length)]);
            data.add(p);
        }
        return data;
    }
}

