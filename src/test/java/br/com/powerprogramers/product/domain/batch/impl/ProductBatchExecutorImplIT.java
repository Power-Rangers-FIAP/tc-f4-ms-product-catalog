package br.com.powerprogramers.product.domain.batch.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import br.com.powerprogramers.product.domain.batch.ProductBatchExecutor;
import br.com.powerprogramers.product.domain.exceptions.ProductLoadJobException;
import br.com.powerprogramers.product.domain.model.Load;
import br.com.powerprogramers.product.domain.utils.LoadHelper;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureTestDatabase
class ProductBatchExecutorImplIT {

  private final ProductBatchExecutor productBatchExecutor;

  @Autowired
  public ProductBatchExecutorImplIT(JobLauncher jobLauncher, Job jobLoadProduct) {
    this.productBatchExecutor = new ProductBatchExecutorImpl(jobLauncher, jobLoadProduct);
  }

  @Test
  void mustLoadSuccessfully() throws IOException {
    Load load = LoadHelper.generateFileSuccessfullyFromRoot();
    assertDoesNotThrow(() -> productBatchExecutor.execute(load));
    LoadHelper.deleteTestFiles(load);
  }

  @Test
  void testExecuteFailure() throws IOException {
    Load load = LoadHelper.generateFileSuccessfullyFromRoot();

    assertDoesNotThrow(() -> productBatchExecutor.execute(load));
    assertThatThrownBy(() -> productBatchExecutor.execute(load))
        .isInstanceOf(ProductLoadJobException.class)
        .hasMessageStartingWith("Error processing file:");

    LoadHelper.deleteTestFiles(load);
  }

  @Test
  void testExecuteThrowsException() throws IOException {
    Load load = LoadHelper.generateFileWithErrorFromRoot();
    assertThatThrownBy(() -> productBatchExecutor.execute(load))
        .isInstanceOf(ProductLoadJobException.class)
        .hasMessageStartingWith("Illegal char <?> at index 8: products?.csv");
  }
}
