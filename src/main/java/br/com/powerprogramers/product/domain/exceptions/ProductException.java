package br.com.powerprogramers.product.domain.exceptions;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/** Generic product error class. */
@Getter
@AllArgsConstructor
public class ProductException extends RuntimeException {
  private final Instant timestamp;
  private final HttpStatus status;
  private final String message;
  private final String path;
}
