package it.greentrails.backend.gestioneupload.exceptions;

public class FileNonTrovatoException extends ArchiviazioneException {

  public FileNonTrovatoException(String message) {
    super(message);
  }

  public FileNonTrovatoException(String message, Throwable cause) {
    super(message, cause);
  }
}
