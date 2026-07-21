package it.greentrails.backend.benchmarks;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Categoria;
import it.greentrails.backend.gestioneattivita.repository.AttivitaRepository;
import it.greentrails.backend.gestionericerca.service.RicercaService;
import it.greentrails.backend.gestionericerca.service.RicercaServiceImpl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
@DynamicHalt(model = "fcn")
public class RicercaCategorieBenchmark {

    @Param({"100", "1000", "5000"})
    private int listSize; // Dimensione media lista per categoria

    @Param({"2", "3", "5"})
    private int numCategories;

    private RicercaService service;
    private List<Categoria> targetCategories;

    @Setup(Level.Trial)
    public void setup() {
        AttivitaRepository repository = mock(AttivitaRepository.class);
        service = new RicercaServiceImpl(repository);
        
        targetCategories = new ArrayList<>();
        Map<Long, List<Attivita>> mockDatabase = new HashMap<>();
        
        // Generiamo le categorie
        for (int i = 0; i < numCategories; i++) {
            Categoria c = new Categoria();
            c.setId((long) i);
            c.setNome("Categoria " + i);
            targetCategories.add(c);
        }

        // Generiamo le liste di attività per ogni categoria
        // Strategia:
        // Creiamo 'listSize' elementi per ogni categoria.
        // Una frazione (es. 20%) sarÃ  comune a TUTTE le categorie (IDs 0..N*0.2)
        // Il resto saranno ID casuali o specifici per quella categoria per rendere l'intersezione costosa ma non vuota.
        
        int commonCount = (int) (listSize * 0.2); // 20% in comune
        
        for (Categoria cat : targetCategories) {
            List<Attivita> attivitaList = new ArrayList<>(listSize);
            
            // Aggiungi elementi comuni
            for (int k = 0; k < commonCount; k++) {
                Attivita a = new Attivita();
                a.setId((long) k);
                attivitaList.add(a);
            }
            
            // Aggiungi elementi specifici per questa categoria (offset elevato per evitare collisioni non volute)
            long offset = 1000000L + (cat.getId() * listSize);
            for (int k = commonCount; k < listSize; k++) {
                Attivita a = new Attivita();
                a.setId(offset + k);
                attivitaList.add(a);
            }
            
            // Shuffle per rendere things more realistic (e slower contains?)
            Collections.shuffle(attivitaList, new Random(42));
            
            mockDatabase.put(cat.getId(), attivitaList);
        }

        when(repository.findByCategoria(anyLong())).thenAnswer(invocation -> {
            Long catId = invocation.getArgument(0);
            return mockDatabase.getOrDefault(catId, new ArrayList<>());
        });
    }

    @Benchmark
    public void benchmarkIntersezioneCategorie(Blackhole bh) {
        List<Attivita> result = service.findAttivitaByCategorie(targetCategories);
        bh.consume(result);
    }

}

