package it.greentrails.backend.benchmarks;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.greentrails.backend.gestioneupload.service.ArchiviazioneFileSystemService;
import it.greentrails.backend.gestioneupload.service.ArchiviazioneService;
import it.greentrails.backend.utils.ArchiviazioneProperties;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
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
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.springframework.web.multipart.MultipartFile;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class ArchiviazioneFileSystemServiceBenchmark {

  private ArchiviazioneService service;
  private Path tempDir;
  private MultipartFile testFile;
  private final String media = "test_media";
  private byte[] fileContent;

  @Setup(Level.Trial)
  public void setup() throws IOException {
    tempDir = Files.createTempDirectory("greentrails-benchmark-");
    ArchiviazioneProperties properties = new ArchiviazioneProperties();
    properties.setLocation(tempDir.toString());
    service = new ArchiviazioneFileSystemService(properties);
    service.init();

    fileContent = ("immagine di test per il benchmark di upload, circa 100 byte.".repeat(5)
        + System.currentTimeMillis()).getBytes();
    testFile = mock(MultipartFile.class);
    when(testFile.isEmpty()).thenReturn(false);
    when(testFile.getContentType()).thenReturn("image/jpeg");
    when(testFile.getOriginalFilename()).thenReturn("test.jpg");
    when(testFile.getInputStream()).thenAnswer(
        inv -> new ByteArrayInputStream(fileContent));

    Files.createDirectories(tempDir.resolve(media));
    Files.writeString(tempDir.resolve(media).resolve("file1.jpg"), "contenuto1");
    Files.writeString(tempDir.resolve(media).resolve("file2.jpg"), "contenuto2");
    Files.writeString(tempDir.resolve(media).resolve("file3.png"), "contenuto3");
  }

  @TearDown(Level.Trial)
  public void tearDown() throws IOException {
    if (tempDir != null && tempDir.toFile().exists()) {
      try (var walk = Files.walk(tempDir)) {
        walk.sorted(java.util.Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(java.io.File::delete);
      }
    }
  }

  @Benchmark
  public void benchmarkStore(Blackhole bh) {
    service.store(media, testFile);
    bh.consume(true);
  }

  @Benchmark
  public void benchmarkLoadAll(Blackhole bh) {
    List<String> files = service.loadAll(media);
    bh.consume(files);
  }

  @Benchmark
  public void benchmarkDelete(Blackhole bh) {
    service.delete(media, "file1.jpg");
    bh.consume(true);
  }
}
