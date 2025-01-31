package br.com.powerprogramers.product.domain.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import br.com.powerprogramers.product.domain.dto.ProductDto;
import br.com.powerprogramers.product.domain.entity.ProductEntity;
import br.com.powerprogramers.product.domain.exceptions.ProductNotFoundException;
import br.com.powerprogramers.product.domain.repository.ProductRepository;
import br.com.powerprogramers.product.domain.service.usecase.update.UpdateStockUseCase;
import java.math.BigDecimal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class StockServiceImplTest {

  private AutoCloseable openMocks;

  @Mock private ProductRepository productRepository;
  @Mock private UpdateStockUseCase updateStockUseCase;

  @InjectMocks private StockServiceImpl stockServiceImpl;

  @BeforeEach
  void setUp() {
    openMocks = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  void tearDown() throws Exception {
    openMocks.close();
  }

  @Test
  void mustUpdateStockSuccessfully() {
    Long productId = 1L;
    Integer amount = 50;
    ProductDto productDto =
        new ProductDto()
            .id(productId)
            .name("Orange")
            .description("Argentine sweet orange")
            .amount(100)
            .price(BigDecimal.valueOf(12.5));

    when(updateStockUseCase.execute(productId)).thenReturn(productDto);
    when(productRepository.save(any(ProductEntity.class))).thenAnswer(p -> p.getArgument(0));

    ProductDto result = stockServiceImpl.updateStock(productId, amount);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(productId);
    assertThat(result.getAmount()).isEqualTo(150);
  }

  @Test
  void mustGenerateException_WhenUpdateStock_WithInvalidProduct() {
    Long productId = 1L;
    Integer amount = 50;

    when(updateStockUseCase.execute(productId))
        .thenThrow(new ProductNotFoundException("/products"));

    assertThatThrownBy(() -> stockServiceImpl.updateStock(productId, amount))
        .isInstanceOf(ProductNotFoundException.class)
        .hasMessage("product not found");
  }
}
