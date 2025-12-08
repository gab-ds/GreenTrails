package it.greentrails.backend.entities;

import it.greentrails.backend.enums.StatoPagamento;
import it.greentrails.backend.enums.StatoPrenotazione;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "prenotazione_alloggio")
public class PrenotazioneAlloggio {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "id_itinerario", nullable = false)
  @NotNull(message = "L'itinerario non può essere vuoto.")
  private Itinerario itinerario;

  @ManyToOne(optional = false)
  @JoinColumn(name = "id_camera", nullable = false)
  @NotNull(message = "La camera non può essere vuota.")
  private Camera camera;

  @Column(name = "num_adulti", nullable = false)
  @NotNull(message = "Il numero degli adulti non può essere vuoto.")
  @Positive(message = "Il numero degli adulti non può essere inferiore ad 1.")
  private int numAdulti;

  @Column(name = "num_bambini", nullable = false)
  @NotNull(message = "Il numero degli adulti non può essere vuoto.")
  @PositiveOrZero(message = "Il numero dei bambini non può essere negativo.")
  private int numBambini;

  @Temporal(TemporalType.TIME)
  @Column(name = "data_inizio", nullable = false)
  @NotNull(message = "La data di inizio non può essere vuota.")
  @FutureOrPresent(message = "La data di inizio non può essere antecedente alla data odierna.")
  private Date dataInizio;

  @Temporal(TemporalType.TIME)
  @Column(name = "data_fine", nullable = false)
  @NotNull(message = "La data di fine non può essere vuota.")
  @FutureOrPresent(message = "La data di fine non può essere antecedente alla data odierna.")
  private Date dataFine;

  @Column(name = "num_camere", nullable = false)
  @Positive(message = "Il numero delle camere non può essere inferiore ad 1.")
  private int numCamere;

  @Enumerated
  @Column(name = "stato", nullable = false)
  @NotNull(message = "Lo stato della prenotazione non può essere vuoto.")
  private StatoPrenotazione stato = StatoPrenotazione.NON_CONFERMATA;

  @Enumerated
  @Column(name = "stato_pagamento", nullable = false)
  @NotNull(message = "Lo stato del pagamento non può essere vuoto.")
  private StatoPagamento statoPagamento = StatoPagamento.IN_CORSO;

  @Column(name = "prezzo", nullable = false)
  @NotNull(message = "Il prezzo non può essere vuoto.")
  @PositiveOrZero(message = "Il prezzo non può essere negativo.")
  private double prezzo;

}