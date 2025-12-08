package it.greentrails.backend.entities;

import it.greentrails.backend.enums.CategorieAlloggio;
import it.greentrails.backend.enums.CategorieAttivitaTuristica;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.geo.Point;

@Getter
@Setter
@Entity
@Table(name = "attivita")
public class Attivita {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "id_gestore", nullable = false)
  @NotNull(message = "Il gestore non può essere vuoto.")
  private Utente gestore;

  @Column(name = "nome", nullable = false, length = 50)
  @NotBlank(message = "Il nome non può essere vuoto.")
  @Size(max = 50, message = "Il nome è troppo lungo.")
  @Pattern(regexp = "^[0-9A-zÀ-ú '-]*", message = "Il nome non ha formato valido.")
  private String nome;

  @Column(name = "indirizzo", nullable = false)
  @NotBlank(message = "L'indirizzo non può essere vuoto.")
  @Size(max = 255, message = "L'indirizzo è troppo lungo.")
  @Pattern(regexp = "^[0-9A-zÀ-ú '-,.]*", message = "L'indirizzo non ha formato valido.")
  private String indirizzo;

  @Column(name = "cap", nullable = false, length = 5)
  @JdbcTypeCode(SqlTypes.CHAR)
  @NotBlank(message = "Il CAP non può essere vuoto.")
  @Size(min = 5, max = 5, message = "Il CAP non ha lunghezza valida.")
  @Pattern(regexp = "^[0-9]*", message = "Il CAP non ha formato valido.")
  private String cap;

  @Column(name = "citta", nullable = false)
  @NotBlank(message = "La città non può essere vuota.")
  @Size(max = 50, message = "La città è troppo lunga.")
  @Pattern(regexp = "^[0-9A-zÀ-ú '-]*", message = "La città non ha formato valido.")
  private String citta;

  @Column(name = "provincia", nullable = false, length = 2)
  @JdbcTypeCode(SqlTypes.CHAR)
  @NotBlank(message = "La provincia non può essere vuota.")
  @Size(min = 2, max = 2, message = "La provincia non ha lunghezza valida.")
  @Pattern(regexp = "^[A-Z]*", message = "La provincia non ha formato valido.")
  private String provincia;

  @Column(name = "coordinate", nullable = false)
  @JdbcTypeCode(SqlTypes.POINT)
  @NotNull(message = "Le coordinate non possono essere vuote.")
  private Point coordinate;

  @Column(name = "prezzo")
  @PositiveOrZero(message = "Il prezzo non può essere negativo.")
  private Double prezzo;

  @Column(name = "descrizione_breve", nullable = false, length = 140)
  @NotBlank(message = "La descrizione breve non può essere vuota.")
  @Size(max = 140, message = "La descrizione breve è troppo lunga.")
  @Pattern(regexp = "^[A-Z](?s:.)*", message = "La descrizione breve non ha formato valido.")
  private String descrizioneBreve;

  @Column(name = "descrizione_lunga", nullable = false, length = 2000)
  @NotBlank(message = "La descrizione lunga non può essere vuota.")
  @Size(max = 2000, message = "La descrizione lunga è troppo lunga.")
  @Pattern(regexp = "^[A-Z](?s:.)*", message = "La descrizione lunga non ha formato valido.")
  private String descrizioneLunga;

  @Column(name = "media", nullable = false)
  @NotNull(message = "I media non possono essere vuoti.")
  private String media;

  @Column(name = "disponibilita")
  @Positive(message = "La disponibilità non può essere inferiore a 1.")
  private Integer disponibilita;

  @OneToOne(optional = false, orphanRemoval = true)
  @JoinColumn(name = "id_valori", nullable = false, unique = true)
  @NotNull(message = "I valori di ecosostenibilità non possono essere vuoti.")
  private ValoriEcosostenibilita valoriEcosostenibilita;

  @Enumerated
  @Column(name = "categoria_alloggio")
  private CategorieAlloggio categoriaAlloggio;

  @Enumerated
  @Column(name = "categoria_attivita_turistica")
  private CategorieAttivitaTuristica categoriaAttivitaTuristica;

  @Column(name = "is_alloggio", nullable = false)
  private boolean isAlloggio = false;

  @Column(name = "eliminata", nullable = false)
  private boolean eliminata = false;

  @ManyToMany
  @JoinTable(name = "assegnazione_categorie",
      joinColumns = @JoinColumn(name = "id_attivita"),
      inverseJoinColumns = @JoinColumn(name = "id_categoria"))
  private Set<Categoria> categorie = new LinkedHashSet<>();

}