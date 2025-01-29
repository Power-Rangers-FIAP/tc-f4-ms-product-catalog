package br.com.powerprogramers.product.domain.exceptions;

import java.time.Instant;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/** Product creation exception class. */
@Getter
public class CreateProductUseCaseException extends ProductException {

  /**
   * Builder for exception to product creation.
   *
   * @param message Message of the Error Reason
   */
  public CreateProductUseCaseException(String message) {
    super(Instant.now(), HttpStatus.BAD_REQUEST, message, "/products");
  }
}
