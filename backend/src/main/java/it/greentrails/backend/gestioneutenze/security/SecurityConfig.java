package it.greentrails.backend.gestioneutenze.security;

import it.greentrails.backend.enums.RuoloUtente;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@Order(SecurityProperties.BASIC_AUTH_ORDER - 10)
public class SecurityConfig {

  private static final String ROLE_VISITATORE = RuoloUtente.VISITATORE.name();
  private static final String ROLE_GESTORE = RuoloUtente.GESTORE_ATTIVITA.name();
  private static final String ROLE_ADMIN = RuoloUtente.AMMINISTRATORE.name();

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests((authorize) -> authorize
            .requestMatchers(HttpMethod.PUT, "/api/utenti").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/utenti").authenticated()
            .requestMatchers(HttpMethod.GET, "/api/utenti/preferenze").hasRole(ROLE_VISITATORE)

            .requestMatchers(HttpMethod.POST, "/api/utenti/questionario").hasRole(ROLE_VISITATORE)

            .requestMatchers(HttpMethod.POST, "/api/file").authenticated()
            .requestMatchers(HttpMethod.DELETE, "/api/file/**").authenticated()
            .requestMatchers(HttpMethod.GET, "/api/file/**").permitAll()

            .requestMatchers(HttpMethod.GET, "/api/attivita/*").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/attivita/perPrezzo").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/attivita/perGestore").hasRole(ROLE_GESTORE)
            .requestMatchers(HttpMethod.GET, "/api/attivita/alloggi").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/attivita/attivitaTuristiche").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/attivita/all").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/attivita").hasRole(ROLE_GESTORE)
            .requestMatchers(HttpMethod.POST, "/api/attivita").hasRole(ROLE_GESTORE)
            .requestMatchers(HttpMethod.POST, "/api/attivita/*").hasRole(ROLE_GESTORE)
            .requestMatchers(HttpMethod.DELETE, "/api/attivita/*").hasRole(ROLE_GESTORE)

            .requestMatchers(HttpMethod.GET, "/api/camere/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/camere").hasRole(ROLE_GESTORE)
            .requestMatchers(HttpMethod.DELETE, "/api/camere/*").hasRole(ROLE_GESTORE)

            .requestMatchers("/api/categorie/**").hasRole(ROLE_GESTORE)

            .requestMatchers(HttpMethod.GET, "/api/recensioni/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/recensioni").hasRole(ROLE_VISITATORE)
            .requestMatchers(HttpMethod.DELETE, "/api/recensioni/*").authenticated()

            .requestMatchers(HttpMethod.GET, "/api/valori/*").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/valori").authenticated()
            .requestMatchers(HttpMethod.POST, "/api/valori/*").hasAnyRole(ROLE_GESTORE, ROLE_ADMIN)

            .requestMatchers("/api/itinerari/**").hasRole(ROLE_VISITATORE)

            .requestMatchers(HttpMethod.GET, "/api/prenotazioni-alloggio")
            .hasRole(ROLE_VISITATORE)
            .requestMatchers(HttpMethod.GET, "/api/prenotazioni-attivita-turistica")
            .hasRole(ROLE_VISITATORE)
            .requestMatchers(HttpMethod.GET, "/api/prenotazioni-alloggio/*")
            .hasRole(ROLE_VISITATORE)
            .requestMatchers(HttpMethod.GET, "/api/prenotazioni-attivita-turistica/*")
            .hasRole(ROLE_VISITATORE)
            .requestMatchers(HttpMethod.GET, "/api/prenotazioni-alloggio/perAttivita/*")
            .hasRole(ROLE_GESTORE)
            .requestMatchers(HttpMethod.GET, "/api/prenotazioni-attivita-turistica/perAttivita/*")
            .hasRole(ROLE_GESTORE)
            .requestMatchers(HttpMethod.POST, "/api/prenotazioni-alloggio")
            .hasRole(ROLE_VISITATORE)
            .requestMatchers(HttpMethod.POST, "/api/prenotazioni-attivita-turistica")
            .hasRole(ROLE_VISITATORE)
            .requestMatchers(HttpMethod.DELETE, "/api/prenotazioni-alloggio/*")
            .hasRole(ROLE_VISITATORE)
            .requestMatchers(HttpMethod.DELETE, "/api/prenotazioni-attivita-turistica/*")
            .hasRole(ROLE_VISITATORE)

            .requestMatchers("/api/ricerca/**").permitAll()

            .requestMatchers(HttpMethod.POST, "/api/segnalazioni").authenticated()
            .requestMatchers(HttpMethod.GET, "/api/segnalazioni/**").hasRole(ROLE_ADMIN)
            .requestMatchers(HttpMethod.DELETE, "/api/segnalazioni/*").hasRole(ROLE_ADMIN)

            .anyRequest().authenticated()
        )
        .httpBasic(Customizer.withDefaults())
        .csrf(AbstractHttpConfigurer::disable);

    return http.build();
  }

}