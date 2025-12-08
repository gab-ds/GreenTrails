package it.greentrails.backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "recensione")
public class Recensione {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "id_visitatore")
  @NotNull(message = "Il visitatore non può essere vuoto.")
  private Utente visitatore;

  @ManyToOne(optional = false)
  @JoinColumn(name = "id_attivita", nullable = false)
  @NotNull(message = "L'attività non può essere vuota.")
  private Attivita attivita;

  @Column(name = "valutazione_stelle_esperienza", nullable = false)
  @NotNull(message = "La valutazione dell'esperienza non può essere vuota.")
  @PositiveOrZero(message = "La valutazione dell'esperienza non può essere negativa.")
  @Max(value = 5, message = "La valutazione dell'esperienza non può essere superiore a 5.")
  private int valutazioneStelleEsperienza;

  @Column(name = "descrizione")
  @Size(max = 255, message = "La valutazione discorsiva è troppo lunga.")
  @Pattern(regexp = "^$|^[A-Za-zÀ-ÖØ-öø-ÿ0-9][\\s\\S]*$",
      message = "La valutazione discorsiva non ha un formato valido.")
  private String descrizione;

  @Column(name = "media")
  private String media;

  @ManyToOne(optional = false)
  @JoinColumn(name = "id_valori", nullable = false)
  @NotNull(message = "I valori di ecosostenibilità non possono essere vuoti.")
  private ValoriEcosostenibilita valoriEcosostenibilita;

}