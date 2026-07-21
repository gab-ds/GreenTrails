package it.greentrails.backend.benchmarks;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.greentrails.backend.entities.Itinerario;
import it.greentrails.backend.entities.Preferenze;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.enums.RuoloUtente;
import it.greentrails.backend.enums.StatoItinerario;
import it.greentrails.backend.gestioneitinerari.adapter.ItinerariAdapter;
import it.greentrails.backend.gestioneitinerari.repository.ItinerariRepository;
import it.greentrails.backend.gestioneitinerari.service.ItinerariService;
import it.greentrails.backend.gestioneitinerari.service.ItinerariServiceImpl;
import it.greentrails.backend.gestioneprenotazioni.repository.PrenotazioneAlloggioRepository;
import it.greentrails.backend.gestioneprenotazioni.repository.PrenotazioneAttivitaTuristicaRepository;
import it.greentrails.backend.gestioneprenotazioni.service.PrenotazioneAlloggioService;
import it.greentrails.backend.gestioneprenotazioni.service.PrenotazioneAttivitaTuristicaService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
@DynamicHalt(model = "fcn")
public class ItinerariServiceImplBenchmark {

  @Param({"100", "1000", "5000"})
  private int listSize;

  private ItinerariService service;
  private ItinerariRepository repository;
  private Preferenze testPreferenze;
  private Utente testUtente;
  private Itinerario testItinerario;
  private long existingId = 1L;

  @Setup(Level.Trial)
  public void setup() {
    repository = mock(ItinerariRepository.class);
    PrenotazioneAlloggioService prenAlloggioService = mock(PrenotazioneAlloggioService.class);
    PrenotazioneAttivitaTuristicaService prenAttivitaService = mock(
        PrenotazioneAttivitaTuristicaService.class);
    PrenotazioneAlloggioRepository prenAlloggioRepo = mock(PrenotazioneAlloggioRepository.class);
    PrenotazioneAttivitaTuristicaRepository prenAttivitaRepo = mock(
        PrenotazioneAttivitaTuristicaRepository.class);
    ItinerariAdapter adapter = mock(ItinerariAdapter.class);

    testUtente = new Utente();
    testUtente.setId(1L);
    testUtente.setRuolo(RuoloUtente.VISITATORE);

    testItinerario = new Itinerario();
    testItinerario.setId(existingId);
    testItinerario.setVisitatore(testUtente);
    testItinerario.setStato(StatoItinerario.PIANIFICATO);
    testItinerario.setTotale(250.0);

    testPreferenze = new Preferenze();
    testPreferenze.setId(testUtente.getId());
    testPreferenze.setVisitatore(testUtente);

    List<Itinerario> mockItinerari = new ArrayList<>(listSize);
    for (int i = 0; i < listSize; i++) {
      Itinerario it = new Itinerario();
      it.setId((long) i);
      it.setVisitatore(testUtente);
      mockItinerari.add(it);
    }
    Page<Itinerario> mockPage = new PageImpl<>(mockItinerari);

    when(adapter.pianificazioneAutomatica(testPreferenze)).thenReturn(testItinerario);
    when(repository.save(any(Itinerario.class))).thenReturn(testItinerario);
    when(repository.findById(existingId)).thenReturn(Optional.of(testItinerario));
    when(repository.findByVisitatore(anyLong(), any(Pageable.class))).thenReturn(mockPage);

    service = new ItinerariServiceImpl(
        repository, prenAlloggioService, prenAttivitaService,
        prenAlloggioRepo, prenAttivitaRepo, adapter);
  }

  @Benchmark
  public void benchmarkCreateByPreferenze(Blackhole bh) throws Exception {
    Itinerario result = service.createByPreferenze(testPreferenze);
    bh.consume(result);
  }

  @Benchmark
  public void benchmarkFindByUtente(Blackhole bh) throws Exception {
    List<Itinerario> result = service.findItinerariByUtente(testUtente);
    bh.consume(result);
  }

  @Benchmark
  public void benchmarkSaveItinerario(Blackhole bh) throws Exception {
    Itinerario result = service.saveItinerario(testItinerario);
    bh.consume(result);
  }

  @Benchmark
  public void benchmarkFindById(Blackhole bh) throws Exception {
    Itinerario result = service.findById(existingId);
    bh.consume(result);
  }
}
