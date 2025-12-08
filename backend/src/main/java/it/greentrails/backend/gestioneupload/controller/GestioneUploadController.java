package it.greentrails.backend.gestioneupload.controller;

import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.enums.RuoloUtente;
import it.greentrails.backend.gestioneattivita.repository.AttivitaRepository;
import it.greentrails.backend.gestioneattivita.repository.RecensioneRepository;
import it.greentrails.backend.gestionesegnalazioni.repository.SegnalazioniRepository;
import it.greentrails.backend.gestioneupload.exceptions.FileNonTrovatoException;
import it.greentrails.backend.gestioneupload.service.ArchiviazioneService;
import it.greentrails.backend.utils.service.ResponseGenerator;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("api/file")
@RequiredArgsConstructor
public class GestioneUploadController {

  private final ArchiviazioneService archiviazioneService;
  private final AttivitaRepository attivitaRepository;
  private final RecensioneRepository recensioneRepository;
  private final SegnalazioniRepository segnalazioniRepository;

  @GetMapping("{media:.+}")
  public ResponseEntity<Object> elencaFileCaricati(@PathVariable final String media) {
    return ResponseGenerator.generateResponse(HttpStatus.OK, archiviazioneService.loadAll(media));
  }

  @GetMapping("{media:.+}/{filename:.+}")
  @ResponseBody
  public ResponseEntity<Resource> serviFile(
      @PathVariable final String media,
      @PathVariable final String filename) {

    Resource file = archiviazioneService.loadAsResource(media, filename);

    if (file == null) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + file.getFilename() + "\"")
        .contentType(MediaType.APPLICATION_OCTET_STREAM).body(file);
  }

  @PostMapping
  public ResponseEntity<Object> gestisciUploadFile(
      @AuthenticationPrincipal Utente utente,
      @RequestParam("media") String media,
      @RequestParam("file") MultipartFile file) {
    AtomicBoolean auth = new AtomicBoolean(utente.getRuolo() == RuoloUtente.AMMINISTRATORE);
    if (!auth.get()) {
      attivitaRepository.findOneByMedia(media).ifPresentOrElse(
          a -> auth.set(a.getGestore().getId().equals(utente.getId())),
          () -> recensioneRepository.findOneByMedia(media).ifPresentOrElse(
              r -> auth.set(r.getVisitatore().getId().equals(utente.getId())),
              () -> segnalazioniRepository.findOneByMedia(media).ifPresent(
                  s -> auth.set(s.getUtente().getId().equals(utente.getId()))
              )
          )
      );
    }
    if (!auth.get()) {
      return ResponseGenerator.generateResponse(HttpStatus.FORBIDDEN,
          "Impossibile caricare un file qui.");
    }
    archiviazioneService.store(media, file);
    return ResponseGenerator.generateResponse(HttpStatus.OK, "File caricato");
  }

  @ExceptionHandler(FileNonTrovatoException.class)
  public ResponseEntity<?> handleStorageFileNotFound(FileNonTrovatoException exc) {
    return ResponseEntity.notFound().build();
  }


  @DeleteMapping("{media:.+}/{filename:.+}")
  private ResponseEntity<Object> cancellaMedia(
      @AuthenticationPrincipal Utente utente,
      @PathVariable final String media,
      @PathVariable final String filename
  ) {
    AtomicBoolean auth = new AtomicBoolean(utente.getRuolo() == RuoloUtente.AMMINISTRATORE);
    if (!auth.get()) {
      attivitaRepository.findOneByMedia(media).ifPresentOrElse(
          a -> auth.set(a.getGestore().getId().equals(utente.getId())),
          () -> recensioneRepository.findOneByMedia(media).ifPresentOrElse(
              r -> auth.set(r.getVisitatore().getId().equals(utente.getId())),
              () -> segnalazioniRepository.findOneByMedia(media).ifPresent(
                  s -> auth.set(s.getUtente().getId().equals(utente.getId()))
              )
          )
      );
    }
    if (!auth.get()) {
      return ResponseGenerator.generateResponse(HttpStatus.FORBIDDEN,
          "Impossibile eliminare il file.");
    }
    archiviazioneService.delete(media, filename);
    return ResponseGenerator.generateResponse(HttpStatus.OK, "File eliminato");
  }

}