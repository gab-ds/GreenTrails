package it.greentrails.backend.entities;

import it.greentrails.backend.enums.RuoloUtente;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@Entity
@Table(name = "utente")
public class Utente implements UserDetails {

  @Getter
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "nome", length = 50, nullable = false)
  @NotBlank(message = "Il nome non può essere vuoto.")
  @Size(max = 50, message = "Il nome è troppo lungo.")
  private String nome;

  @Column(name = "cognome", length = 50, nullable = false)
  @NotBlank(message = "Il cognome non può essere vuoto.")
  @Size(max = 50, message = "Il cognome è troppo lungo.")
  private String cognome;

  @Temporal(TemporalType.DATE)
  @Column(name = "data_nascita", nullable = false)
  @NotNull(message = "La data di nascita non può essere vuota.")
  @PastOrPresent(message = "La data di nascita non può essere futura.")
  private Date dataNascita;

  @Column(name = "email", nullable = false, unique = true)
  @NotBlank(message = "L'email non può essere vuota.")
  @Email(message = "Il formato dell'email non è valido.")
  private String email;

  @Column(name = "password", nullable = false)
  @NotBlank(message = "La password non può essere vuota.")
  private String password;

  @Enumerated
  @Column(name = "ruolo", nullable = false)
  @NotNull(message = "Il ruolo non può essere vuoto.")
  private RuoloUtente ruolo = RuoloUtente.VISITATORE;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + ruolo.name()));
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}