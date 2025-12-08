package it.greentrails.backend.gestioneupload.service;

import java.nio.file.Path;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ArchiviazioneService {

  void init();


  void store(String media, MultipartFile file);

  List<String> loadAll(String media);

  Path load(String media, String filename);

  Resource loadAsResource(String media, String filename);

  void delete(String media, String filename);

  void deleteAll();

}
