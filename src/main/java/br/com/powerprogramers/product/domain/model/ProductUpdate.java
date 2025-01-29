package br.com.powerprogramers.product.domain.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Product class to validate your update. */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductUpdate {
  private Long id;
  private String name;
  private String description;
  private BigDecimal price;
}
