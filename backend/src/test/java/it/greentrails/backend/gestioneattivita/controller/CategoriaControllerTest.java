package it.greentrails.backend.gestioneattivita.controller;

import it.greentrails.backend.entities.Attivita;
import it.greentrails.backend.entities.Categoria;
import it.greentrails.backend.entities.Utente;
import it.greentrails.backend.entities.ValoriEcosostenibilita;
import it.greentrails.backend.enums.CategorieAlloggio;
import it.greentrails.backend.enums.RuoloUtente;
import it.greentrails.backend.gestioneattivita.service.AttivitaService;
import it.greentrails.backend.gestioneattivita.service.CategoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.geo.Point;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CategoriaControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private CategoriaService categoriaService;

  @MockBean
  private AttivitaService attivitaService;

  private Utente utente;
  private Attivita alloggio;
  private Categoria categoria;
  private ValoriEcosostenibilita valori;

  @BeforeEach
  void setUp() {
    utente = new Utente();
    utente.setId(1L);
    utente.setEmail("gestore@test.com");
    utente.setRuolo(RuoloUtente.GESTORE_ATTIVITA);

    valori = new ValoriEcosostenibilita();
    valori.setId(1L);

    categoria = new Categoria();
    categoria.setId(1L);
    categoria.setNome("Relax e Benessere");
    categoria.setDescrizione("Attività e alloggi per il relax e il benessere");

    alloggio = new Attivita();
    alloggio.setId(1L);
    alloggio.setAlloggio(true);
    alloggio.setGestore(utente);
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
    alloggio.setCategorie(new LinkedHashSet<>());
  }

  @Test
  void testAggiungiCategoria_Success() throws Exception {
    when(categoriaService.findById(1L)).thenReturn(categoria);
    when(attivitaService.findById(1L)).thenReturn(alloggio);
    when(attivitaService.saveAttivita(any(Attivita.class))).thenReturn(alloggio);

    mockMvc.perform(post("/api/categorie/1")
            .param("idAttivita", "1")
            .with(user(utente))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(categoriaService).findById(1L);
    verify(attivitaService).findById(1L);
    verify(attivitaService).saveAttivita(any(Attivita.class));
  }

  @Test
  void testAggiungiCategoria_AttivitaNonTrovata() throws Exception {
    Utente altroUtente = new Utente();
    altroUtente.setId(2L);
    altroUtente.setEmail("altro@test.com");
    altroUtente.setRuolo(RuoloUtente.GESTORE_ATTIVITA);

    when(categoriaService.findById(1L)).thenReturn(categoria);
    when(attivitaService.findById(1L)).thenReturn(alloggio);

    mockMvc.perform(post("/api/categorie/1")
            .param("idAttivita", "1")
            .with(user(altroUtente))
            .with(csrf()))
        .andExpect(status().isNotFound());

    verify(categoriaService).findById(1L);
    verify(attivitaService).findById(1L);
    verify(attivitaService, never()).saveAttivita(any());
  }

  @Test
  void testAggiungiCategoria_Exception() throws Exception {
    when(categoriaService.findById(1L)).thenThrow(new Exception("Categoria non trovata"));

    mockMvc.perform(post("/api/categorie/1")
            .param("idAttivita", "1")
            .with(user(utente))
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(categoriaService).findById(1L);
  }

  @Test
  void testVisualizzaCategoria_Success() throws Exception {
    when(categoriaService.findById(1L)).thenReturn(categoria);

    mockMvc.perform(get("/api/categorie/1")
            .with(user(utente))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(categoriaService).findById(1L);
  }

  @Test
  void testVisualizzaCategoria_Exception() throws Exception {
    when(categoriaService.findById(999L)).thenThrow(new Exception("Categoria non trovata"));

    mockMvc.perform(get("/api/categorie/999")
            .with(user(utente))
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(categoriaService).findById(999L);
  }

  @Test
  void testRimuoviCategoria_Success() throws Exception {
    Set<Categoria> categorie = new LinkedHashSet<>();
    categorie.add(categoria);
    alloggio.setCategorie(categorie);

    when(categoriaService.findById(1L)).thenReturn(categoria);
    when(attivitaService.findById(1L)).thenReturn(alloggio);
    when(attivitaService.saveAttivita(any(Attivita.class))).thenReturn(alloggio);

    mockMvc.perform(delete("/api/categorie/1")
            .param("idAttivita", "1")
            .with(user(utente))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(categoriaService).findById(1L);
    verify(attivitaService).findById(1L);
    verify(attivitaService).saveAttivita(any(Attivita.class));
  }

  @Test
  void testRimuoviCategoria_AttivitaNonTrovata() throws Exception {
    Utente altroUtente = new Utente();
    altroUtente.setId(2L);
    altroUtente.setEmail("altro@test.com");
    altroUtente.setRuolo(RuoloUtente.GESTORE_ATTIVITA);

    when(categoriaService.findById(1L)).thenReturn(categoria);
    when(attivitaService.findById(1L)).thenReturn(alloggio);

    mockMvc.perform(delete("/api/categorie/1")
            .param("idAttivita", "1")
            .with(user(altroUtente))
            .with(csrf()))
        .andExpect(status().isNotFound());

    verify(categoriaService).findById(1L);
    verify(attivitaService).findById(1L);
    verify(attivitaService, never()).saveAttivita(any());
  }

  @Test
  void testRimuoviCategoria_Exception() throws Exception {
    when(categoriaService.findById(1L)).thenThrow(new Exception("Categoria non trovata"));

    mockMvc.perform(delete("/api/categorie/1")
            .param("idAttivita", "1")
            .with(user(utente))
            .with(csrf()))
        .andExpect(status().isInternalServerError());

    verify(categoriaService).findById(1L);
  }
}