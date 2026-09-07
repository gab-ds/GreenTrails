package it.greentrails.backend.gestioneupload.service;

import it.greentrails.backend.gestioneupload.exceptions.ArchiviazioneException;
import it.greentrails.backend.gestioneupload.exceptions.FileNonTrovatoException;
import it.greentrails.backend.utils.ArchiviazioneProperties;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ArchiviazioneFileSystemService implements ArchiviazioneService {

  private final Path rootLocation;
  private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "video/mp4");
  private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "mp4");

  @Autowired
  public ArchiviazioneFileSystemService(ArchiviazioneProperties properties) {
    if (properties.getLocation() == null || properties.getLocation().trim().isEmpty()) {
      throw new ArchiviazioneException("Il percorso di upload è vuoto.");
    }
    this.rootLocation = Paths.get(properties.getLocation()).toAbsolutePath().normalize();
  }

  @Override
  public void store(String media, MultipartFile file) {
    try {
      if (file == null || file.isEmpty()) {
        throw new ArchiviazioneException("Il file è vuoto.");
      }

      String extension = validateAndExtractExtension(file);
      Path destinationDir = resolveAndValidate(media, null);
      Files.createDirectories(destinationDir);

      String filename = (System.currentTimeMillis() / 1000L) + "." + extension;
      Path destinationFile = destinationDir.resolve(filename);

      try (InputStream inputStream = file.getInputStream()) {
        Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
      }
    } catch (IOException e) {
      throw new ArchiviazioneException("Impossibile salvare il file.", e);
    }
  }

  @Override
  public List<String> loadAll(String media) {
    Path mediaDir = resolveAndValidate(media, null);
    if (!Files.exists(mediaDir)) {
      return List.of();
    }
    try (Stream<Path> stream = Files.walk(mediaDir, 1)) {
      return stream
          .filter(path -> !path.equals(mediaDir))
          .map(mediaDir::relativize)
          .map(Path::toString)
          .toList();
    } catch (IOException e) {
      throw new ArchiviazioneException("Impossibile leggere i file salvati", e);
    }
  }

  @Override
  public Path load(String media, String filename) {
    return resolveAndValidate(media, filename);
  }

  @Override
  public Resource loadAsResource(String media, String filename) {
    try {
      Path file = load(media, filename);
      Resource resource = new UrlResource(file.toUri());
      if (resource.exists() && resource.isReadable()) {
        return resource;
      }
      throw new FileNonTrovatoException("Impossibile trovare il file: " + filename);
    } catch (MalformedURLException e) {
      throw new FileNonTrovatoException("Impossibile trovare il file: " + filename, e);
    }
  }

  @Override
  public void delete(String media, String filename) {
    Path file = resolveAndValidate(media, filename);
    try {
      Files.deleteIfExists(file);
    } catch (IOException e) {
      throw new ArchiviazioneException("Impossibile eliminare il file: " + filename, e);
    }
  }

  @Override
  public void deleteAll() {
    FileSystemUtils.deleteRecursively(rootLocation.toFile());
  }

  @Override
  public void init() {
    try {
      Files.createDirectories(rootLocation);
    } catch (IOException e) {
      throw new ArchiviazioneException("Impossibile inizializzare l'archiviazione", e);
    }
  }

  private Path resolveAndValidate(String media, String filename) {
    Path mediaDir = this.rootLocation.resolve(media).normalize().toAbsolutePath();

    if (!mediaDir.startsWith(this.rootLocation)) {
      throw new ArchiviazioneException("Accesso negato: contesto media non valido.");
    }

    if (filename == null || filename.isBlank()) {
      return mediaDir;
    }

    Path targetFile = mediaDir.resolve(filename).normalize().toAbsolutePath();

    if (!targetFile.startsWith(mediaDir)) {
      throw new ArchiviazioneException("Accesso negato: percorso file non valido.");
    }

    return targetFile;
  }

  private String validateAndExtractExtension(MultipartFile file) {
    String contentType = file.getContentType();
    if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
      throw new ArchiviazioneException("Il formato del file non è valido.");
    }

    String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
    if (ext == null || !ALLOWED_EXTENSIONS.contains(ext.toLowerCase())) {
      throw new ArchiviazioneException("Estensione del file non supportata.");
    }

    return ext.toLowerCase();
  }
}
