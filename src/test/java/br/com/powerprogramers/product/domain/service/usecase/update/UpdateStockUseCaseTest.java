package br.com.powerprogramers.product.domain.service.usecase.update;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import br.com.powerprogramers.product.domain.dto.ProductDto;
import br.com.powerprogramers.product.domain.exceptions.ProductNotFoundException;
import br.com.powerprogramers.product.domain.service.ProductService;
import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UpdateStockUseCaseTest {

  private AutoCloseable openMocks;
  @Mock private ProductService productService;

  @InjectMocks private UpdateStockUseCase updateStockUseCase;

  @BeforeEach
  void setUp() {
    openMocks = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  void tearDown() throws Exception {
    openMocks.close();
  }

  @Test
  void mustUpdateInventorySuccessfully() {
    Long productId = 1L;
    ProductDto productDto = new ProductDto();
    productDto.setId(productId);
    productDto.setName("Orange");
    productDto.setDescription("Argentine sweet orange");
    productDto.setAmount(100);
    productDto.setPrice(BigDecimal.valueOf(12.5));

    when(productService.findById(productId)).thenReturn(productDto);

    ProductDto result = updateStockUseCase.execute(productId);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(productId);
    assertThat(result.getName()).isEqualTo("Orange");
    assertThat(result.getDescription()).isEqualTo("Argentine sweet orange");
    assertThat(result.getAmount()).isEqualTo(100);
    assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(12.5));
  }

  @Test
  void mustGenerateException_WhenUpdateInventory_WithInvalidProduct() {
    Long productId = 1L;

    when(productService.findById(productId)).thenThrow(new ProductNotFoundException("/products"));

    assertThatThrownBy(() -> updateStockUseCase.execute(productId))
        .isInstanceOf(ProductNotFoundException.class)
        .hasMessage("product not found");
  }
}
