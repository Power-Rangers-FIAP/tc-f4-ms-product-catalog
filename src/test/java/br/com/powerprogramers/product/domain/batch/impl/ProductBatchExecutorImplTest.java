package br.com.powerprogramers.product.domain.batch.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import br.com.powerprogramers.product.domain.batch.ProductBatchExecutor;
import br.com.powerprogramers.product.domain.exceptions.ProductLoadJobException;
import br.com.powerprogramers.product.domain.model.Load;
import br.com.powerprogramers.product.domain.utils.LoadHelper;
import java.util.Collections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;

class ProductBatchExecutorImplTest {

  private AutoCloseable openMocks;
  @Mock private JobLauncher jobLauncher;
  @Mock private Job jobLoadProduct;
  private ProductBatchExecutor productBatchExecutor;

  @BeforeEach
  void setUp() {
    openMocks = MockitoAnnotations.openMocks(this);
    productBatchExecutor = new ProductBatchExecutorImpl(jobLauncher, jobLoadProduct);
  }

  @AfterEach
  void tearDown() throws Exception {
    openMocks.close();
  }

  @Test
  void mustLoadSuccessfully() throws Exception {
    Load load = LoadHelper.generateFileSuccessfully();
    JobExecution jobExecution = mock(JobExecution.class);
    when(jobExecution.getStatus()).thenReturn(BatchStatus.COMPLETED);
    when(jobLauncher.run(any(Job.class), any(JobParameters.class))).thenReturn(jobExecution);

    assertDoesNotThrow(() -> productBatchExecutor.execute(load));
  }

  @Test
  void mustGenerateException_WhenTestExecuteFailure() throws Exception {
    Load load = LoadHelper.generateFileSuccessfully();
    JobExecution jobExecution = mock(JobExecution.class);
    when(jobExecution.getStatus()).thenReturn(BatchStatus.FAILED);
    when(jobExecution.getAllFailureExceptions())
        .thenReturn(Collections.singletonList(new RuntimeException("Test exception")));
    when(jobLauncher.run(any(Job.class), any(JobParameters.class))).thenReturn(jobExecution);

    ProductLoadJobException exception =
        assertThrows(ProductLoadJobException.class, () -> productBatchExecutor.execute(load));
    assertEquals("Error processing file: Test exception", exception.getMessage());
  }

  @Test
  void mustGenerateException_WhenTestExecuteThrowsException() throws Exception {
    Load load = LoadHelper.generateFileSuccessfully();
    when(jobLauncher.run(any(Job.class), any(JobParameters.class)))
        .thenThrow(new RuntimeException("Launcher exception"));

    ProductLoadJobException exception =
        assertThrows(ProductLoadJobException.class, () -> productBatchExecutor.execute(load));
    assertEquals("Launcher exception", exception.getMessage());
  }
}
