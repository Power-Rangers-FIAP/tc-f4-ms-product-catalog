package br.com.powerprogramers.product.domain.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.powerprogramers.product.domain.dto.CreateProductDto;
import br.com.powerprogramers.product.domain.dto.PagedProductDto;
import br.com.powerprogramers.product.domain.dto.ProductDto;
import br.com.powerprogramers.product.domain.dto.UpdateProductDto;
import br.com.powerprogramers.product.domain.exceptions.ProductNotFoundException;
import br.com.powerprogramers.product.domain.repository.ProductRepository;
import br.com.powerprogramers.product.domain.service.ProductService;
import br.com.powerprogramers.product.domain.service.usecase.create.CreateProductUseCase;
import br.com.powerprogramers.product.domain.utils.ProductHelper;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class ProductServiceImplIT {

  private final ProductService productService;

  @Autowired
  public ProductServiceImplIT(
      ProductRepository productRepository, CreateProductUseCase createProductUseCase) {
    this.productService = new ProductServiceImpl(productRepository, createProductUseCase);
  }

  @Test
  void mustFindProductByIdSuccessfully() {
    Long productId = 1L;

    ProductDto result = productService.findById(productId);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(productId);
    assertThat(result.getName()).isEqualTo("Orange");
    assertThat(result.getDescription()).isEqualTo("Argentine sweet orange");
    assertThat(result.getAmount()).isEqualTo(150);
    assertThat(result.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(10.5));
    assertThat(result.isActive()).isTrue();
  }

  @Test
  void mustGenerateException_WhenFindProductById_WithInvalidProduct() {
    Long productId = 0L;

    assertThatThrownBy(() -> productService.findById(productId))
        .isInstanceOf(ProductNotFoundException.class)
        .hasMessage("product not found");
  }

  @Test
  void mustFindAllProductsSuccessfully() {

    PagedProductDto result = productService.findAll(0, 10, "Orange", null, true);

    assertThat(result).isNotNull();
    assertThat(result.getItems()).hasSizeGreaterThanOrEqualTo(1);
    assertThat(result.getPage()).isZero();
    assertThat(result.getPerPage()).isEqualTo(10);
    assertThat(result.getTotal()).isPositive();
    assertThat(result.getItems().get(0).getId()).isEqualTo(1L);
    assertThat(result.getItems().get(0).getName()).isEqualTo("Orange");
    assertThat(result.getItems().get(0).getDescription()).isEqualTo("Argentine sweet orange");
    assertThat(result.getItems().get(0).getAmount()).isEqualTo(150);
    assertThat(result.getItems().get(0).getPrice()).isEqualByComparingTo(BigDecimal.valueOf(10.5));
    assertThat(result.getItems().get(0).isActive()).isTrue();
  }

  @Test
  void mustSaveProductSuccessfully() {
    CreateProductDto createProductDto = ProductHelper.generateCreateProductDto();

    ProductDto result = productService.save(createProductDto);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isNotNull();
    assertThat(result.getName()).isEqualTo("Orange");
    assertThat(result.getDescription()).isEqualTo("Argentine sweet orange");
    assertThat(result.getAmount()).isEqualTo(150);
    assertThat(result.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(10.5));
    assertThat(result.isActive()).isTrue();
  }

  @Test
  void mustUpdateProductSuccessfully() {
    Long productId = 1L;
    UpdateProductDto updateProductDto = new UpdateProductDto();
    updateProductDto.setName("Orange - altered");
    updateProductDto.setDescription("Argentine sweet orange - altered");
    updateProductDto.setPrice(BigDecimal.valueOf(25.0));

    ProductDto result = productService.update(productId, updateProductDto);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(productId);
    assertThat(result.getName()).isEqualTo(updateProductDto.getName());
    assertThat(result.getDescription()).isEqualTo(updateProductDto.getDescription());
    assertThat(result.getAmount()).isEqualTo(150);
    assertThat(result.getPrice()).isEqualByComparingTo(updateProductDto.getPrice());
    assertThat(result.isActive()).isTrue();
  }

  @Test
  void mustActivateProductSuccessfully() {
    Long productId = 2L;

    ProductDto result = productService.activate(productId);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(productId);
    assertThat(result.isActive()).isTrue();
  }

  @Test
  void mustDeactivateProductSuccessfully() {
    Long productId = 1L;

    ProductDto result = productService.deactivate(productId);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(productId);
    assertThat(result.isActive()).isFalse();
  }
}
