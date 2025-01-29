package br.com.powerprogramers.product.domain.consumer;

import br.com.powerprogramers.product.domain.model.ProductRequested;
import br.com.powerprogramers.product.domain.service.StockService;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/** Class that represents a product consumer. */
@Component
@RequiredArgsConstructor
public class ProductConsumer {

  private final StockService stockService;

  @Bean(name = "removeStock")
  Consumer<ProductRequested> consumer() {
    return productRequested -> {
      if (productRequested.getAmount() < 1) {
        throw new IllegalArgumentException("The amount cannot be negative or zero.");
      }
      this.stockService.updateStock(productRequested.getId(), productRequested.getAmount() * -1);
    };
  }
}
