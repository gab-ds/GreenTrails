package it.greentrails.backend.gestioneitinerari.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Camera;
import it.greentrails.backend.entities.Itinerario;
import it.greentrails.backend.entities.Preferenze;
import it.greentrails.backend.entities.PrenotazioneAlloggio;
import it.greentrails.backend.entities.PrenotazioneAttivitaTuristica;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.entities.ValoriEcosostenibilita;
import it.greentrails.backend.enums.PreferenzeAlimentari;
import it.greentrails.backend.enums.PreferenzeAlloggio;
import it.greentrails.backend.enums.PreferenzeAttivita;
import it.greentrails.backend.enums.PreferenzeBudget;
import it.greentrails.backend.enums.PreferenzeStagione;
import it.greentrails.backend.enums.PreferenzeViaggio;
import it.greentrails.backend.enums.RuoloUtente;
import it.greentrails.backend.gestioneattivita.repository.AttivitaRepository;
import it.greentrails.backend.gestioneattivita.repository.CameraRepository;
import it.greentrails.backend.gestioneitinerari.repository.ItinerariRepository;
import it.greentrails.backend.gestioneprenotazioni.repository.PrenotazioneAlloggioRepository;
import it.greentrails.backend.gestioneprenotazioni.repository.PrenotazioneAttivitaTuristicaRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Point;

@ExtendWith(MockitoExtension.class)
class ItinerariStubAdapterTest {

  @Mock
  private AttivitaRepository attivitaRepository;

  @Mock
  private CameraRepository cameraRepository;

  @Mock
  private ItinerariRepository itinerariRepository;

  @Mock
  private PrenotazioneAlloggioRepository prenotazioneAlloggioRepository;

  @Mock
  private PrenotazioneAttivitaTuristicaRepository prenotazioneAttivitaTuristicaRepository;

  @InjectMocks
  private ItinerariStubAdapter itinerariStubAdapter;

  private Preferenze preferenze;
  private Utente visitatore;
  private Itinerario itinerarioSaved;

  @BeforeEach
  void setUp() {
    visitatore = new Utente();
    visitatore.setId(1L);
    visitatore.setNome("Mario");
    visitatore.setCognome("Rossi");
    visitatore.setEmail("mario.rossi@example.com");
    visitatore.setRuolo(RuoloUtente.VISITATORE);

    preferenze = new Preferenze();
    preferenze.setId(1L);
    preferenze.setVisitatore(visitatore);
    preferenze.setViaggioPreferito(PreferenzeViaggio.MONTAGNA);
    preferenze.setAlloggioPreferito(PreferenzeAlloggio.HOTEL);
    preferenze.setPreferenzaAlimentare(PreferenzeAlimentari.NESSUNA_PREFERENZA);
    preferenze.setAttivitaPreferita(PreferenzeAttivita.ALL_APERTO);
    preferenze.setAnimaleDomestico(false);
    preferenze.setBudgetPreferito(PreferenzeBudget.MEDIO);
    preferenze.setSouvenir(false);
    preferenze.setStagioniPreferite(PreferenzeStagione.PRIMAVERA_ESTATE);

    itinerarioSaved = new Itinerario();
    itinerarioSaved.setId(1L);
    itinerarioSaved.setVisitatore(visitatore);
  }

  @Test
  void testPianificazioneAutomatica_ConAttivitaTuristicheECamere() {
    // Arrange
    List<Attivita> attivitaList = createAttivitaList();
    List<Camera> camereList = createCamereList();

    when(itinerariRepository.save(any(Itinerario.class))).thenReturn(itinerarioSaved);
    when(attivitaRepository.findAll()).thenReturn(attivitaList);
    when(cameraRepository.findAll()).thenReturn(camereList);

    // Act
    Itinerario result = itinerariStubAdapter.pianificazioneAutomatica(preferenze);

    // Assert
    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals(visitatore, result.getVisitatore());

    verify(itinerariRepository).save(any(Itinerario.class));
    verify(attivitaRepository).findAll();
    verify(cameraRepository).findAll();

    // Verifica che siano state create prenotazioni per attività turistiche (non alloggi)
    ArgumentCaptor<PrenotazioneAttivitaTuristica> prenotazioneAttivitaCaptor =
        ArgumentCaptor.forClass(PrenotazioneAttivitaTuristica.class);
    verify(prenotazioneAttivitaTuristicaRepository, atLeast(1))
        .save(prenotazioneAttivitaCaptor.capture());

    List<PrenotazioneAttivitaTuristica> prenotazioniAttivita =
        prenotazioneAttivitaCaptor.getAllValues();
    assertTrue(prenotazioniAttivita.size() <= 3);
    for (PrenotazioneAttivitaTuristica p : prenotazioniAttivita) {
      assertNotNull(p.getAttivitaTuristica());
      assertFalse(p.getAttivitaTuristica().isAlloggio());
      assertEquals(itinerarioSaved, p.getItinerario());
      assertNotNull(p.getDataInizio());
      assertEquals(1, p.getNumAdulti());
      assertEquals(0, p.getNumBambini());
      assertEquals(p.getAttivitaTuristica().getPrezzo(), p.getPrezzo());
    }

    // Verifica che sia stata creata una prenotazione alloggio
    ArgumentCaptor<PrenotazioneAlloggio> prenotazioneAlloggioCaptor =
        ArgumentCaptor.forClass(PrenotazioneAlloggio.class);
    verify(prenotazioneAlloggioRepository, atLeast(1))
        .save(prenotazioneAlloggioCaptor.capture());

    List<PrenotazioneAlloggio> prenotazioniAlloggio =
        prenotazioneAlloggioCaptor.getAllValues();
    assertTrue(prenotazioniAlloggio.size() <= 1);
    for (PrenotazioneAlloggio p : prenotazioniAlloggio) {
      assertNotNull(p.getCamera());
      assertEquals(itinerarioSaved, p.getItinerario());
      assertNotNull(p.getDataInizio());
      assertNotNull(p.getDataFine());
      assertEquals(1, p.getNumAdulti());
      assertEquals(0, p.getNumBambini());
      assertEquals(1, p.getNumCamere());
      assertEquals(p.getCamera().getPrezzo(), p.getPrezzo());
    }
  }

  @Test
  void testPianificazioneAutomatica_ConMenoAttivitaTuristicheDisponibili() {
    // Arrange: Solo 2 attività turistiche disponibili (meno di 3)
    List<Attivita> attivitaList = new ArrayList<>();
    attivitaList.add(createAttivitaTuristica(1L, "Attività 1", 50.0));
    attivitaList.add(createAttivitaTuristica(2L, "Attività 2", 60.0));
    // Aggiungi un alloggio (che non deve essere prenotato come attività turistica)
    attivitaList.add(createAlloggio(3L, "Alloggio"));

    List<Camera> camereList = createCamereList();

    when(itinerariRepository.save(any(Itinerario.class))).thenReturn(itinerarioSaved);
    when(attivitaRepository.findAll()).thenReturn(attivitaList);
    when(cameraRepository.findAll()).thenReturn(camereList);

    // Act
    Itinerario result = itinerariStubAdapter.pianificazioneAutomatica(preferenze);

    // Assert
    assertNotNull(result);

    ArgumentCaptor<PrenotazioneAttivitaTuristica> prenotazioneAttivitaCaptor =
        ArgumentCaptor.forClass(PrenotazioneAttivitaTuristica.class);
    verify(prenotazioneAttivitaTuristicaRepository, times(2))
        .save(prenotazioneAttivitaCaptor.capture());

    List<PrenotazioneAttivitaTuristica> prenotazioniAttivita =
        prenotazioneAttivitaCaptor.getAllValues();
    assertEquals(2, prenotazioniAttivita.size());
    for (PrenotazioneAttivitaTuristica p : prenotazioniAttivita) {
      assertFalse(p.getAttivitaTuristica().isAlloggio());
      assertEquals(0, p.getNumBambini());
      assertEquals(p.getAttivitaTuristica().getPrezzo(), p.getPrezzo());
    }
  }

  @Test
  void testPianificazioneAutomatica_ConSoloAlloggi() {
    // Arrange: Solo alloggi disponibili, nessuna attività turistica
    List<Attivita> attivitaList = new ArrayList<>();
    attivitaList.add(createAlloggio(1L, "Alloggio 1"));
    attivitaList.add(createAlloggio(2L, "Alloggio 2"));

    List<Camera> camereList = createCamereList();

    when(itinerariRepository.save(any(Itinerario.class))).thenReturn(itinerarioSaved);
    when(attivitaRepository.findAll()).thenReturn(attivitaList);
    when(cameraRepository.findAll()).thenReturn(camereList);

    // Act
    Itinerario result = itinerariStubAdapter.pianificazioneAutomatica(preferenze);

    // Assert
    assertNotNull(result);

    // Non devono essere create prenotazioni per attività turistiche
    verify(prenotazioneAttivitaTuristicaRepository, never()).save(any());

    // Deve essere creata una prenotazione alloggio
    ArgumentCaptor<PrenotazioneAlloggio> alloggioCaptor =
        ArgumentCaptor.forClass(PrenotazioneAlloggio.class);
    verify(prenotazioneAlloggioRepository, atLeast(1)).save(alloggioCaptor.capture());

    for (PrenotazioneAlloggio p : alloggioCaptor.getAllValues()) {
      assertEquals(0, p.getNumBambini());
      assertEquals(p.getCamera().getPrezzo(), p.getPrezzo());
    }
  }

  @Test
  void testPianificazioneAutomatica_SenzaCamereDisponibili() {
    // Arrange: Attività disponibili ma nessuna camera
    List<Attivita> attivitaList = createAttivitaList();
    List<Camera> camereList = Collections.emptyList();

    when(itinerariRepository.save(any(Itinerario.class))).thenReturn(itinerarioSaved);
    when(attivitaRepository.findAll()).thenReturn(attivitaList);
    when(cameraRepository.findAll()).thenReturn(camereList);

    // Act
    Itinerario result = itinerariStubAdapter.pianificazioneAutomatica(preferenze);

    // Assert
    assertNotNull(result);

    // Devono essere create prenotazioni per attività turistiche
    verify(prenotazioneAttivitaTuristicaRepository, atLeast(1))
        .save(any(PrenotazioneAttivitaTuristica.class));

    // Non devono essere create prenotazioni alloggio
    verify(prenotazioneAlloggioRepository, never()).save(any());
  }

  @Test
  void testPianificazioneAutomatica_SenzaAttivitaDisponibili() {
    // Arrange: Nessuna attività disponibile
    List<Attivita> attivitaList = Collections.emptyList();
    List<Camera> camereList = createCamereList();

    when(itinerariRepository.save(any(Itinerario.class))).thenReturn(itinerarioSaved);
    when(attivitaRepository.findAll()).thenReturn(attivitaList);
    when(cameraRepository.findAll()).thenReturn(camereList);

    // Act
    Itinerario result = itinerariStubAdapter.pianificazioneAutomatica(preferenze);

    // Assert
    assertNotNull(result);

    // Non devono essere create prenotazioni per attività turistiche
    verify(prenotazioneAttivitaTuristicaRepository, never()).save(any());

    // Deve essere creata una prenotazione alloggio
    ArgumentCaptor<PrenotazioneAlloggio> alloggioCaptor =
        ArgumentCaptor.forClass(PrenotazioneAlloggio.class);
    verify(prenotazioneAlloggioRepository, atLeast(1)).save(alloggioCaptor.capture());

    for (PrenotazioneAlloggio p : alloggioCaptor.getAllValues()) {
      assertEquals(0, p.getNumBambini());
      assertEquals(p.getCamera().getPrezzo(), p.getPrezzo());
    }
  }

  @Test
  void testPianificazioneAutomatica_SenzaAttivitaNeCamere() {
    // Arrange: Nessuna attività e nessuna camera disponibile
    List<Attivita> attivitaList = Collections.emptyList();
    List<Camera> camereList = Collections.emptyList();

    when(itinerariRepository.save(any(Itinerario.class))).thenReturn(itinerarioSaved);
    when(attivitaRepository.findAll()).thenReturn(attivitaList);
    when(cameraRepository.findAll()).thenReturn(camereList);

    // Act
    Itinerario result = itinerariStubAdapter.pianificazioneAutomatica(preferenze);

    // Assert
    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals(visitatore, result.getVisitatore());

    // Non devono essere create prenotazioni
    verify(prenotazioneAttivitaTuristicaRepository, never()).save(any());
    verify(prenotazioneAlloggioRepository, never()).save(any());
  }

  @Test
  void testPianificazioneAutomatica_ConEsattamente3AttivitaTuristiche() {
    // Arrange: Esattamente 3 attività turistiche disponibili
    List<Attivita> attivitaList = new ArrayList<>();
    attivitaList.add(createAttivitaTuristica(1L, "Attività 1", 50.0));
    attivitaList.add(createAttivitaTuristica(2L, "Attività 2", 60.0));
    attivitaList.add(createAttivitaTuristica(3L, "Attività 3", 70.0));

    List<Camera> camereList = createCamereList();

    when(itinerariRepository.save(any(Itinerario.class))).thenReturn(itinerarioSaved);
    when(attivitaRepository.findAll()).thenReturn(attivitaList);
    when(cameraRepository.findAll()).thenReturn(camereList);

    // Act
    Itinerario result = itinerariStubAdapter.pianificazioneAutomatica(preferenze);

    // Assert
    assertNotNull(result);

    ArgumentCaptor<PrenotazioneAttivitaTuristica> prenotazioneAttivitaCaptor =
        ArgumentCaptor.forClass(PrenotazioneAttivitaTuristica.class);
    verify(prenotazioneAttivitaTuristicaRepository, times(3))
        .save(prenotazioneAttivitaCaptor.capture());

    List<PrenotazioneAttivitaTuristica> prenotazioniAttivita =
        prenotazioneAttivitaCaptor.getAllValues();
    assertEquals(3, prenotazioniAttivita.size());

    for (PrenotazioneAttivitaTuristica p : prenotazioniAttivita) {
      assertEquals(0, p.getNumBambini());
      assertEquals(p.getAttivitaTuristica().getPrezzo(), p.getPrezzo());
    }
  }

  @Test
  void testPianificazioneAutomatica_VerificaItinerarioSalvato() {
    // Arrange
    List<Attivita> attivitaList = createAttivitaList();
    List<Camera> camereList = createCamereList();

    when(attivitaRepository.findAll()).thenReturn(attivitaList);
    when(cameraRepository.findAll()).thenReturn(camereList);

    ArgumentCaptor<Itinerario> itinerarioCaptor = ArgumentCaptor.forClass(Itinerario.class);
    when(itinerariRepository.save(itinerarioCaptor.capture())).thenReturn(itinerarioSaved);

    // Act
    Itinerario result = itinerariStubAdapter.pianificazioneAutomatica(preferenze);

    // Assert
    assertNotNull(result);

    Itinerario itinerarioSalvato = itinerarioCaptor.getValue();
    assertNotNull(itinerarioSalvato);
    assertEquals(visitatore, itinerarioSalvato.getVisitatore());

    // Verifica numBambini = 0 per tutte le prenotazioni attività
    ArgumentCaptor<PrenotazioneAttivitaTuristica> captorAtt =
        ArgumentCaptor.forClass(PrenotazioneAttivitaTuristica.class);
    verify(prenotazioneAttivitaTuristicaRepository, atLeast(1)).save(captorAtt.capture());
    captorAtt.getAllValues().forEach(p ->
        assertEquals(0, p.getNumBambini()));

    // Verifica numBambini = 0 per tutte le prenotazioni alloggio
    ArgumentCaptor<PrenotazioneAlloggio> captorAll =
        ArgumentCaptor.forClass(PrenotazioneAlloggio.class);
    verify(prenotazioneAlloggioRepository, atLeast(1)).save(captorAll.capture());
    captorAll.getAllValues().forEach(p ->
        assertEquals(0, p.getNumBambini()));
  }

  @Test
  void testPianificazioneAutomatica_VerificaNumeroAttivitaTuristiche() {
    List<Attivita> attivitaList = createAttivitaList();
    List<Camera> camereList = createCamereList();

    when(itinerariRepository.save(any(Itinerario.class))).thenReturn(itinerarioSaved);
    when(attivitaRepository.findAll()).thenReturn(attivitaList);
    when(cameraRepository.findAll()).thenReturn(camereList);

    ArgumentCaptor<PrenotazioneAttivitaTuristica> captorAttivita =
        ArgumentCaptor.forClass(PrenotazioneAttivitaTuristica.class);
    when(prenotazioneAttivitaTuristicaRepository.save(captorAttivita.capture()))
        .thenReturn(new PrenotazioneAttivitaTuristica());

    when(prenotazioneAttivitaTuristicaRepository.findByItinerario(eq(1L), any(Pageable.class)))
        .thenAnswer(invocation -> {
          List<PrenotazioneAttivitaTuristica> list = captorAttivita.getAllValues();
          return new PageImpl<>(list);
        });

    // Act
    Itinerario result = itinerariStubAdapter.pianificazioneAutomatica(preferenze);

    // Assert
    assertNotNull(result);
    var prenotazioni = prenotazioneAttivitaTuristicaRepository
        .findByItinerario(itinerarioSaved.getId(), Pageable.unpaged()).toList();
    assertEquals(3, prenotazioni.size(), "Dovrebbero esserci esattamente 3 attività turistiche");

    for (PrenotazioneAttivitaTuristica p : prenotazioni) {
      assertNotNull(p.getAttivitaTuristica());
      assertFalse(p.getAttivitaTuristica().isAlloggio());
    }
  }

  @Test
  void testPianificazioneAutomatica_VerificaNumeroAlloggi() {
    // Arrange
    List<Attivita> attivitaList = createAttivitaList();
    List<Camera> camereList = createCamereList();

    when(itinerariRepository.save(any(Itinerario.class))).thenReturn(itinerarioSaved);
    when(attivitaRepository.findAll()).thenReturn(attivitaList);
    when(cameraRepository.findAll()).thenReturn(camereList);

    ArgumentCaptor<PrenotazioneAlloggio> captorAlloggio =
        ArgumentCaptor.forClass(PrenotazioneAlloggio.class);
    when(prenotazioneAlloggioRepository.save(captorAlloggio.capture()))
        .thenReturn(new PrenotazioneAlloggio());

    when(prenotazioneAlloggioRepository.findByItinerario(eq(1L), any(Pageable.class)))
        .thenAnswer(invocation -> {
          List<PrenotazioneAlloggio> list = captorAlloggio.getAllValues();
          return new PageImpl<>(list);
        });

    // Act
    Itinerario result = itinerariStubAdapter.pianificazioneAutomatica(preferenze);

    // Assert
    assertNotNull(result);
    var prenotazioni = prenotazioneAlloggioRepository
        .findByItinerario(itinerarioSaved.getId(), Pageable.unpaged()).toList();
    assertEquals(1, prenotazioni.size(), "Dovrebbe esserci esattamente 1 alloggio");
  }

  // Helper methods

  private List<Attivita> createAttivitaList() {
    List<Attivita> list = new ArrayList<>();
    list.add(createAttivitaTuristica(1L, "Escursione in montagna", 50.0));
    list.add(createAttivitaTuristica(2L, "Visita al museo", 30.0));
    list.add(createAttivitaTuristica(3L, "Tour guidato", 40.0));
    list.add(createAttivitaTuristica(4L, "Degustazione vini", 60.0));
    list.add(createAlloggio(5L, "Hotel Verde"));
    return list;
  }

  private Attivita createAttivitaTuristica(Long id, String nome, double prezzo) {
    Attivita attivita = new Attivita();
    attivita.setId(id);
    attivita.setNome(nome);
    attivita.setPrezzo(prezzo);
    attivita.setAlloggio(false);
    attivita.setDescrizioneBreve("Descrizione breve " + nome);
    attivita.setDescrizioneLunga("Descrizione lunga " + nome);
    attivita.setIndirizzo("Via Test, 1");
    attivita.setCap("00100");
    attivita.setCitta("Roma");
    attivita.setProvincia("RM");
    attivita.setCoordinate(new Point(12.0, 42.0));
    attivita.setMedia("http://example.com/media.jpg");

    ValoriEcosostenibilita valori = new ValoriEcosostenibilita();
    valori.setId(1L);
    valori.setPoliticheAntispreco(true);
    valori.setProdottiLocali(true);
    valori.setEnergiaVerde(true);
    valori.setRaccoltaDifferenziata(true);
    valori.setLimiteEmissioneCO2(true);
    valori.setContattoConNatura(true);
    attivita.setValoriEcosostenibilita(valori);

    return attivita;
  }

  private Attivita createAlloggio(Long id, String nome) {
    Attivita attivita = createAttivitaTuristica(id, nome, 100.0);
    attivita.setAlloggio(true);
    return attivita;
  }

  private List<Camera> createCamereList() {
    List<Camera> list = new ArrayList<>();

    Attivita alloggio = createAlloggio(10L, "Hotel Test");

    Camera camera1 = new Camera();
    camera1.setId(1L);
    camera1.setTipoCamera("Singola");
    camera1.setPrezzo(100.0);
    camera1.setCapienza(2);
    camera1.setDisponibilita(5);
    camera1.setDescrizione("Camera singola confortevole");
    camera1.setAlloggio(alloggio);

    Camera camera2 = new Camera();
    camera2.setId(2L);
    camera2.setTipoCamera("Doppia");
    camera2.setPrezzo(150.0);
    camera2.setCapienza(4);
    camera2.setDisponibilita(3);
    camera2.setDescrizione("Camera doppia spaziosa");
    camera2.setAlloggio(alloggio);

    list.add(camera1);
    list.add(camera2);

    return list;
  }
}