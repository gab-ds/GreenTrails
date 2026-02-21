package it.greentrails.backend.gestionericerca.controller;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Categoria;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.entities.ValoriEcosostenibilita;
import it.greentrails.backend.enums.RuoloUtente;
import it.greentrails.backend.gestioneattivita.service.CategoriaService;
import it.greentrails.backend.gestionericerca.service.RicercaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.geo.Point;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RicercaControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private RicercaService ricercaService;

  @MockBean
  private CategoriaService categoriaService;

  private Attivita attivita1;
  private Attivita attivita2;
  private Attivita attivita3;
  private Categoria categoria1;
  private Categoria categoria2;
  private List<Attivita> tutteAttivita;
  private List<Attivita> attivitaFiltrate;

  @BeforeEach
  void setUp() {
    Utente gestore = new Utente();
    gestore.setId(1L);
    gestore.setEmail("gestore@test.com");
    gestore.setRuolo(RuoloUtente.GESTORE_ATTIVITA);

    ValoriEcosostenibilita valori = new ValoriEcosostenibilita();
    valori.setId(1L);

    // Attività a Roma
    attivita1 = new Attivita();
    attivita1.setId(1L);
    attivita1.setNome("Hotel Eco Roma");
    attivita1.setGestore(gestore);
    attivita1.setAlloggio(true);
    attivita1.setCoordinate(new Point(41.9028, 12.4964)); // Roma
    attivita1.setValoriEcosostenibilita(valori);

    // Attività a Milano
    attivita2 = new Attivita();
    attivita2.setId(2L);
    attivita2.setNome("Tour Verde Milano");
    attivita2.setGestore(gestore);
    attivita2.setAlloggio(false);
    attivita2.setPrezzo(50.0);
    attivita2.setCoordinate(new Point(45.4642, 9.1900)); // Milano
    attivita2.setValoriEcosostenibilita(valori);

    // Attività a Napoli
    attivita3 = new Attivita();
    attivita3.setId(3L);
    attivita3.setNome("B&B Napoli");
    attivita3.setGestore(gestore);
    attivita3.setAlloggio(true);
    attivita3.setCoordinate(new Point(40.8518, 14.2681)); // Napoli
    attivita3.setValoriEcosostenibilita(valori);

    categoria1 = new Categoria();
    categoria1.setId(1L);
    categoria1.setNome("Eco-friendly");

    categoria2 = new Categoria();
    categoria2.setId(2L);
    categoria2.setNome("Natura");

    tutteAttivita = Arrays.asList(attivita1, attivita2, attivita3);
    attivitaFiltrate = Arrays.asList(attivita1, attivita3);
  }

  @Test
  void testCerca_SoloQuery() throws Exception {
    when(ricercaService.findAttivita("hotel")).thenReturn(tutteAttivita);

    mockMvc.perform(post("/api/ricerca")
            .param("query", "hotel")
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(3));

    verify(ricercaService).findAttivita("hotel");
  }

  @Test
  void testCerca_ConCategorie() throws Exception {
    when(ricercaService.findAttivita("eco")).thenReturn(tutteAttivita);
    when(categoriaService.findById(1L)).thenReturn(categoria1);
    when(categoriaService.findById(2L)).thenReturn(categoria2);
    when(ricercaService.findAttivitaByCategorie(anyList())).thenReturn(attivitaFiltrate);

    mockMvc.perform(post("/api/ricerca")
            .param("query", "eco")
            .param("idCategorie", "1", "2")
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(2));

    verify(ricercaService).findAttivita("eco");
    verify(categoriaService).findById(1L);
    verify(categoriaService).findById(2L);
    verify(ricercaService).findAttivitaByCategorie(anyList());
  }

  @Test
  void testCerca_ConPosizione() throws Exception {
    when(ricercaService.findAttivita("roma")).thenReturn(tutteAttivita);
    when(ricercaService.findAttivitaByPosizione(any(Point.class), eq(10.0)))
        .thenReturn(Arrays.asList(attivita1));

    mockMvc.perform(post("/api/ricerca")
            .param("query", "roma")
            .param("latitudine", "41")
            .param("longitudine", "12")
            .param("raggio", "10.0")
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(1));

    verify(ricercaService).findAttivita("roma");
    verify(ricercaService).findAttivitaByPosizione(any(Point.class), eq(10.0));
  }

  @Test
  void testCerca_ConCategorieEPosizione() throws Exception {
    when(ricercaService.findAttivita("hotel")).thenReturn(tutteAttivita);
    when(categoriaService.findById(1L)).thenReturn(categoria1);
    when(ricercaService.findAttivitaByCategorie(anyList())).thenReturn(attivitaFiltrate);
    when(ricercaService.findAttivitaByPosizione(any(Point.class), eq(50.0)))
        .thenReturn(Arrays.asList(attivita1, attivita3));

    mockMvc.perform(post("/api/ricerca")
            .param("query", "hotel")
            .param("idCategorie", "1")
            .param("latitudine", "41")
            .param("longitudine", "12")
            .param("raggio", "50.0")
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(2));

    verify(ricercaService).findAttivita("hotel");
    verify(categoriaService).findById(1L);
    verify(ricercaService).findAttivitaByCategorie(anyList());
    verify(ricercaService).findAttivitaByPosizione(any(Point.class), eq(50.0));
  }

  @Test
  void testCerca_QueryVuota() throws Exception {
    when(ricercaService.findAttivita("")).thenReturn(new ArrayList<>());

    mockMvc.perform(post("/api/ricerca")
            .param("query", "")
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(0));

    verify(ricercaService).findAttivita("");
  }

  @Test
  void testCerca_NessunRisultato() throws Exception {
    when(ricercaService.findAttivita("nonEsiste")).thenReturn(new ArrayList<>());

    mockMvc.perform(post("/api/ricerca")
            .param("query", "nonEsiste")
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(0));

    verify(ricercaService).findAttivita("nonEsiste");
  }

  @Test
  void testCerca_CategorieVuote() throws Exception {
    when(ricercaService.findAttivita("hotel")).thenReturn(tutteAttivita);

    mockMvc.perform(post("/api/ricerca")
            .param("query", "hotel")
            .param("idCategorie", "")
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(3));

    verify(ricercaService).findAttivita("hotel");
    verify(ricercaService, never()).findAttivitaByCategorie(anyList());
  }

  @Test
  void testCerca_SoloPosizioneSenzaRaggio() throws Exception {
    when(ricercaService.findAttivita("hotel")).thenReturn(tutteAttivita);

    mockMvc.perform(post("/api/ricerca")
            .param("query", "hotel")
            .param("latitudine", "41")
            .param("longitudine", "12")
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(3));

    verify(ricercaService).findAttivita("hotel");
    verify(ricercaService, never()).findAttivitaByPosizione(any(Point.class), anyDouble());
  }

  @Test
  void testCercaPerPosizione_Success() throws Exception {
    when(ricercaService.findAttivitaByPosizione(any(Point.class), eq(20.0)))
        .thenReturn(Arrays.asList(attivita1, attivita3));

    mockMvc.perform(post("/api/ricerca/perPosizione")
            .param("latitudine", "41.9")
            .param("longitudine", "12.5")
            .param("raggio", "20.0")
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(2));

    verify(ricercaService).findAttivitaByPosizione(any(Point.class), eq(20.0));
  }

  @Test
  void testCercaPerPosizione_ConCategorie() throws Exception {
    when(ricercaService.findAttivitaByPosizione(any(Point.class), eq(30.0)))
        .thenReturn(tutteAttivita);
    when(categoriaService.findById(1L)).thenReturn(categoria1);
    when(ricercaService.findAttivitaByCategorie(anyList())).thenReturn(attivitaFiltrate);

    mockMvc.perform(post("/api/ricerca/perPosizione")
            .param("latitudine", "42.0")
            .param("longitudine", "13.0")
            .param("raggio", "30.0")
            .param("idCategorie", "1")
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(2));

    verify(ricercaService).findAttivitaByPosizione(any(Point.class), eq(30.0));
    verify(categoriaService).findById(1L);
    verify(ricercaService).findAttivitaByCategorie(anyList());
  }

  @Test
  void testCercaPerPosizione_ConPiuCategorie() throws Exception {
    when(ricercaService.findAttivitaByPosizione(any(Point.class), eq(15.0)))
        .thenReturn(tutteAttivita);
    when(categoriaService.findById(1L)).thenReturn(categoria1);
    when(categoriaService.findById(2L)).thenReturn(categoria2);
    when(ricercaService.findAttivitaByCategorie(anyList())).thenReturn(attivitaFiltrate);

    mockMvc.perform(post("/api/ricerca/perPosizione")
            .param("latitudine", "40.8")
            .param("longitudine", "14.3")
            .param("raggio", "15.0")
            .param("idCategorie", "1", "2")
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(2));

    verify(ricercaService).findAttivitaByPosizione(any(Point.class), eq(15.0));
    verify(categoriaService).findById(1L);
    verify(categoriaService).findById(2L);
    verify(ricercaService).findAttivitaByCategorie(anyList());
  }

  @Test
  void testCercaPerPosizione_NessunRisultato() throws Exception {
    when(ricercaService.findAttivitaByPosizione(any(Point.class), eq(5.0)))
        .thenReturn(new ArrayList<>());

    mockMvc.perform(post("/api/ricerca/perPosizione")
            .param("latitudine", "60.0")
            .param("longitudine", "10.0")
            .param("raggio", "5.0")
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(0));

    verify(ricercaService).findAttivitaByPosizione(any(Point.class), eq(5.0));
  }

  @Test
  void testCercaPerPosizione_CategorieVuote() throws Exception {
    when(ricercaService.findAttivitaByPosizione(any(Point.class), eq(25.0)))
        .thenReturn(tutteAttivita);

    mockMvc.perform(post("/api/ricerca/perPosizione")
            .param("latitudine", "45.5")
            .param("longitudine", "9.2")
            .param("raggio", "25.0")
            .param("idCategorie", "")
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(3));

    verify(ricercaService).findAttivitaByPosizione(any(Point.class), eq(25.0));
    verify(ricercaService, never()).findAttivitaByCategorie(anyList());
  }

  @Test
  void testCercaPerPosizione_RaggioGrande() throws Exception {
    when(ricercaService.findAttivitaByPosizione(any(Point.class), eq(100.0)))
        .thenReturn(tutteAttivita);

    mockMvc.perform(post("/api/ricerca/perPosizione")
            .param("latitudine", "41.9")
            .param("longitudine", "12.5")
            .param("raggio", "100.0")
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(3));

    verify(ricercaService).findAttivitaByPosizione(any(Point.class), eq(100.0));
  }

  @Test
  void testCercaPerPosizione_CoordinateDecimali() throws Exception {
    when(ricercaService.findAttivitaByPosizione(any(Point.class), eq(12.5)))
        .thenReturn(Arrays.asList(attivita1));

    mockMvc.perform(post("/api/ricerca/perPosizione")
            .param("latitudine", "41.9028")
            .param("longitudine", "12.4964")
            .param("raggio", "12.5")
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(1));

    verify(ricercaService).findAttivitaByPosizione(any(Point.class), eq(12.5));
  }

  @Test
  void testCerca_ConUnaSolaCategoria() throws Exception {
    when(ricercaService.findAttivita("hotel")).thenReturn(tutteAttivita);
    when(categoriaService.findById(1L)).thenReturn(categoria1);
    when(ricercaService.findAttivitaByCategorie(anyList())).thenReturn(attivitaFiltrate);

    mockMvc.perform(post("/api/ricerca")
            .param("query", "hotel")
            .param("idCategorie", "1")
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(2));

    verify(ricercaService).findAttivita("hotel");
    verify(categoriaService).findById(1L);
    verify(ricercaService).findAttivitaByCategorie(anyList());
  }

  @Test
  void testCercaPerPosizione_ConUnaSolaCategoria() throws Exception {
    when(ricercaService.findAttivitaByPosizione(any(Point.class), eq(30.0)))
        .thenReturn(tutteAttivita);
    when(categoriaService.findById(2L)).thenReturn(categoria2);
    when(ricercaService.findAttivitaByCategorie(anyList())).thenReturn(Arrays.asList(attivita2));

    mockMvc.perform(post("/api/ricerca/perPosizione")
            .param("latitudine", "45.5")
            .param("longitudine", "9.2")
            .param("raggio", "30.0")
            .param("idCategorie", "2")
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(1));

    verify(ricercaService).findAttivitaByPosizione(any(Point.class), eq(30.0));
    verify(categoriaService).findById(2L);
    verify(ricercaService).findAttivitaByCategorie(anyList());
  }
}


