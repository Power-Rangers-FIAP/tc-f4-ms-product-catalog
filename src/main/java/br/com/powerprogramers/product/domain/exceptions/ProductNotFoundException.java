package br.com.powerprogramers.product.domain.exceptions;

import java.time.Instant;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/** Product not found exception class. */
@Getter
public class ProductNotFoundException extends ProductException {

  /**
   * Builder for exception to product not found.
   *
   * @param path path of exception
   */
  public ProductNotFoundException(String path) {
    super(Instant.now(), HttpStatus.NOT_FOUND, "product not found", path);
  }
}
