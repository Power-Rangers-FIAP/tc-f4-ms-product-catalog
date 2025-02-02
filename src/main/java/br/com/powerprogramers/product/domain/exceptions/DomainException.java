package br.com.powerprogramers.product.domain.exceptions;

import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.context.request.WebRequest;

/**
 * Record that represents domain errors.
 *
 * @param timestamp exception time
 * @param status Http error status code
 * @param message reason for the error
 * @param path Way of Error
 */
public record DomainException(Instant timestamp, int status, String message, String path) {

  /**
   * Builds a new instance of DominioException with the given parameters.
   *
   * @param statusCode the HTTP status code
   * @param message the exception message
   * @param request the web request
   * @return a new instance of DominioException
   */
  public static DomainException build(
      HttpStatusCode statusCode, String message, WebRequest request) {
    String path = request == null ? "" : request.getDescription(false).substring(4);
    return new DomainException(Instant.now(), statusCode.value(), message, path);
  }

  /**
   * Create a DomainException from the input parameter.
   *
   * @param ex ProductException
   * @param request WebRequest
   * @return a new DominioException
   */
  public static DomainException from(ProductException ex, WebRequest request) {
    return new DomainException(
        Instant.now(),
        ex.getStatus().value(),
        ex.getMessage(),
        request.getDescription(false).substring(4));
  }

  /**
   * Create a DomainException from the input parameter.
   *
   * @param status HttpStatus
   * @param message description of exception
   * @param request WebRequest
   * @return a new DominioException
   */
  public static DomainException from(HttpStatus status, String message, WebRequest request) {
    return new DomainException(
        Instant.now(), status.value(), message, request.getDescription(false).substring(4));
  }
}
