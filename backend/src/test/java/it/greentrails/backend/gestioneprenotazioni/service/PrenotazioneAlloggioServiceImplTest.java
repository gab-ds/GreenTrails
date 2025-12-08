package it.greentrails.backend.gestioneprenotazioni.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.gestioneattivita.service.CameraService;
import it.greentrails.backend.gestioneprenotazioni.repository.PrenotazioneAlloggioRepository;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class PrenotazioneAlloggioServiceImplTest {

  @Mock
  private PrenotazioneAlloggioRepository repository;

  @Mock
  private CameraService cameraService;

  @InjectMocks
  private PrenotazioneAlloggioServiceImpl service;

  private final Date dataInizio = new GregorianCalendar(2024, Calendar.MAY, 15)
      .getTime();
  private final Date dataFine = new GregorianCalendar(2024, Calendar.MAY, 18)
      .getTime();

  @BeforeEach
  void setUp() {
  }


  @Test
  void controllaDisponibilitaAlloggioNullAttivita() {
    assertThrows(Exception.class, () ->
        service.controllaDisponibilitaAlloggio(null, dataInizio, dataFine));
  }

  @Test
  void controllaDisponibilitaAlloggioConAttivitaTuristica() {
    Attivita attivita = new Attivita();
    attivita.setId(1L);
    attivita.setAlloggio(false);
    assertThrows(Exception.class, () ->
        service.controllaDisponibilitaAlloggio(attivita, dataInizio, dataFine));
  }

  @Test
  void controllaDisponibilitaAlloggioConDataFinePrecedenteDataInizio() {
    Attivita attivita = new Attivita();
    attivita.setId(1L);
    attivita.setAlloggio(false);
    assertThrows(Exception.class, () ->
        service.controllaDisponibilitaAlloggio(attivita, dataFine, dataInizio));
  }

  @Test
  void controllaDisponibilitaAlloggioSuccess() throws Exception {
    Attivita attivita = new Attivita();
    attivita.setId(1L);
    attivita.setAlloggio(true);

    when(cameraService.getCamereByAlloggio(attivita))
        .thenReturn(List.of());
    when(repository.getPostiOccupatiAlloggioTra(attivita.getId(), dataInizio, dataFine))
        .thenReturn(0);

    assertEquals(0, service.controllaDisponibilitaAlloggio(attivita, dataInizio, dataFine));
  }
}