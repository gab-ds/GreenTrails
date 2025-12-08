package it.greentrails.backend.gestioneprenotazioni.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import it.greentrails.backend.entities.PrenotazioneAttivitaTuristica;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.enums.RuoloUtente;
import it.greentrails.backend.gestioneprenotazioni.repository.PrenotazioneAttivitaTuristicaRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class PrenotazioneAttivitaTuristicaServiceImplTest {

  @Mock
  PrenotazioneAttivitaTuristicaRepository repositoryMock;

  @InjectMocks
  PrenotazioneAttivitaTuristicaServiceImpl service;

  @Test
  void testGetPrenotazioniByVisitatoreWithNullVisitatore() {
    assertThrows(Exception.class, () -> {
      service.getPrenotazioniByVisitatore(null);
    });
  }

  @Test
  void testGetPrenotazioniByVisitatoreWithNonVisitatore() {
    Utente visitatore = new Utente();
    visitatore.setId(1L);
    visitatore.setRuolo(RuoloUtente.GESTORE_ATTIVITA);

    assertThrows(Exception.class, () -> {
      service.getPrenotazioniByVisitatore(visitatore);
    });
  }

  @Test
  void testGetPrenotazioniByVisitatoreWithValidVisitatore() throws Exception {
    Utente visitatore = new Utente();
    visitatore.setId(1L);
    visitatore.setRuolo(RuoloUtente.VISITATORE);

    List<PrenotazioneAttivitaTuristica> prenotazioni = new ArrayList<>();
    Page<PrenotazioneAttivitaTuristica> page = new PageImpl<>(prenotazioni);

    when(repositoryMock.findByVisitatore(eq(1L), any(Pageable.class))).thenReturn(page);

    List<PrenotazioneAttivitaTuristica> risultato = service.getPrenotazioniByVisitatore(visitatore);

    assertNotNull(risultato);
  }
}
