package br.com.powerprogramers.product.domain.exceptions;

import java.time.Instant;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/** Exception to product load. */
@Getter
public class ProductLoadMoveFileException extends ProductException {
  /** Builder for exception to product load. */
  public ProductLoadMoveFileException(String fileName) {
    super(
        Instant.now(),
        HttpStatus.CONFLICT,
        "Unable to move file: %s".formatted(fileName),
        "/products/load");
  }
}
