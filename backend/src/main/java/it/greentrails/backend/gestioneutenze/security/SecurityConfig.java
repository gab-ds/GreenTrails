package it.greentrails.backend.gestioneutenze.security;

import it.greentrails.backend.enums.RuoloUtente;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.List;


@Configuration
@EnableWebSecurity
@Order(SecurityProperties.BASIC_AUTH_ORDER - 10)
public class SecurityConfig {

  private static final String ROLE_VISITATORE = RuoloUtente.VISITATORE.name();
  private static final String ROLE_GESTORE = RuoloUtente.GESTORE_ATTIVITA.name();
  private static final String ROLE_ADMIN = RuoloUtente.AMMINISTRATORE.name();

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  public SecurityConfig(final JwtAuthenticationFilter jwtAuthenticationFilter) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(
      final AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    configuration.setAllowedOrigins(Arrays.asList(
        "http://localhost:9000",
        "http://localhost:4200",
        "http://frontend",
        "http://frontend:80"
    ));

    configuration.setAllowedMethods(Arrays.asList(
        "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
    ));

    configuration.setAllowedHeaders(List.of("*"));

    configuration.setAllowCredentials(true);

    configuration.setExposedHeaders(Arrays.asList(
        "Authorization",
        "Content-Type",
        "X-Requested-With"
    ));

    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests((authorize) -> authorize
            .requestMatchers(HttpMethod.PUT, "/api/utenti").permitAll()
            .requestMatchers("/api/auth/**").permitAll()
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

            .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
            .requestMatchers("/actuator/**").authenticated()

            .anyRequest().authenticated()
        )
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(jwtAuthenticationFilter,
            UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

}
