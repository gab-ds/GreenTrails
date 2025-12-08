package it.greentrails.backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "camera")
public class Camera {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "id_alloggio", nullable = false)
  @NotNull(message = "L'alloggio non può essere vuoto.")
  private Attivita alloggio;

  @Column(name = "tipo_camera", nullable = false, length = 50)
  @NotNull(message = "La tipologia della camera non può essere vuota.")
  private String tipoCamera;

  @Column(name = "prezzo", nullable = false)
  @NotNull(message = "Il prezzo non può essere vuoto.")
  @PositiveOrZero(message = "Il prezzo non può essere negativo.")
  private double prezzo;

  @Column(name = "disponibilita", nullable = false)
  @NotNull(message = "La disponibilità non può essere vuota.")
  @Positive(message = "La disponibilità non può essere inferiore a 1.")
  private Integer disponibilita;

  @Column(name = "descrizione", nullable = false, length = 140)
  @NotBlank(message = "La descrizione non può essere vuota.")
  @Size(max = 140, message = "La descrizione è troppo lunga.")
  private String descrizione;

  @Column(name = "capienza", nullable = false)
  @NotNull(message = "La capienza non può essere vuota.")
  @Positive(message = "La capienza non può essere inferiore a 1.")
  private int capienza;

}