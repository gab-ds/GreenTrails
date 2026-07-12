package it.greentrails.backend.gestioneitinerari.adapter;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Camera;
import it.greentrails.backend.entities.Itinerario;
import it.greentrails.backend.entities.Preferenze;
import it.greentrails.backend.entities.PrenotazioneAlloggio;
import it.greentrails.backend.entities.PrenotazioneAttivitaTuristica;
import it.greentrails.backend.gestioneattivita.repository.AttivitaRepository;
import it.greentrails.backend.gestioneattivita.repository.CameraRepository;
import it.greentrails.backend.gestioneitinerari.repository.ItinerariRepository;
import it.greentrails.backend.gestioneprenotazioni.repository.PrenotazioneAlloggioRepository;
import it.greentrails.backend.gestioneprenotazioni.repository.PrenotazioneAttivitaTuristicaRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
/*@ nullable_by_default @*/
public class ItinerariStubAdapter implements ItinerariAdapter {

  /*@ spec_public @*/
  private final AttivitaRepository attivitaRepository;
  /*@ spec_public @*/
  private final CameraRepository cameraRepository;
  /*@ spec_public @*/
  private final ItinerariRepository itinerariRepository;
  /*@ spec_public @*/
  private final PrenotazioneAlloggioRepository prenotazioneAlloggioRepository;
  /*@ spec_public @*/
  private final PrenotazioneAttivitaTuristicaRepository prenotazioneAttivitaTuristicaRepository;

  //@ public invariant attivitaRepository != null;
  //@ public invariant cameraRepository != null;
  //@ public invariant itinerariRepository != null;
  //@ public invariant prenotazioneAlloggioRepository != null;
  //@ public invariant prenotazioneAttivitaTuristicaRepository != null;


  /*@
    @ also
    @ requires preferenze != null;
    @ ensures \result != null;
    @*/
  @Override
  public Itinerario pianificazioneAutomatica(Preferenze preferenze) {
    Itinerario itinerario = new Itinerario();
    itinerario.setVisitatore(preferenze.getVisitatore());
    Itinerario itinerarioFinal = itinerariRepository.save(itinerario);
    List<Attivita> attivitaTuristiche = attivitaRepository.findAll();
    Collections.shuffle(attivitaTuristiche);
    attivitaTuristiche.stream().filter(a -> !a.isAlloggio()).limit(3).forEach(a -> {
      PrenotazioneAttivitaTuristica p = new PrenotazioneAttivitaTuristica();
      p.setAttivitaTuristica(a);
      p.setItinerario(itinerarioFinal);
      p.setDataInizio(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)));
      p.setNumAdulti(1);
      p.setNumBambini(0);
      p.setPrezzo(a.getPrezzo());
      PrenotazioneAttivitaTuristica saved = prenotazioneAttivitaTuristicaRepository.save(p);
    });
    List<Camera> camere = cameraRepository.findAll();
    Collections.shuffle(camere);
    camere.stream().limit(1).forEach(c -> {
      PrenotazioneAlloggio p = new PrenotazioneAlloggio();
      p.setCamera(c);
      p.setItinerario(itinerarioFinal);
      p.setDataInizio(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)));
      p.setDataFine(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
      p.setNumAdulti(1);
      p.setNumBambini(0);
      p.setNumCamere(1);
      p.setPrezzo(c.getPrezzo());
      PrenotazioneAlloggio saved = prenotazioneAlloggioRepository.save(p);
    });
    return itinerarioFinal;
  }

}
