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
@Table(name = "prenotazione_attivita_turistica")
public class PrenotazioneAttivitaTuristica {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "id_itinerario", nullable = false)
  @NotNull(message = "L'itinerario non può essere vuoto.")
  private Itinerario itinerario;

  @ManyToOne(optional = false)
  @JoinColumn(name = "id_attivita_turistica", nullable = false)
  @NotNull(message = "L'attività turistica non può essere vuota.")
  private Attivita attivitaTuristica;

  @Column(name = "num_adulti", nullable = false)
  @NotNull(message = "Il numero degli adulti non può essere vuoto.")
  @Positive(message = "Il numero degli adulti non può essere inferiore ad 1.")
  private int numAdulti;

  @Column(name = "num_bambini", nullable = false)
  @NotNull(message = "Il numero dei bambini non può essere vuoto.")
  @PositiveOrZero(message = "Il numero dei bambini non può essere negativo.")
  private int numBambini;

  @Temporal(TemporalType.TIME)
  @Column(name = "data_inizio", nullable = false)
  @NotNull(message = "La data di inizio non può essere vuota.")
  @FutureOrPresent(message = "La data di inizio non può essere antecedente alla data odierna.")
  private Date dataInizio;

  @Temporal(TemporalType.TIME)
  @Column(name = "data_fine")
  @FutureOrPresent(message = "La data di fine non può essere antecedente alla data odierna.")
  private Date dataFine;

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