package it.greentrails.backend.utils.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ResponseGeneratorTest {

  @Test
  void testGenerateResponse_Success_WithData() {
    // Given
    String data = "Test data";
    HttpStatus status = HttpStatus.OK;

    // When
    ResponseEntity<Object> response = ResponseGenerator.generateResponse(status, data);

    // Then
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody() instanceof Map);

    @SuppressWarnings("unchecked")
    Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
    assertEquals("success", responseBody.get("status"));
    assertEquals(data, responseBody.get("data"));
  }

  @Test
  void testGenerateResponse_Success_WithCreated() {
    // Given
    String data = "Created successfully";
    HttpStatus status = HttpStatus.CREATED;

    // When
    ResponseEntity<Object> response = ResponseGenerator.generateResponse(status, data);

    // Then
    assertNotNull(response);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertTrue(response.getBody() instanceof Map);

    @SuppressWarnings("unchecked")
    Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
    assertEquals("success", responseBody.get("status"));
    assertEquals(data, responseBody.get("data"));
  }

  @Test
  void testGenerateResponse_ClientError_BadRequest() {
    // Given
    String errorMessage = "Invalid input";
    HttpStatus status = HttpStatus.BAD_REQUEST;

    // When
    ResponseEntity<Object> response = ResponseGenerator.generateResponse(status, errorMessage);

    // Then
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(response.getBody() instanceof Map);

    @SuppressWarnings("unchecked")
    Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
    assertEquals("failure", responseBody.get("status"));
    assertEquals(errorMessage, responseBody.get("data"));
  }

  @Test
  void testGenerateResponse_ClientError_NotFound() {
    // Given
    String errorMessage = "Resource not found";
    HttpStatus status = HttpStatus.NOT_FOUND;

    // When
    ResponseEntity<Object> response = ResponseGenerator.generateResponse(status, errorMessage);

    // Then
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertTrue(response.getBody() instanceof Map);

    @SuppressWarnings("unchecked")
    Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
    assertEquals("failure", responseBody.get("status"));
    assertEquals(errorMessage, responseBody.get("data"));
  }

  @Test
  void testGenerateResponse_ClientError_Conflict() {
    // Given
    String errorMessage = "Conflict occurred";
    HttpStatus status = HttpStatus.CONFLICT;

    // When
    ResponseEntity<Object> response = ResponseGenerator.generateResponse(status, errorMessage);

    // Then
    assertNotNull(response);
    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertTrue(response.getBody() instanceof Map);

    @SuppressWarnings("unchecked")
    Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
    assertEquals("failure", responseBody.get("status"));
    assertEquals(errorMessage, responseBody.get("data"));
  }

  @Test
  void testGenerateResponse_ServerError_InternalServerError() {
    // Given
    String errorMessage = "Internal server error";
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    // When
    ResponseEntity<Object> response = ResponseGenerator.generateResponse(status, errorMessage);

    // Then
    assertNotNull(response);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertTrue(response.getBody() instanceof Map);

    @SuppressWarnings("unchecked")
    Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
    assertEquals("error", responseBody.get("status"));
    assertEquals(errorMessage, responseBody.get("error"));
  }

  @Test
  void testGenerateResponse_ServerError_ServiceUnavailable() {
    // Given
    String errorMessage = "Service unavailable";
    HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;

    // When
    ResponseEntity<Object> response = ResponseGenerator.generateResponse(status, errorMessage);

    // Then
    assertNotNull(response);
    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    assertTrue(response.getBody() instanceof Map);

    @SuppressWarnings("unchecked")
    Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
    assertEquals("error", responseBody.get("status"));
    assertEquals(errorMessage, responseBody.get("error"));
  }

  @Test
  void testGenerateResponse_Success_WithComplexObject() {
    // Given
    Map<String, Object> complexData = Map.of(
        "id", 123,
        "name", "Test",
        "active", true
    );
    HttpStatus status = HttpStatus.OK;

    // When
    ResponseEntity<Object> response = ResponseGenerator.generateResponse(status, complexData);

    // Then
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody() instanceof Map);

    @SuppressWarnings("unchecked")
    Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
    assertEquals("success", responseBody.get("status"));
    assertEquals(complexData, responseBody.get("data"));
  }

  @Test
  void testGenerateResponse_Success_NoContent() {
    // Given
    HttpStatus status = HttpStatus.NO_CONTENT;

    // When
    ResponseEntity<Object> response = ResponseGenerator.generateResponse(status, null);

    // Then
    assertNotNull(response);
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertTrue(response.getBody() instanceof Map);

    @SuppressWarnings("unchecked")
    Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
    assertEquals("success", responseBody.get("status"));
    assertNull(responseBody.get("data"));
  }
}