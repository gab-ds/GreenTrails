package it.greentrails.backend.gestioneupload.service;

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
    // pulisco la directory temporanea
    if (Files.exists(tempRoot)) {
      // ricorsivamente delete
      Files.walk(tempRoot)
          .sorted(Comparator.reverseOrder())
          .forEach(p -> {
            try {
              Files.deleteIfExists(p);
            } catch (IOException e) {
              // ignore
            }
          });
    }
  }

  @Test
  void constructorEmptyLocationThrows() {
    ArchiviazioneProperties p = new ArchiviazioneProperties();
    p.setLocation("   ");
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
    // cleanup
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

    // dopo lo store deve esserci un file nella directory media1
    Path mediaDir = tempRoot.resolve(media);
    assertTrue(Files.exists(mediaDir) && Files.isDirectory(mediaDir));
    List<String> all = service.loadAll(media);
    assertNotNull(all);
    assertFalse(all.isEmpty());
    String filename = all.getFirst();
    assertTrue(filename.endsWith(".jpg"));

    // loadAsResource
    Resource r = service.loadAsResource(media, filename);
    assertNotNull(r);
    assertTrue(r.exists());
    assertTrue(r.isReadable());

    // delete
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
  void loadAsResourceNotFoundThrows() {
    assertThrows(FileNonTrovatoException.class, () -> service.loadAsResource("m", "nofile.txt"));
  }

  @Test
  void deletePathTraversalThrows() throws IOException {
    String media = "m2";
    // creo la directory media
    Path mediaDir = tempRoot.resolve(media);
    Files.createDirectories(mediaDir);
    // provo a eliminare con filename che risale fuori
    assertThrows(ArchiviazioneException.class, () -> service.delete(media, "../escape.txt"));
  }

  @Test
  void deleteNonExistingDoesNotThrow() {
    // se il file non esiste non deve lanciare eccezioni
    service.delete("no_media", "nofile.jpg");
  }

  @Test
  void deleteAllRemovesRoot() throws IOException {
    // creo una sottodirectory e un file
    Path mediaDir = tempRoot.resolve("m3");
    Files.createDirectories(mediaDir);
    Path f = mediaDir.resolve("a.txt");
    Files.write(f, "x".getBytes());
    assertTrue(Files.exists(f));

    service.deleteAll();
    assertFalse(Files.exists(tempRoot));
  }

  // ----- nuovi test per migliorare la coverage -----

  @Test
  void storeWhenDirectoryAlreadyExists() throws Exception {
    String media = "existsMedia";
    Path mediaDir = tempRoot.resolve(media);
    Files.createDirectories(mediaDir); // directory già esistente -> copre il ramo "exists == true"

    MultipartFile file = Mockito.mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getContentType()).thenReturn("image/png");
    when(file.getOriginalFilename()).thenReturn("img.png");
    Mockito.doReturn(new ByteArrayInputStream("x".getBytes())).when(file).getInputStream();

    // non deve lanciare eccezioni
    service.store(media, file);

    List<String> all = service.loadAll(media);
    assertFalse(all.isEmpty());
  }

  @Test
  void storeWithMediaPathTraversalThrows() throws Exception {
    // se il parametro media contiene componenti che risalgono la gerarchia, deve fallire
    MultipartFile file = Mockito.mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getContentType()).thenReturn("image/jpeg");
    when(file.getOriginalFilename()).thenReturn("foto.jpg");
    Mockito.doReturn(new ByteArrayInputStream("x".getBytes())).when(file).getInputStream();

    assertThrows(ArchiviazioneException.class, () -> service.store("../escape", file));
  }

  @Test
  void loadAsResourceExistsButNotReadable() throws Exception {
    String media = "nr";
    Path mediaDir = tempRoot.resolve(media);
    Files.createDirectories(mediaDir);
    Path f = mediaDir.resolve("secret.jpg");
    Files.write(f, "data".getBytes());

    // proviamo a rimuovere i permessi di lettura (POSIX). Se il file system non supporta POSIX,
    // non falliamo il test: consideriamo il test valido se non possiamo cambiare i permessi.
    try {
      Set<PosixFilePermission> perms = PosixFilePermissions.fromString("-wx------");
      Files.setPosixFilePermissions(f, perms);
    } catch (UnsupportedOperationException | IOException ignored) {
      // filesystem non POSIX (es. Windows nei runner) o errore IO: niente da fare
    }

    // carichiamo la risorsa: se il file esiste ma non è leggibile, UrlResource.exists() può essere true
    // e il metodo dovrebbe ritornare la risorsa (esercitando la combinazione exists==true && isReadable==false)
    Resource r = service.loadAsResource(media, "secret.jpg");
    assertNotNull(r);
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
  void loadAllNonExistingThrows() {
    // se la directory non esiste Files.walk solleverà NoSuchFileException -> deve essere wrap in ArchiviazioneException
    assertThrows(ArchiviazioneException.class, () -> service.loadAll("non_esiste"));
  }

}