package br.com.powerprogramers.product.configurations;

import br.com.powerprogramers.product.domain.service.ProductService;
import br.com.powerprogramers.product.domain.service.usecase.create.CreateProductUseCase;
import br.com.powerprogramers.product.domain.service.usecase.update.UpdateStockUseCase;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

/** Configuration class for the application. */
@Configuration
public class ApplicationConfig {

  /**
   * Provides a CreateProductUseCase instance.
   *
   * @return a new CreateProductUseCase instance
   */
  @Bean
  public CreateProductUseCase createProductUseCase() {
    return new CreateProductUseCase();
  }

  /**
   * Provides an UpdateStockUseCase instance.
   *
   * @param productService the ProductService instance
   * @return a new UpdateStockUseCase instance
   */
  @Bean
  public UpdateStockUseCase updateStockUseCase(ProductService productService) {
    return new UpdateStockUseCase(productService);
  }

  /**
   * Provides a TaskScheduler instance.
   *
   * @return a new TaskScheduler instance
   */
  @Bean
  public TaskScheduler taskScheduler() {
    return new ConcurrentTaskScheduler(new ScheduledThreadPoolExecutor(20));
  }
}
