package it.greentrails.backend.gestioneupload.service;

import java.nio.file.Path;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/*@ nullable_by_default @*/
public interface ArchiviazioneService {

  void init();

  void store(/*@ nullable @*/ String media, /*@ nullable @*/ MultipartFile file);

  /*@ ensures \result != null; @*/
  List<String> loadAll(/*@ nullable @*/ String media);

  Path load(/*@ nullable @*/ String media, /*@ nullable @*/ String filename);

  /*@
    @ ensures \result != null;
    @*/
  Resource loadAsResource(/*@ nullable @*/ String media, /*@ nullable @*/ String filename);

  void delete(/*@ nullable @*/ String media, /*@ nullable @*/ String filename);

  void deleteAll();

}
