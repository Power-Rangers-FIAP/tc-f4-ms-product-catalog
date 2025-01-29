package br.com.powerprogramers.product.domain.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Class that represents a product load.
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductLoad {
  private String name;
  private String description;
  private Integer amount;
  private BigDecimal price;
  private boolean active;
}
