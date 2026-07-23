package it.greentrails.backend.benchmarks;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.gestioneutenze.repository.PreferenzeRepository;
import it.greentrails.backend.gestioneutenze.repository.UtenteRepository;
import it.greentrails.backend.gestioneutenze.service.GestioneUtenzeService;
import it.greentrails.backend.gestioneutenze.service.GestioneUtenzeServiceImpl;
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
import org.openjdk.jmh.annotations.DynamicHalt;
import org.springframework.security.core.userdetails.UserDetails;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class GestioneUtenzeBenchmark {

    private GestioneUtenzeService service;
    private Utente targetUtente;
    private String targetEmail;

    @Setup(Level.Trial)
    public void setup() {
        UtenteRepository utenteRepository = mock(UtenteRepository.class);
        PreferenzeRepository preferenzeRepository = mock(PreferenzeRepository.class);

        targetEmail = "test@greentrails.it";
        targetUtente = new Utente();
        targetUtente.setId(1L);
        targetUtente.setEmail(targetEmail);

        when(utenteRepository.findOneByEmail(targetEmail)).thenReturn(Optional.of(targetUtente));
        when(utenteRepository.findById(1L)).thenReturn(Optional.of(targetUtente));

        service = new GestioneUtenzeServiceImpl(utenteRepository, preferenzeRepository);
    }

    @Benchmark
    public void benchmarkLoadUserByUsername(Blackhole bh) {
        UserDetails userDetails = service.loadUserByUsername(targetEmail);
        bh.consume(userDetails);
    }

    @Benchmark
    public void benchmarkFindById(Blackhole bh) throws Exception {
        Utente utente = service.findById(1L);
        bh.consume(utente);
    }
}
