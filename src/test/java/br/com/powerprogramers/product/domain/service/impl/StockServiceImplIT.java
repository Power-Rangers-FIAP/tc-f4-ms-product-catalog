package br.com.powerprogramers.product.domain.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.powerprogramers.product.domain.dto.ProductDto;
import br.com.powerprogramers.product.domain.exceptions.ProductNotFoundException;
import br.com.powerprogramers.product.domain.repository.ProductRepository;
import br.com.powerprogramers.product.domain.service.StockService;
import br.com.powerprogramers.product.domain.service.usecase.update.UpdateStockUseCase;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class StockServiceImplIT {

  private final StockService stockService;

  @Autowired
  public StockServiceImplIT(
      ProductRepository productRepository, UpdateStockUseCase updateStockUseCase) {
    this.stockService = new StockServiceImpl(productRepository, updateStockUseCase);
  }

  @Test
  void mustUpdateStockSuccessfully() {
    Long productId = 1L;
    Integer amount = 50;

    ProductDto result = stockService.updateStock(productId, amount);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(productId);
    assertThat(result.getName()).isEqualTo("Orange");
    assertThat(result.getDescription()).isEqualTo("Argentine sweet orange");
    assertThat(result.getAmount()).isEqualTo(200);
    assertThat(result.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(10.5));
    assertThat(result.isActive()).isTrue();
  }

  @Test
  void mustGenerateException_WhenUpdateStock_WithInvalidProduct() {
    Long productId = 0L;
    Integer amount = 50;

    assertThatThrownBy(() -> stockService.updateStock(productId, amount))
        .isInstanceOf(ProductNotFoundException.class)
        .hasMessage("product not found");
  }
}
