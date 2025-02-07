package br.com.powerprogramers.product.domain.utils;

import br.com.powerprogramers.product.domain.dto.CreateProductDto;
import br.com.powerprogramers.product.domain.dto.ProductDto;
import br.com.powerprogramers.product.domain.dto.UpdateProductDto;
import br.com.powerprogramers.product.domain.entity.ProductEntity;
import br.com.powerprogramers.product.domain.model.Product;
import java.math.BigDecimal;

public final class ProductHelper {

  public static final Long ID = 1L;
  public static final String NAME = "Orange";
  public static final String DESCRIPTION = "Argentine sweet orange";
  public static final int AMOUNT = 150;
  public static final BigDecimal PRICE = BigDecimal.valueOf(10.5);

  private ProductHelper() {
    throw new UnsupportedOperationException("This class cannot be instantiated.");
  }

  public static CreateProductDto generateCreateProductDto() {
    return new CreateProductDto().name(NAME).description(DESCRIPTION).amount(AMOUNT).price(PRICE);
  }

  public static UpdateProductDto generateUpdateProductDto() {
    return new UpdateProductDto()
        .name(NAME + " updated")
        .description(DESCRIPTION + " updated")
        .price(PRICE.add(BigDecimal.valueOf(5)));
  }

  public static ProductDto generateProductDto(boolean active) {
    return new ProductDto()
        .id(ID)
        .name(NAME)
        .description(DESCRIPTION)
        .amount(AMOUNT)
        .price(PRICE)
        .active(active);
  }

  public static ProductDto generateProductDtoUpdated() {
    UpdateProductDto updateProductDto = generateUpdateProductDto();
    return new ProductDto()
        .id(ID)
        .name(updateProductDto.getName())
        .description(updateProductDto.getDescription())
        .amount(AMOUNT)
        .price(updateProductDto.getPrice())
        .active(true);
  }

  public static Product generateProduct() {
    return generateProductBuilder().build();
  }

  public static Product.ProductBuilder generateProductBuilder() {
    return Product.builder()
        .id(ID)
        .name(NAME)
        .description(DESCRIPTION)
        .amount(AMOUNT)
        .price(PRICE)
        .active(true);
  }

  public static ProductEntity generateProductEntity(boolean active) {
    return ProductEntity.builder()
        .id(ID)
        .name(NAME)
        .description(DESCRIPTION)
        .amount(AMOUNT)
        .price(PRICE)
        .active(active)
        .build();
  }
}
