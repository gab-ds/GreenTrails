package it.greentrails.backend.gestioneupload.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import it.greentrails.backend.gestioneupload.exceptions.ArchiviazioneException;
import it.greentrails.backend.gestioneupload.exceptions.FileNonTrovatoException;
import it.greentrails.backend.utils.ArchiviazioneProperties;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

class ArchiviazioneFileSystemServiceTest {

  private Path tempRoot;
  private ArchiviazioneFileSystemService service;

  @BeforeEach
  void setUp() throws IOException {
    tempRoot = Files.createTempDirectory("archiviazione-test");
    ArchiviazioneProperties props = new ArchiviazioneProperties();
    props.setLocation(tempRoot.toString());
    service = new ArchiviazioneFileSystemService(props);
    service.init();
  }

  @AfterEach
  void tearDown() throws IOException {
    if (Files.exists(tempRoot)) {
      try (var stream = Files.walk(tempRoot)) {
        stream.sorted(Comparator.reverseOrder())
            .forEach(p -> {
              try {
                Files.deleteIfExists(p);
              } catch (IOException ignored) {
              }
            });
      }
    }
  }

  @Test
  void constructorEmptyLocationThrows() {
    ArchiviazioneProperties p = new ArchiviazioneProperties();
    p.setLocation("    ");
    assertThrows(ArchiviazioneException.class, () -> new ArchiviazioneFileSystemService(p));
  }

  @Test
  void initCreatesDirectory() throws IOException {
    Path newRoot = tempRoot.resolve("newroot");
    ArchiviazioneProperties p = new ArchiviazioneProperties();
    p.setLocation(newRoot.toString());
    ArchiviazioneFileSystemService svc = new ArchiviazioneFileSystemService(p);
    svc.init();
    assertTrue(Files.exists(newRoot));
    assertTrue(Files.isDirectory(newRoot));
    Files.deleteIfExists(newRoot);
  }

  @Test
  void storeAndLoadAllAndLoadAsResourceAndDelete() throws Exception {
    MultipartFile file = Mockito.mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getContentType()).thenReturn("image/jpeg");
    when(file.getOriginalFilename()).thenReturn("foto.jpg");
    byte[] data = "hello".getBytes();
    Mockito.doReturn(new ByteArrayInputStream(data)).when(file).getInputStream();

    String media = "media1";
    service.store(media, file);

    Path mediaDir = tempRoot.resolve(media);
    assertTrue(Files.exists(mediaDir) && Files.isDirectory(mediaDir));
    List<String> all = service.loadAll(media);
    assertNotNull(all);
    assertFalse(all.isEmpty());
    String filename = all.getFirst();
    assertTrue(filename.endsWith(".jpg"));

    Resource r = service.loadAsResource(media, filename);
    assertNotNull(r);
    assertTrue(r.exists());
    assertTrue(r.isReadable());

    service.delete(media, filename);
    assertFalse(Files.exists(mediaDir.resolve(filename)));
  }

  @Test
  void storeEmptyFileThrows() {
    MultipartFile file = Mockito.mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(true);
    assertThrows(ArchiviazioneException.class, () -> service.store("m", file));
  }

  @Test
  void storeInvalidContentTypeThrows() {
    MultipartFile file = Mockito.mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getContentType()).thenReturn("text/plain");
    when(file.getOriginalFilename()).thenReturn("file.txt");
    assertThrows(ArchiviazioneException.class, () -> service.store("m", file));
  }

  @Test
  void storeDisallowedExtensionThrowsRceProtection() {
    MultipartFile file = Mockito.mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getContentType()).thenReturn("image/jpeg");
    when(file.getOriginalFilename()).thenReturn("exploit.php");

    assertThrows(ArchiviazioneException.class, () -> service.store("m", file));
  }

  @Test
  void loadAsResourceNotFoundThrows() {
    assertThrows(FileNonTrovatoException.class, () -> service.loadAsResource("m", "nofile.jpg"));
  }

  @Test
  void deletePathTraversalThrows() throws IOException {
    String media = "m2";
    Path mediaDir = tempRoot.resolve(media);
    Files.createDirectories(mediaDir);
    assertThrows(ArchiviazioneException.class, () -> service.delete(media, "../escape.txt"));
  }

  @Test
  void deleteNonExistingDoesNotThrow() {
    service.delete("no_media", "nofile.jpg");
  }

  @Test
  void deleteAllRemovesRoot() throws IOException {
    Path mediaDir = tempRoot.resolve("m3");
    Files.createDirectories(mediaDir);
    Path f = mediaDir.resolve("a.txt");
    Files.write(f, "x".getBytes());
    assertTrue(Files.exists(f));

    service.deleteAll();
    assertFalse(Files.exists(tempRoot));
  }

  @Test
  void storeWhenDirectoryAlreadyExists() throws Exception {
    String media = "existsMedia";
    Path mediaDir = tempRoot.resolve(media);
    Files.createDirectories(mediaDir);

    MultipartFile file = Mockito.mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getContentType()).thenReturn("image/png");
    when(file.getOriginalFilename()).thenReturn("img.png");
    Mockito.doReturn(new ByteArrayInputStream("x".getBytes())).when(file).getInputStream();

    service.store(media, file);

    List<String> all = service.loadAll(media);
    assertFalse(all.isEmpty());
  }

  @Test
  void storeWithMediaPathTraversalThrows() throws Exception {
    MultipartFile file = Mockito.mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getContentType()).thenReturn("image/jpeg");
    when(file.getOriginalFilename()).thenReturn("foto.jpg");
    Mockito.doReturn(new ByteArrayInputStream("x".getBytes())).when(file).getInputStream();

    assertThrows(ArchiviazioneException.class, () -> service.store("../escape", file));
  }

  @Test
  void loadAsResourceExistsButNotReadableThrows() throws Exception {
    String media = "nr";
    Path mediaDir = tempRoot.resolve(media);
    Files.createDirectories(mediaDir);
    Path f = mediaDir.resolve("secret.jpg");
    Files.write(f, "data".getBytes());

    try {
      Set<PosixFilePermission> perms = PosixFilePermissions.fromString("-wx------");
      Files.setPosixFilePermissions(f, perms);

      if (!Files.isReadable(f)) {
        assertThrows(FileNonTrovatoException.class, () -> service.loadAsResource(media, "secret.jpg"));
      }
    } catch (UnsupportedOperationException | IOException ignored) {
    }
  }

  @Test
  void storeInputStreamThrowsIOException() throws Exception {
    MultipartFile file = Mockito.mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getContentType()).thenReturn("image/jpeg");
    when(file.getOriginalFilename()).thenReturn("bad.jpg");
    Mockito.doThrow(new IOException("boom")).when(file).getInputStream();

    assertThrows(ArchiviazioneException.class, () -> service.store("mio", file));
  }

  @Test
  void loadAllNonExistingReturnsEmptyList() {
    List<String> files = service.loadAll("non_esiste");
    assertNotNull(files);
    assertTrue(files.isEmpty());
  }
}
