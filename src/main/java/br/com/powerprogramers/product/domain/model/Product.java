package br.com.powerprogramers.product.domain.model;

import br.com.powerprogramers.product.domain.dto.UpdateProductDto;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

/** Product model class to validate it. */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {
  private Long id;
  private String name;
  private String description;
  private Integer amount;
  private BigDecimal price;
  private boolean active;

  /** Updates the product's active status to true. */
  public void activate() {
    this.active = true;
  }

  /** Updates the product's active status to false. */
  public void deactivate() {
    this.active = false;
  }

  /**
   * Update the product's atributes with the UpdateProductDto data.
   *
   * @param updateProductDto data to be updated
   */
  public void update(UpdateProductDto updateProductDto) {
    if (!ObjectUtils.isEmpty(updateProductDto.getName())) {
      this.name = updateProductDto.getName();
    }

    if (!ObjectUtils.isEmpty(updateProductDto.getDescription())) {
      this.description = updateProductDto.getDescription();
    }

    if (updateProductDto.getPrice().compareTo(BigDecimal.ZERO) > 0) {
      this.price = updateProductDto.getPrice();
    }
  }

  /**
   * Updates the product's amount.
   *
   * @param amount the amount to be updated
   */
  public void updateAmount(Integer amount) {
    if (this.amount + amount < 0) {
      throw new IllegalArgumentException("The total purchase cannot be more than the stock total.");
    }
    this.amount += amount;
  }
}
