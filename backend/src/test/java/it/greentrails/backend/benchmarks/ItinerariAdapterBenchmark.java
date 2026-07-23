package it.greentrails.backend.benchmarks;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Camera;
import it.greentrails.backend.entities.Itinerario;
import it.greentrails.backend.entities.Preferenze;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.gestioneattivita.repository.AttivitaRepository;
import it.greentrails.backend.gestioneattivita.repository.CameraRepository;
import it.greentrails.backend.gestioneitinerari.adapter.ItinerariAdapter;
import it.greentrails.backend.gestioneitinerari.adapter.ItinerariStubAdapter;
import it.greentrails.backend.gestioneitinerari.repository.ItinerariRepository;
import it.greentrails.backend.gestioneprenotazioni.repository.PrenotazioneAlloggioRepository;
import it.greentrails.backend.gestioneprenotazioni.repository.PrenotazioneAttivitaTuristicaRepository;
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
@OutputTimeUnit(TimeUnit.MILLISECONDS) // Milliseconds because shuffle is heavy
@Fork(1)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class ItinerariAdapterBenchmark {

    @Param({"100", "1000", "5000", "10000"})
    private int listSize;

    private ItinerariAdapter adapter;
    private Preferenze dummyPreferenze;

    @Setup(Level.Trial)
    public void setup() {
        AttivitaRepository attivitaRepo = mock(AttivitaRepository.class);
        CameraRepository cameraRepo = mock(CameraRepository.class);
        ItinerariRepository itinerariRepo = mock(ItinerariRepository.class);
        PrenotazioneAlloggioRepository prenAlloggioRepo = mock(PrenotazioneAlloggioRepository.class);
        PrenotazioneAttivitaTuristicaRepository prenAttivitaRepo = mock(PrenotazioneAttivitaTuristicaRepository.class);

        // Prepare Mock Data
        List<Attivita> mockAttivita = generateAttivita(listSize);
        List<Camera> mockCamere = generateCamere(listSize);

        when(attivitaRepo.findAll()).thenReturn(mockAttivita);
        when(cameraRepo.findAll()).thenReturn(mockCamere);
        
        // Mock save to return input or dummy
        when(itinerariRepo.save(any(Itinerario.class))).thenAnswer(i -> {
            Itinerario it = i.getArgument(0);
            it.setId(99L);
            return it;
        });

        // Initialize Adapter
        adapter = new ItinerariStubAdapter(
            attivitaRepo, 
            cameraRepo, 
            itinerariRepo, 
            prenAlloggioRepo, 
            prenAttivitaRepo
        );

        // Dummy Preferenze
        dummyPreferenze = new Preferenze();
        Utente u = new Utente();
        u.setId(1L);
        dummyPreferenze.setVisitatore(u);
    }

    @Benchmark
    public void benchmarkPianificazione(Blackhole bh) {
        Itinerario result = adapter.pianificazioneAutomatica(dummyPreferenze);
        bh.consume(result);
    }

    private List<Attivita> generateAttivita(int size) {
        List<Attivita> list = new ArrayList<>(size);
        Random r = new Random(42);
        for (int i = 0; i < size; i++) {
            Attivita a = new Attivita();
            a.setId((long) i);
            a.setPrezzo(10.0 + r.nextDouble() * 100);
            // Mix of Alloggio (true) and Attivita Turistica (false)
            // Adapter filters for !isAlloggio
            a.setAlloggio(r.nextBoolean()); 
            list.add(a);
        }
        return list;
    }

    private List<Camera> generateCamere(int size) {
        List<Camera> list = new ArrayList<>(size);
        Random r = new Random(42);
        for (int i = 0; i < size; i++) {
            Camera c = new Camera();
            c.setId((long) i);
            c.setPrezzo(50.0 + r.nextDouble() * 200);
            list.add(c);
        }
        return list;
    }
}

