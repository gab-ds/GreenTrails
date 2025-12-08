package it.greentrails.backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "valori_ecosostenibilita")
public class ValoriEcosostenibilita {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "politiche_antispreco")
  private Boolean politicheAntispreco;

  @Column(name = "prodotti_locali")
  private Boolean prodottiLocali;

  @Column(name = "energia_verde")
  private Boolean energiaVerde;

  @Column(name = "raccolta_differenziata")
  private Boolean raccoltaDifferenziata;

  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  @Column(name = "limite_emissione_CO2")
  private Boolean limiteEmissioneCO2;

  @Column(name = "contatto_con_natura")
  private Boolean contattoConNatura;

}