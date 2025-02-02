package br.com.powerprogramers.product.domain.service.usecase.update;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import br.com.powerprogramers.product.domain.dto.ProductDto;
import br.com.powerprogramers.product.domain.exceptions.ProductNotFoundException;
import br.com.powerprogramers.product.domain.service.ProductService;
import br.com.powerprogramers.product.domain.utils.ProductHelper;
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
    ProductDto productDto = ProductHelper.generateProductDto(true);

    when(productService.findById(anyLong())).thenReturn(productDto);

    ProductDto result = updateStockUseCase.execute(ProductHelper.ID);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(ProductHelper.ID);
    assertThat(result.getName()).isEqualTo(ProductHelper.NAME);
    assertThat(result.getDescription()).isEqualTo(ProductHelper.DESCRIPTION);
    assertThat(result.getAmount()).isEqualTo(ProductHelper.AMOUNT);
    assertThat(result.getPrice()).isEqualTo(ProductHelper.PRICE);
  }

  @Test
  void mustGenerateException_WhenUpdateInventory_WithInvalidProduct() {
    when(productService.findById(anyLong())).thenThrow(new ProductNotFoundException("/products"));

    assertThatThrownBy(() -> updateStockUseCase.execute(ProductHelper.ID))
        .isInstanceOf(ProductNotFoundException.class)
        .hasMessage("product not found");
  }
}
