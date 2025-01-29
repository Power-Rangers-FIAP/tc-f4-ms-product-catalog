package br.com.powerprogramers.product.configurations;

import br.com.powerprogramers.product.domain.service.ProductService;
import br.com.powerprogramers.product.domain.service.usecase.create.CreateProductUseCase;
import br.com.powerprogramers.product.domain.service.usecase.update.UpdateStockUseCase;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

/** Project configuration class. */
@Configuration
public class ApplicationConfig {

  /**
   * CreateProductUseCase instance configuration.
   *
   * @return new CreateProductUseCase
   */
  @Bean
  public CreateProductUseCase createProductUseCase() {
    return new CreateProductUseCase();
  }

  /**
   * UpdateStockUseCase instance configuration.
   *
   * @param productService ProductService instance
   * @return new UpdateStockUseCase
   */
  @Bean
  public UpdateStockUseCase updateStockUseCase(ProductService productService) {
    return new UpdateStockUseCase(productService);
  }

  @Bean
  public TaskScheduler taskScheduler() {
    return new ConcurrentTaskScheduler(new ScheduledThreadPoolExecutor(20));
  }
}
