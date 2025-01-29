package br.com.powerprogramers.product.domain.exceptions;

import java.time.Instant;
import org.springframework.http.HttpStatus;

/** Exception to product load job. */
public class ProductLoadJobException extends ProductException {
  /**
   * Builder for exception to product load job.
   *
   * @param message message of exception
   */
  public ProductLoadJobException(String message) {
    super(Instant.now(), HttpStatus.INTERNAL_SERVER_ERROR, message, "/products/load");
  }
}
