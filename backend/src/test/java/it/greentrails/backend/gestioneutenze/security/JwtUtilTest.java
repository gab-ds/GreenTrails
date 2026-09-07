package it.greentrails.backend.gestioneutenze.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class JwtUtilTest {

  @Autowired
  private JwtUtil jwtUtil;

  private static final Long TEST_ID = 1L;
  private static final String TEST_EMAIL = "mario@test.it";
  private static final String TEST_NOME = "Mario";
  private static final String TEST_COGNOME = "Rossi";
  private static final String TEST_RUOLO = "VISITATORE";

  @Test
  void generateToken_returnsNonNullToken() {
    final String token = jwtUtil.generateToken(TEST_ID, TEST_EMAIL,
        TEST_NOME, TEST_COGNOME, TEST_RUOLO);

    assertNotNull(token);
    assertFalse(token.isEmpty());
  }

  @Test
  void extractEmail_returnsCorrectEmail() {
    final String token = jwtUtil.generateToken(TEST_ID, TEST_EMAIL,
        TEST_NOME, TEST_COGNOME, TEST_RUOLO);

    assertEquals(TEST_EMAIL, jwtUtil.extractEmail(token));
  }

  @Test
  void extractId_returnsCorrectId() {
    final String token = jwtUtil.generateToken(TEST_ID, TEST_EMAIL,
        TEST_NOME, TEST_COGNOME, TEST_RUOLO);

    assertEquals(TEST_ID, jwtUtil.extractId(token));
  }

  @Test
  void extractNome_returnsCorrectNome() {
    final String token = jwtUtil.generateToken(TEST_ID, TEST_EMAIL,
        TEST_NOME, TEST_COGNOME, TEST_RUOLO);

    assertEquals(TEST_NOME, jwtUtil.extractNome(token));
  }

  @Test
  void extractCognome_returnsCorrectCognome() {
    final String token = jwtUtil.generateToken(TEST_ID, TEST_EMAIL,
        TEST_NOME, TEST_COGNOME, TEST_RUOLO);

    assertEquals(TEST_COGNOME, jwtUtil.extractCognome(token));
  }

  @Test
  void extractRuolo_returnsCorrectRuolo() {
    final String token = jwtUtil.generateToken(TEST_ID, TEST_EMAIL,
        TEST_NOME, TEST_COGNOME, TEST_RUOLO);

    assertEquals(TEST_RUOLO, jwtUtil.extractRuolo(token));
  }

  @Test
  void validateToken_validToken_returnsTrue() {
    final String token = jwtUtil.generateToken(TEST_ID, TEST_EMAIL,
        TEST_NOME, TEST_COGNOME, TEST_RUOLO);

    assertTrue(jwtUtil.validateToken(token));
  }

  @Test
  void validateToken_tamperedToken_returnsFalse() {
    final String token = jwtUtil.generateToken(TEST_ID, TEST_EMAIL,
        TEST_NOME, TEST_COGNOME, TEST_RUOLO);
    final String tampered = token.substring(0, token.length() - 5) + "XXXXX";

    assertFalse(jwtUtil.validateToken(tampered));
  }

  @Test
  void validateToken_invalidString_returnsFalse() {
    assertFalse(jwtUtil.validateToken("not.a.valid.jwt.token"));
  }

  @Test
  void validateToken_emptyString_returnsFalse() {
    assertFalse(jwtUtil.validateToken(""));
  }
}
