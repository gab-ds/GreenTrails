package it.greentrails.backend.gestioneupload.exceptions;

public class ArchiviazioneException extends RuntimeException {

  public ArchiviazioneException(String message) {
    super(message);
  }

  public ArchiviazioneException(String message, Throwable cause) {
    super(message, cause);
  }
}
