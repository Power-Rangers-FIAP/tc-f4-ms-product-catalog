package br.com.powerprogramers.product.domain.consumer;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.powerprogramers.product.domain.dto.ProductDto;
import br.com.powerprogramers.product.domain.model.ProductRequested;
import br.com.powerprogramers.product.domain.service.StockService;
import java.util.function.Consumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductConsumerTest {

  private AutoCloseable openMocks;
  @Mock private StockService stockService;
  private ProductConsumer productConsumer;

  @BeforeEach
  void setUp() {
    openMocks = MockitoAnnotations.openMocks(this);
    productConsumer = new ProductConsumer(stockService);
  }

  @AfterEach
  void tearDown() throws Exception {
    openMocks.close();
  }

  @Test
  void mustConsumeProductSuccessfully() {
    ProductRequested productRequested = new ProductRequested(1L, 10);

    when(stockService.updateStock(anyLong(), anyInt())).thenAnswer(t -> new ProductDto());

    Consumer<ProductRequested> consumer = productConsumer.consumer();
    consumer.accept(productRequested);

    verify(stockService, times(1)).updateStock(1L, -10);
  }

  @Test
  void mustThrowExceptionForInvalidAmount() {
    ProductRequested productRequested = new ProductRequested(1L, 0);

    Consumer<ProductRequested> consumer = productConsumer.consumer();

    assertThatThrownBy(() -> consumer.accept(productRequested))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("The amount cannot be negative or zero.");

    verify(stockService, never()).updateStock(anyLong(), anyInt());
  }
}
