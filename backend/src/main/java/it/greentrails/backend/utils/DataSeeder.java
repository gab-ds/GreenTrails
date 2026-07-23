package it.greentrails.backend.utils;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Camera;
import it.greentrails.backend.entities.Categoria;
import it.greentrails.backend.entities.Itinerario;
import it.greentrails.backend.entities.Preferenze;
import it.greentrails.backend.entities.PrenotazioneAlloggio;
import it.greentrails.backend.entities.PrenotazioneAttivitaTuristica;
import it.greentrails.backend.entities.Recensione;
import it.greentrails.backend.entities.Segnalazione;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.entities.ValoriEcosostenibilita;
import it.greentrails.backend.enums.CategorieAlloggio;
import it.greentrails.backend.enums.CategorieAttivitaTuristica;
import it.greentrails.backend.enums.PreferenzeAlimentari;
import it.greentrails.backend.enums.PreferenzeAlloggio;
import it.greentrails.backend.enums.PreferenzeAttivita;
import it.greentrails.backend.enums.PreferenzeBudget;
import it.greentrails.backend.enums.PreferenzeStagione;
import it.greentrails.backend.enums.PreferenzeViaggio;
import it.greentrails.backend.enums.RuoloUtente;
import it.greentrails.backend.enums.StatoItinerario;
import it.greentrails.backend.enums.StatoPrenotazione;
import it.greentrails.backend.enums.StatoSegnalazione;
import it.greentrails.backend.gestioneattivita.repository.AttivitaRepository;
import it.greentrails.backend.gestioneattivita.repository.CameraRepository;
import it.greentrails.backend.gestioneattivita.repository.CategoriaRepository;
import it.greentrails.backend.gestioneattivita.repository.RecensioneRepository;
import it.greentrails.backend.gestioneattivita.repository.ValoriEcosostenibilitaRepository;
import it.greentrails.backend.gestioneutenze.repository.PreferenzeRepository;
import it.greentrails.backend.gestioneutenze.repository.UtenteRepository;
import it.greentrails.backend.gestionesegnalazioni.repository.SegnalazioniRepository;
import it.greentrails.backend.gestioneitinerari.repository.ItinerariRepository;
import it.greentrails.backend.gestioneprenotazioni.repository.PrenotazioneAlloggioRepository;
import it.greentrails.backend.gestioneprenotazioni.repository.PrenotazioneAttivitaTuristicaRepository;
import java.util.Date;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.geo.Point;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "test"})
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

  private final UtenteRepository utenteRepository;
  private final CategoriaRepository categoriaRepository;
  private final ValoriEcosostenibilitaRepository valoriRepository;
  private final AttivitaRepository attivitaRepository;
  private final CameraRepository cameraRepository;
  private final RecensioneRepository recensioneRepository;
  private final PreferenzeRepository preferenzeRepository;
  private final ItinerariRepository itinerarioRepository;
  private final SegnalazioniRepository segnalazioneRepository;
  private final PrenotazioneAlloggioRepository prenotazioneAlloggioRepository;
  private final PrenotazioneAttivitaTuristicaRepository prenotazioneAttivitaTuristicaRepository;

  private final PasswordEncoder encoder = new BCryptPasswordEncoder();

  @Override
  public void run(String... args) throws Exception {
    if (utenteRepository.count() > 0) {
      return;
    }

    Utente mario = utenteRepository.save(createUtente("Mario", "Rossi", "1980-05-15",
        "mario@test.it", RuoloUtente.GESTORE_ATTIVITA));
    Utente lucia = utenteRepository.save(createUtente("Lucia", "Verdi", "1985-03-20",
        "lucia@test.it", RuoloUtente.GESTORE_ATTIVITA));
    Utente marco = utenteRepository.save(createUtente("Marco", "Bianchi", "1995-07-10",
        "marco@test.it", RuoloUtente.VISITATORE));
    Utente anna = utenteRepository.save(createUtente("Anna", "Neri", "2000-01-25",
        "anna@test.it", RuoloUtente.VISITATORE));
    Utente admin = utenteRepository.save(createUtente("Admin", "System", "1990-01-01",
        "admin@test.it", RuoloUtente.AMMINISTRATORE));

    Categoria natura = categoriaRepository.save(
        createCategoria("Natura e Paesaggio", "Attività all'aria aperta a contatto con la natura"));
    Categoria cultura = categoriaRepository.save(
        createCategoria("Cultura e Storia", "Visite a siti storici, musei e monumenti"));
    Categoria gastro = categoriaRepository.save(
        createCategoria("Gastronomia", "Esperienze culinarie e degustazioni"));
    Categoria relax = categoriaRepository.save(
        createCategoria("Relax e Benessere", "Spa, centri benessere e attività rilassanti"));
    Categoria sport = categoriaRepository.save(
        createCategoria("Sport e Avventura", "Attività sportive e avventurose"));
    Categoria ecosostenibilita = categoriaRepository.save(
        createCategoria("Eco-Sostenibilità", "Attività con attenzione all'ambiente"));

    ValoriEcosostenibilita v1 = valoriRepository.save(createValori(true, true, true, true, true, true));
    ValoriEcosostenibilita v2 = valoriRepository.save(createValori(true, true, false, true, false, true));
    ValoriEcosostenibilita v3 = valoriRepository.save(createValori(false, true, true, true, true, false));
    ValoriEcosostenibilita v4 = valoriRepository.save(createValori(true, false, true, true, false, true));
    ValoriEcosostenibilita v5 = valoriRepository.save(createValori(true, true, true, true, false, true));
    ValoriEcosostenibilita v6 = valoriRepository.save(createValori(false, true, false, true, true, true));

    Attivita ecoHotel = attivitaRepository.save(createAlloggio(mario,
        "EcoHotel Roma Verde", "Via dei Fori Imperiali 1", "00100", "Roma", "RM",
        new Point(41.9028, 12.4964), 120.00,
        "Hotel ecosostenibile nel cuore di Roma",
        "Hotel con pannelli solari, raccolta differenziata e prodotti locali.",
        v1, CategorieAlloggio.HOTEL, 50));

    Attivita bbFirenze = attivitaRepository.save(createAlloggio(mario,
        "B And B Fiorentino Bio", "Via della Vigna Nuova 10", "50123", "Firenze", "FI",
        new Point(43.7696, 11.2558), 85.00,
        "Bed & Breakfast biologico a Firenze",
        "B&B con colazione biologica a base di prodotti toscani.",
        v2, CategorieAlloggio.BED_AND_BREAKFAST, 20));

    Attivita agriturismo = attivitaRepository.save(createAlloggio(lucia,
        "Agriturismo Costa Blu", "Via Marina 5", "84010", "Amalfi", "SA",
        new Point(40.6333, 14.6000), 95.00,
        "Agriturismo sostenibile sulla Costiera",
        "Prodotti a km0 dall'orto, energia solare e recupero acque.",
        v3, CategorieAlloggio.VILLAGGIO_TURISTICO, 30));

    Attivita vesuvio = attivitaRepository.save(createAttivitaTuristica(lucia,
        "Escursione Vesuvio", "Piazza Duomo 1", "80013", "Caserta", "CE",
        new Point(41.2341, 14.4321), 25.00,
        "Escursione guidata al Vesuvio",
        "Camminata guidata nei sentieri del Vesuvio.",
        v4, CategorieAttivitaTuristica.ALL_APERTO, 100));

    Attivita tourGastro = attivitaRepository.save(createAttivitaTuristica(mario,
        "Tour Gastronomico Roma", "Via del Corso 100", "00100", "Roma", "RM",
        new Point(41.9030, 12.4760), 55.00,
        "Tour enogastronomico a Roma",
        "Degustazione vini, formaggi e cooking class.",
        v5, CategorieAttivitaTuristica.VISITE_CULTURALI_STORICHE, 40));

    Attivita pompei = attivitaRepository.save(createAttivitaTuristica(lucia,
        "Visita Guidata Pompei", "Via Villa dei Misteri 2", "80045", "Pompei", "NA",
        new Point(40.7500, 14.4897), 35.00,
        "Visita guidata eco-sostenibile a Pompei",
        "Tour a piedi con guida, focus su storia e ambiente.",
        v6, CategorieAttivitaTuristica.VISITE_CULTURALI_STORICHE, 80));

    vesuvio.getCategorie().addAll(Set.of(natura, sport));
    tourGastro.getCategorie().addAll(Set.of(gastro, ecosostenibilita));
    pompei.getCategorie().addAll(Set.of(cultura, ecosostenibilita));
    attivitaRepository.saveAll(Set.of(vesuvio, tourGastro, pompei));

    Camera c1 = cameraRepository.save(createCamera(ecoHotel,
        "Camera Doppia", 120.00, 20, "Letto matrimoniale e bagno privato", 2));
    cameraRepository.save(createCamera(ecoHotel,
        "Camera Singola", 80.00, 15, "Singola con bagno privato", 1));
    cameraRepository.save(createCamera(ecoHotel,
        "Suite", 200.00, 5, "Suite con terrazzo vista Fori Imperiali", 3));
    cameraRepository.save(createCamera(bbFirenze,
        "Camera Doppia", 85.00, 8, "Letto matrimoniale e vista Duomo", 2));
    cameraRepository.save(createCamera(bbFirenze,
        "Camera Tripla", 110.00, 4, "Camera per famiglia con 3 letti", 3));
    Camera cAgriDoppia = cameraRepository.save(createCamera(agriturismo,
        "Camera Doppia", 95.00, 10, "Vista mare e balcone", 2));
    cameraRepository.save(createCamera(agriturismo,
        "Appartamento", 150.00, 3, "Con cucina", 4));

    Itinerario it1 = itinerarioRepository.save(createItinerario(marco, StatoItinerario.PIANIFICATO, 0.0));
    Itinerario it2 = itinerarioRepository.save(createItinerario(marco, StatoItinerario.PIANIFICATO, 0.0));
    Itinerario it3 = itinerarioRepository.save(createItinerario(anna, StatoItinerario.PIANIFICATO, 0.0));

    recensioneRepository.save(createRecensione(marco, ecoHotel, 5,
        "Hotel meraviglioso, attento all'ambiente!", v1));
    recensioneRepository.save(createRecensione(marco, tourGastro, 4,
        "Tour interessante, cibo ottimo.", v5));
    recensioneRepository.save(createRecensione(anna, agriturismo, 5,
        "Agriturismo da sogno, tutto biologico!", v3));
    recensioneRepository.save(createRecensione(anna, pompei, 3,
        "Visita interessante ma lunga per bambini.", v6));

    preferenzeRepository.save(createPreferenze(marco,
        PreferenzeViaggio.CITTA, PreferenzeAlloggio.HOTEL,
        PreferenzeAlimentari.VEGETARIAN, PreferenzeAttivita.GASTRONOMIA,
        false, PreferenzeBudget.ALTO, true, PreferenzeStagione.PRIMAVERA_ESTATE));
    preferenzeRepository.save(createPreferenze(anna,
        PreferenzeViaggio.MARE, PreferenzeAlloggio.OSTELLO,
        PreferenzeAlimentari.GLUTEN_FREE, PreferenzeAttivita.ALL_APERTO,
        true, PreferenzeBudget.BASSO, false, PreferenzeStagione.AUTUNNO_INVERNO));

    segnalazioneRepository.save(createSegnalazione(marco,
        "Camera non corrisponde alla descrizione", true, 1L, null));
    segnalazioneRepository.save(createSegnalazione(anna,
        "Attività sospesa per maltempo", false, null, 6L));

    Date now = new Date(System.currentTimeMillis() + 86_400_000L);
    prenotazioneAlloggioRepository.save(createPrenotazioneAlloggio(it1, c1,
        2, 0, now, now, 1, StatoPrenotazione.COMPLETATA, 240.0));
    prenotazioneAlloggioRepository.save(createPrenotazioneAlloggio(it1, cAgriDoppia,
        2, 1, now, now, 1, StatoPrenotazione.CREATA, 190.0));

    prenotazioneAttivitaTuristicaRepository.save(createPrenotazioneAttivita(it1, tourGastro,
        2, 0, now, now, StatoPrenotazione.COMPLETATA, 110.0));
    prenotazioneAttivitaTuristicaRepository.save(createPrenotazioneAttivita(it2, vesuvio,
        1, 0, now, null, StatoPrenotazione.CREATA, 25.0));
  }

  private Utente createUtente(String nome, String cognome, String dataNascita,
      String email, RuoloUtente ruolo) {
    Utente u = new Utente();
    u.setNome(nome);
    u.setCognome(cognome);
    u.setDataNascita(java.sql.Date.valueOf(dataNascita));
    u.setEmail(email);
    u.setPassword(encoder.encode("password"));
    u.setRuolo(ruolo);
    return u;
  }

  private Categoria createCategoria(String nome, String descrizione) {
    Categoria c = new Categoria();
    c.setNome(nome);
    c.setDescrizione(descrizione);
    return c;
  }

  private ValoriEcosostenibilita createValori(boolean politiche, boolean prodotti,
      boolean energia, boolean raccolta, boolean emissioni, boolean contatto) {
    ValoriEcosostenibilita v = new ValoriEcosostenibilita();
    v.setPoliticheAntispreco(politiche);
    v.setProdottiLocali(prodotti);
    v.setEnergiaVerde(energia);
    v.setRaccoltaDifferenziata(raccolta);
    v.setLimiteEmissioneCO2(emissioni);
    v.setContattoConNatura(contatto);
    return v;
  }

  private Attivita createAlloggio(Utente gestore, String nome, String indirizzo,
      String cap, String citta, String provincia, Point coordinate, double prezzo,
      String descrizioneBreve, String descrizioneLunga,
      ValoriEcosostenibilita valori, CategorieAlloggio categoria, int disponibilita) {
    Attivita a = new Attivita();
    a.setGestore(gestore);
    a.setNome(nome);
    a.setIndirizzo(indirizzo);
    a.setCap(cap);
    a.setCitta(citta);
    a.setProvincia(provincia);
    a.setCoordinate(coordinate);
    a.setPrezzo(prezzo);
    a.setDescrizioneBreve(descrizioneBreve);
    a.setDescrizioneLunga(descrizioneLunga);
    a.setValoriEcosostenibilita(valori);
    a.setCategoriaAlloggio(categoria);
    a.setDisponibilita(disponibilita);
    a.setAlloggio(true);
    a.setMedia(java.util.UUID.randomUUID().toString());
    return a;
  }

  private Attivita createAttivitaTuristica(Utente gestore, String nome, String indirizzo,
      String cap, String citta, String provincia, Point coordinate, double prezzo,
      String descrizioneBreve, String descrizioneLunga,
      ValoriEcosostenibilita valori, CategorieAttivitaTuristica categoria, int disponibilita) {
    Attivita a = new Attivita();
    a.setGestore(gestore);
    a.setNome(nome);
    a.setIndirizzo(indirizzo);
    a.setCap(cap);
    a.setCitta(citta);
    a.setProvincia(provincia);
    a.setCoordinate(coordinate);
    a.setPrezzo(prezzo);
    a.setDescrizioneBreve(descrizioneBreve);
    a.setDescrizioneLunga(descrizioneLunga);
    a.setValoriEcosostenibilita(valori);
    a.setCategoriaAttivitaTuristica(categoria);
    a.setDisponibilita(disponibilita);
    a.setAlloggio(false);
    a.setMedia(java.util.UUID.randomUUID().toString());
    return a;
  }

  private Camera createCamera(Attivita alloggio, String tipoCamera, double prezzo,
      int disponibilita, String descrizione, int capienza) {
    Camera c = new Camera();
    c.setAlloggio(alloggio);
    c.setTipoCamera(tipoCamera);
    c.setPrezzo(prezzo);
    c.setDisponibilita(disponibilita);
    c.setDescrizione(descrizione);
    c.setCapienza(capienza);
    return c;
  }

  private Itinerario createItinerario(Utente visitatore, StatoItinerario stato, double totale) {
    Itinerario i = new Itinerario();
    i.setVisitatore(visitatore);
    i.setStato(stato);
    i.setTotale(totale);
    return i;
  }

  private Recensione createRecensione(Utente visitatore, Attivita attivita,
      int valutazione, String descrizione, ValoriEcosostenibilita valori) {
    Recensione r = new Recensione();
    r.setVisitatore(visitatore);
    r.setAttivita(attivita);
    r.setValutazioneStelleEsperienza(valutazione);
    r.setDescrizione(descrizione);
    r.setValoriEcosostenibilita(valori);
    return r;
  }

  private Preferenze createPreferenze(Utente visitatore, PreferenzeViaggio viaggio,
      PreferenzeAlloggio alloggio, PreferenzeAlimentari alimentare,
      PreferenzeAttivita attivita, boolean animale, PreferenzeBudget budget,
      boolean souvenir, PreferenzeStagione stagione) {
    Preferenze p = new Preferenze();
    p.setId(visitatore.getId());
    p.setVisitatore(visitatore);
    p.setViaggioPreferito(viaggio);
    p.setAlloggioPreferito(alloggio);
    p.setPreferenzaAlimentare(alimentare);
    p.setAttivitaPreferita(attivita);
    p.setAnimaleDomestico(animale);
    p.setBudgetPreferito(budget);
    p.setSouvenir(souvenir);
    p.setStagioniPreferite(stagione);
    return p;
  }

  private Segnalazione createSegnalazione(Utente utente, String descrizione,
      boolean isPerRecensione, Long idRecensione, Long idAttivita) {
    Segnalazione s = new Segnalazione();
    s.setDataSegnalazione(new Date());
    s.setDescrizione(descrizione);
    s.setStato(StatoSegnalazione.CREATA);
    s.setForRecensione(isPerRecensione);
    s.setUtente(utente);
    if (idRecensione != null) {
      Recensione r = new Recensione();
      r.setId(idRecensione);
      s.setRecensione(r);
    }
    if (idAttivita != null) {
      Attivita a = new Attivita();
      a.setId(idAttivita);
      s.setAttivita(a);
    }
    return s;
  }

  private PrenotazioneAlloggio createPrenotazioneAlloggio(Itinerario itinerario,
      Camera camera, int adulti, int bambini, Date dataInizio, Date dataFine,
      int numCamere, StatoPrenotazione stato, double prezzo) {
    PrenotazioneAlloggio p = new PrenotazioneAlloggio();
    p.setItinerario(itinerario);
    p.setCamera(camera);
    p.setNumAdulti(adulti);
    p.setNumBambini(bambini);
    p.setDataInizio(dataInizio);
    p.setDataFine(dataFine);
    p.setNumCamere(numCamere);
    p.setStato(stato);
    p.setPrezzo(prezzo);
    return p;
  }

  private PrenotazioneAttivitaTuristica createPrenotazioneAttivita(Itinerario itinerario,
      Attivita attivita, int adulti, int bambini, Date dataInizio, Date dataFine,
      StatoPrenotazione stato, double prezzo) {
    PrenotazioneAttivitaTuristica p = new PrenotazioneAttivitaTuristica();
    p.setItinerario(itinerario);
    p.setAttivitaTuristica(attivita);
    p.setNumAdulti(adulti);
    p.setNumBambini(bambini);
    p.setDataInizio(dataInizio);
    p.setDataFine(dataFine);
    p.setStato(stato);
    p.setPrezzo(prezzo);
    return p;
  }

}
