package it.greentrails.backend.entities;

import it.greentrails.backend.enums.PreferenzeAlimentari;
import it.greentrails.backend.enums.PreferenzeAlloggio;
import it.greentrails.backend.enums.PreferenzeAttivita;
import it.greentrails.backend.enums.PreferenzeBudget;
import it.greentrails.backend.enums.PreferenzeStagione;
import it.greentrails.backend.enums.PreferenzeViaggio;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "preferenze")
public class Preferenze {

  @Id
  @Column(name = "id_visitatore", nullable = false)
  private Long id;

  @OneToOne(optional = false, cascade = CascadeType.ALL)
  @PrimaryKeyJoinColumn
  @NotNull(message = "Il visitatore non può essere vuoto.")
  private Utente visitatore;

  @Enumerated
  @Column(name = "viaggio_preferito", nullable = false)
  @NotNull(message = "Il viaggio preferito non può essere vuoto.")
  private PreferenzeViaggio viaggioPreferito;

  @Enumerated
  @Column(name = "alloggio_preferito", nullable = false)
  @NotNull(message = "L'alloggio preferito non può essere vuoto.")
  private PreferenzeAlloggio alloggioPreferito;

  @Enumerated
  @Column(name = "preferenza_alimentare", nullable = false)
  @NotNull(message = "La preferenza alimentare non può essere vuota.")
  private PreferenzeAlimentari preferenzaAlimentare;

  @Enumerated
  @Column(name = "attivita_preferita", nullable = false)
  @NotNull(message = "L'attività preferita non può essere vuota.")
  private PreferenzeAttivita attivitaPreferita;

  @Column(name = "animale_domestico", nullable = false)
  private boolean animaleDomestico = false;

  @Enumerated
  @Column(name = "budget_preferito", nullable = false)
  @NotNull(message = "Il budget preferito non può essere vuoto.")
  private PreferenzeBudget budgetPreferito;

  @Column(name = "souvenir", nullable = false)
  private boolean souvenir = false;

  @Enumerated
  @Column(name = "stagioni_preferite", nullable = false)
  @NotNull(message = "Le stagioni preferite non possono essere vuote.")
  private PreferenzeStagione stagioniPreferite;

}