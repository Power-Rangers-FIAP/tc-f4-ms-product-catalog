package br.com.powerprogramers.product.domain.service.usecase.create;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.powerprogramers.product.domain.exceptions.CreateProductUseCaseException;
import br.com.powerprogramers.product.domain.model.Product;
import br.com.powerprogramers.product.domain.utils.ProductHelper;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CreateProductUseCaseTest {

  private CreateProductUseCase createProductUseCase;

  @BeforeEach
  void setUp() {
    createProductUseCase = new CreateProductUseCase();
  }

  @Test
  void mustAllowToCreateProduct() {
    Product product = ProductHelper.generateProduct();

    Product result = createProductUseCase.execute(product);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(product.getId());
    assertThat(result.getName()).isEqualTo(product.getName());
    assertThat(result.getDescription()).isEqualTo(product.getDescription());
    assertThat(result.getAmount()).isEqualTo(product.getAmount());
    assertThat(result.getPrice()).isEqualTo(product.getPrice());
    assertThat(result.isActive()).isEqualTo(product.isActive());
  }

  @Test
  void mustGenerateException_WhenCreateProduct_WithNameIsEmpty() {
    Product product = ProductHelper.generateProductBuilder().name("").build();

    assertThatThrownBy(() -> createProductUseCase.execute(product))
        .isInstanceOf(CreateProductUseCaseException.class)
        .hasMessage("Product name cannot be empty");
  }

  @Test
  void mustGenerateException_WhenCreateProduct_WithDescriptionIsEmpty() {
    Product product = ProductHelper.generateProductBuilder().description("").build();

    assertThatThrownBy(() -> createProductUseCase.execute(product))
        .isInstanceOf(CreateProductUseCaseException.class)
        .hasMessage("Product description cannot be empty");
  }

  @Test
  void mustGenerateException_WhenCreateProduct_WithAmountIsZeroOrNegative() {
    Product product = ProductHelper.generateProductBuilder().amount(-1).build();

    assertThatThrownBy(() -> createProductUseCase.execute(product))
        .isInstanceOf(CreateProductUseCaseException.class)
        .hasMessage("Product amount cannot be zero or negative");
  }

  @Test
  void mustGenerateException_WhenCreateProduct_WithPriceIsZeroOrNegative() {
    Product product = ProductHelper.generateProductBuilder().price(BigDecimal.ZERO).build();

    assertThatThrownBy(() -> createProductUseCase.execute(product))
        .isInstanceOf(CreateProductUseCaseException.class)
        .hasMessage("Product price cannot be zero or negative");
  }
}
