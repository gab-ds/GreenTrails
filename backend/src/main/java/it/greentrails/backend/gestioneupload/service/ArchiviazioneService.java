package it.greentrails.backend.gestioneupload.service;

import java.nio.file.Path;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/*@ nullable_by_default @*/
public interface ArchiviazioneService {

  void init();

  /*@ requires media != null; requires file != null; @*/
  void store(String media, MultipartFile file);

  /*@ requires media != null; @*/
  /*@ ensures \result != null; @*/
  List<String> loadAll(String media);

  /*@ requires media != null; requires filename != null; @*/
  Path load(String media, String filename);

  /*@
    @ requires media != null;
    @ requires filename != null;
    @ ensures \result != null;
    @*/
  Resource loadAsResource(String media, String filename);

  /*@ requires media != null; requires filename != null; @*/
  void delete(String media, String filename);

  void deleteAll();

}
