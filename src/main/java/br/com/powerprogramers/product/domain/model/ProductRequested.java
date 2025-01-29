package br.com.powerprogramers.product.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Class that represents a product requested. */
@Getter
@AllArgsConstructor
public class ProductRequested {
  private Long id;
  private Integer amount;
}
