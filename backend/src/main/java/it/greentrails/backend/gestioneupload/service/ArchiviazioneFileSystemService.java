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
import java.util.Arrays;
import java.util.List;
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
  private static final String[] ALLOWED_CONTENT_TYPES = {"image/jpeg", "image/png", "video/mp4"};

  @Autowired
  public ArchiviazioneFileSystemService(ArchiviazioneProperties properties) {

    if (properties.getLocation().trim().isEmpty()) {
      throw new ArchiviazioneException("Il percorso di upload è vuoto.");
    }

    this.rootLocation = Paths.get(properties.getLocation());
  }

  @Override
  public void store(String media, MultipartFile file) {
    try {
      if (file.isEmpty()) {
        throw new ArchiviazioneException("Il file è vuoto.");
      }
      if (!Arrays.asList(ALLOWED_CONTENT_TYPES).contains(file.getContentType())) {
        throw new ArchiviazioneException("Il formato del file non è valido.");
      }
      Path destinationDir = this.rootLocation.resolve(media);
      if (!destinationDir.toFile().exists()) {
        destinationDir.toFile().mkdir();
      }
      String filename =
          (System.currentTimeMillis() / 1000L) + "." + StringUtils.getFilenameExtension(
              file.getOriginalFilename());
      Path destinationFile = destinationDir.resolve(
              Paths.get(filename))
          .normalize().toAbsolutePath();
      if (!destinationFile.getParent().equals(destinationDir.toAbsolutePath())) {
        throw new ArchiviazioneException(
            "Impossibile salvare al di fuori della cartella di upload.");
      }
      try (InputStream inputStream = file.getInputStream()) {
        Files.copy(inputStream, destinationFile,
            StandardCopyOption.REPLACE_EXISTING);
      }
    } catch (IOException e) {
      throw new ArchiviazioneException("Impossibile salvare il file.", e);
    }
  }

  @Override
  public List<String> loadAll(String media) {
    try {
      Path mediaDir = this.rootLocation.resolve(media);
      return Files.walk(mediaDir, 1)
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
    return rootLocation.resolve(media).resolve(filename);
  }

  @Override
  public Resource loadAsResource(String media, String filename) {
    try {
      Path file = load(media, filename);
      Resource resource = new UrlResource(file.toUri());
      if (resource.exists() || resource.isReadable()) {
        return resource;
      } else {
        throw new FileNonTrovatoException(
            "Impossibile trovare il file: " + filename);

      }
    } catch (MalformedURLException e) {
      throw new FileNonTrovatoException("Impossibile trovare il file: " + filename, e);
    }
  }

  @Override
  public void delete(String media, String filename) {
    Path destinationDir = this.rootLocation.resolve(media);
    Path file = destinationDir.resolve(filename);
    if (!file.getParent().equals(destinationDir.toAbsolutePath())) {
      throw new ArchiviazioneException(
          "Impossibile eliminare al di fuori della cartella di upload.");
    }
    if (Files.exists(file)) {
      try {
        Files.delete(file);
      } catch (IOException e) {
        throw new ArchiviazioneException("Impossibile eliminare il file: " + filename, e);
      }
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
}
