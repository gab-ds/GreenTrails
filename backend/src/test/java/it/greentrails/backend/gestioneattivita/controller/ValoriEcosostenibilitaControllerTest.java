package it.greentrails.backend.gestioneattivita.controller;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.entities.ValoriEcosostenibilita;
import it.greentrails.backend.enums.CategorieAlloggio;
import it.greentrails.backend.enums.RuoloUtente;
import it.greentrails.backend.gestioneattivita.service.AttivitaService;
import it.greentrails.backend.gestioneattivita.service.ValoriEcosostenibilitaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.geo.Point;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ValoriEcosostenibilitaControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ValoriEcosostenibilitaService valoriEcosostenibilitaService;

  @MockBean
  private AttivitaService attivitaService;

  private Utente gestore;
  private Utente amministratore;
  private Attivita alloggio;
  private ValoriEcosostenibilita valori;

  @BeforeEach
  void setUp() {
    gestore = new Utente();
    gestore.setId(1L);
    gestore.setEmail("gestore@test.com");
    gestore.setRuolo(RuoloUtente.GESTORE_ATTIVITA);

    amministratore = new Utente();
    amministratore.setId(2L);
    amministratore.setEmail("admin@test.com");
    amministratore.setRuolo(RuoloUtente.AMMINISTRATORE);

    valori = new ValoriEcosostenibilita();
    valori.setId(1L);
    valori.setPoliticheAntispreco(true);
    valori.setProdottiLocali(true);
    valori.setEnergiaVerde(true);
    valori.setRaccoltaDifferenziata(true);
    valori.setLimiteEmissioneCO2(true);
    valori.setContattoConNatura(true);

    alloggio = new Attivita();
    alloggio.setId(1L);
    alloggio.setAlloggio(true);
    alloggio.setGestore(gestore);
    alloggio.setNome("Hotel Eco");
    alloggio.setIndirizzo("Via Verde 1");
    alloggio.setCap("00100");
    alloggio.setCitta("Roma");
    alloggio.setProvincia("RM");
    alloggio.setCoordinate(new Point(41.9028, 12.4964));
    alloggio.setDescrizioneBreve("Hotel ecosostenibile");
    alloggio.setDescrizioneLunga("Un hotel completamente ecosostenibile");
    alloggio.setValoriEcosostenibilita(valori);
    alloggio.setCategoriaAlloggio(CategorieAlloggio.HOTEL);
    alloggio.setMedia("media-uuid");
    alloggio.setEliminata(false);
  }

  @Test
  void testCreaValoriEcosostenibilita_Success() throws Exception {
    // Return the actual argument passed, so if setters are not called, fields will be null
    when(valoriEcosostenibilitaService.saveValori(any(ValoriEcosostenibilita.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    mockMvc.perform(post("/api/valori")
            .param("politicheAntispreco", "true")
            .param("prodottiLocali", "true")
            .param("energiaVerde", "true")
            .param("raccoltaDifferenziata", "true")
            .param("limiteEmissioneCO2", "true")
            .param("contattoConNatura", "true")
            .with(user(gestore))
            .with(csrf()))
        .andExpect(status().isOk())
        // ResponseGenerator wraps the entity under the "data" key, so assert under $.data
        .andExpect(jsonPath("$.data.politicheAntispreco").value(true))
        .andExpect(jsonPath("$.data.prodottiLocali").value(true))
        .andExpect(jsonPath("$.data.energiaVerde").value(true))
        .andExpect(jsonPath("$.data.raccoltaDifferenziata").value(true))
        .andExpect(jsonPath("$.data.limiteEmissioneCO2").value(true))
        .andExpect(jsonPath("$.data.contattoConNatura").value(true));

    verify(valoriEcosostenibilitaService).saveValori(any(ValoriEcosostenibilita.class));
  }

  @Test
  void testCreaValoriEcosostenibilita_TuttiNull_Success() throws Exception {
    ValoriEcosostenibilita valoriNull = new ValoriEcosostenibilita();
    valoriNull.setId(2L);
    when(valoriEcosostenibilitaService.saveValori(any(ValoriEcosostenibilita.class)))
        .thenReturn(valoriNull);

    mockMvc.perform(post("/api/valori")
            .with(user(gestore))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(2));

    verify(valoriEcosostenibilitaService).saveValori(any(ValoriEcosostenibilita.class));
  }

  @Test
  void testCreaValoriEcosostenibilita_Exception() throws Exception {
    when(valoriEcosostenibilitaService.saveValori(any(ValoriEcosostenibilita.class)))
        .thenThrow(new Exception("Errore DB"));

    mockMvc.perform(post("/api/valori")
            .param("politicheAntispreco", "true")
            .with(user(gestore))
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(valoriEcosostenibilitaService).saveValori(any(ValoriEcosostenibilita.class));
  }

  @Test
  void testVisualizzaValoriEcosostenibilita_Success() throws Exception {
    when(valoriEcosostenibilitaService.findById(1L)).thenReturn(valori);

    mockMvc.perform(get("/api/valori/1")
            .with(user(gestore))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.politicheAntispreco").value(true))
        .andExpect(jsonPath("$.data.prodottiLocali").value(true))
        .andExpect(jsonPath("$.data.energiaVerde").value(true))
        .andExpect(jsonPath("$.data.raccoltaDifferenziata").value(true))
        .andExpect(jsonPath("$.data.limiteEmissioneCO2").value(true))
        .andExpect(jsonPath("$.data.contattoConNatura").value(true));

    verify(valoriEcosostenibilitaService).findById(1L);
  }

  @Test
  void testVisualizzaValoriEcosostenibilita_Exception() throws Exception {
    when(valoriEcosostenibilitaService.findById(999L))
        .thenThrow(new Exception("Valori non trovati"));

    mockMvc.perform(get("/api/valori/999")
            .with(user(gestore))
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(valoriEcosostenibilitaService).findById(999L);
  }

  @Test
  void testModificaValoriEcosostenibilita_ProprioGestore_Success() throws Exception {
    when(valoriEcosostenibilitaService.findById(1L)).thenReturn(valori);
    when(attivitaService.findByValori(valori)).thenReturn(Optional.of(alloggio));

    // Return the actual argument so if setters are not called, the response will have old values
    when(valoriEcosostenibilitaService.saveValori(any(ValoriEcosostenibilita.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    mockMvc.perform(post("/api/valori/1")
            .param("politicheAntispreco", "false")
            .param("prodottiLocali", "false")
            .param("energiaVerde", "false")
            .param("raccoltaDifferenziata", "false")
            .param("limiteEmissioneCO2", "false")
            .param("contattoConNatura", "false")
            .with(user(gestore))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.politicheAntispreco").value(false))
        .andExpect(jsonPath("$.data.prodottiLocali").value(false))
        .andExpect(jsonPath("$.data.energiaVerde").value(false))
        .andExpect(jsonPath("$.data.raccoltaDifferenziata").value(false))
        .andExpect(jsonPath("$.data.limiteEmissioneCO2").value(false))
        .andExpect(jsonPath("$.data.contattoConNatura").value(false));

    verify(valoriEcosostenibilitaService).findById(1L);
    verify(attivitaService).findByValori(valori);
    verify(valoriEcosostenibilitaService).saveValori(any(ValoriEcosostenibilita.class));
  }

  @Test
  void testModificaValoriEcosostenibilita_CambioValoriSpecifici_Success() throws Exception {
    // Creo valori iniziali con alcuni campi a false
    ValoriEcosostenibilita valoriIniziali = new ValoriEcosostenibilita();
    valoriIniziali.setId(2L);
    valoriIniziali.setPoliticheAntispreco(false);
    valoriIniziali.setProdottiLocali(false);
    valoriIniziali.setEnergiaVerde(false);
    valoriIniziali.setRaccoltaDifferenziata(false);
    valoriIniziali.setLimiteEmissioneCO2(false);
    valoriIniziali.setContattoConNatura(false);

    Attivita alloggioIniziale = new Attivita();
    alloggioIniziale.setId(2L);
    alloggioIniziale.setGestore(gestore);
    alloggioIniziale.setValoriEcosostenibilita(valoriIniziali);

    when(valoriEcosostenibilitaService.findById(2L)).thenReturn(valoriIniziali);
    when(attivitaService.findByValori(valoriIniziali)).thenReturn(Optional.of(alloggioIniziale));
    when(valoriEcosostenibilitaService.saveValori(any(ValoriEcosostenibilita.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // Modifico solo alcuni valori specifici da false a true
    mockMvc.perform(post("/api/valori/2")
            .param("politicheAntispreco", "true")
            .param("prodottiLocali", "true")
            .param("energiaVerde", "true")
            .param("raccoltaDifferenziata", "true")
            .param("limiteEmissioneCO2", "true")
            .param("contattoConNatura", "true")
            .with(user(gestore))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(2))
        .andExpect(jsonPath("$.data.politicheAntispreco").value(true))
        .andExpect(jsonPath("$.data.prodottiLocali").value(true))
        .andExpect(jsonPath("$.data.energiaVerde").value(true))
        .andExpect(jsonPath("$.data.raccoltaDifferenziata").value(true))
        .andExpect(jsonPath("$.data.limiteEmissioneCO2").value(true))
        .andExpect(jsonPath("$.data.contattoConNatura").value(true));

    verify(valoriEcosostenibilitaService).findById(2L);
    verify(attivitaService).findByValori(valoriIniziali);
    verify(valoriEcosostenibilitaService).saveValori(any(ValoriEcosostenibilita.class));
  }

  @Test
  void testModificaValoriEcosostenibilita_Amministratore_Success() throws Exception {
    when(valoriEcosostenibilitaService.findById(1L)).thenReturn(valori);
    when(attivitaService.findByValori(valori)).thenReturn(Optional.of(alloggio));

    // Return the actual argument so if setters are not called, the response will have old values
    when(valoriEcosostenibilitaService.saveValori(any(ValoriEcosostenibilita.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    mockMvc.perform(post("/api/valori/1")
            .param("politicheAntispreco", "false")
            .param("prodottiLocali", "false")
            .param("energiaVerde", "false")
            .param("raccoltaDifferenziata", "false")
            .param("limiteEmissioneCO2", "false")
            .param("contattoConNatura", "false")
            .with(user(amministratore))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.politicheAntispreco").value(false))
        .andExpect(jsonPath("$.data.prodottiLocali").value(false))
        .andExpect(jsonPath("$.data.energiaVerde").value(false))
        .andExpect(jsonPath("$.data.raccoltaDifferenziata").value(false))
        .andExpect(jsonPath("$.data.limiteEmissioneCO2").value(false))
        .andExpect(jsonPath("$.data.contattoConNatura").value(false));

    verify(valoriEcosostenibilitaService).findById(1L);
    verify(attivitaService).findByValori(valori);
    verify(valoriEcosostenibilitaService).saveValori(any(ValoriEcosostenibilita.class));
  }

  @Test
  void testModificaValoriEcosostenibilita_AltroGestore_NotFound() throws Exception {
    Utente altroGestore = new Utente();
    altroGestore.setId(3L);
    altroGestore.setEmail("altro@test.com");
    altroGestore.setRuolo(RuoloUtente.GESTORE_ATTIVITA);

    when(valoriEcosostenibilitaService.findById(1L)).thenReturn(valori);
    when(attivitaService.findByValori(valori)).thenReturn(Optional.of(alloggio));

    mockMvc.perform(post("/api/valori/1")
            .param("politicheAntispreco", "false")
            .with(user(altroGestore))
            .with(csrf()))
        .andExpect(status().isNotFound());

    verify(valoriEcosostenibilitaService).findById(1L);
    verify(attivitaService).findByValori(valori);
    verify(valoriEcosostenibilitaService, never()).saveValori(any());
  }

  @Test
  void testModificaValoriEcosostenibilita_AttivitaNonTrovata_NotFound() throws Exception {
    when(valoriEcosostenibilitaService.findById(1L)).thenReturn(valori);
    when(attivitaService.findByValori(valori)).thenReturn(Optional.empty());

    mockMvc.perform(post("/api/valori/1")
            .param("politicheAntispreco", "false")
            .with(user(gestore))
            .with(csrf()))
        .andExpect(status().isNotFound());

    verify(valoriEcosostenibilitaService).findById(1L);
    verify(attivitaService).findByValori(valori);
    verify(valoriEcosostenibilitaService, never()).saveValori(any());
  }

  @Test
  void testModificaValoriEcosostenibilita_Exception() throws Exception {
    when(valoriEcosostenibilitaService.findById(1L))
        .thenThrow(new Exception("Valori non trovati"));

    mockMvc.perform(post("/api/valori/1")
            .param("politicheAntispreco", "false")
            .with(user(gestore))
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(valoriEcosostenibilitaService).findById(1L);
  }
}