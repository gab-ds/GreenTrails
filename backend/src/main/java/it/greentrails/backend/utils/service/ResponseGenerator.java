package it.greentrails.backend.utils.service;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseGenerator {

  private ResponseGenerator() {
  }

  public static ResponseEntity<Object> generateResponse(final HttpStatus statusCode,
      final Object response) {
    Map<String, Object> responseMap = new HashMap<>();
    String message;
    if (statusCode.isError()) {
      if (statusCode.is4xxClientError()) {
        responseMap.put("data", response);
        message = "failure";
      } else {
        responseMap.put("error", response.toString());
        message = "error";
      }
    } else {
      responseMap.put("data", response);
      message = "success";
    }
    responseMap.put("status", message);
    return new ResponseEntity<>(responseMap, statusCode);
  }
}
