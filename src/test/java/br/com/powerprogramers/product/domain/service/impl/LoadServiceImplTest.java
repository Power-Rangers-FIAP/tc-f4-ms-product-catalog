package br.com.powerprogramers.product.domain.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import br.com.powerprogramers.product.domain.batch.ProductBatchExecutor;
import br.com.powerprogramers.product.domain.dto.LoadJobDto;
import br.com.powerprogramers.product.domain.exceptions.ProductException;
import br.com.powerprogramers.product.domain.exceptions.ProductLoadJobException;
import br.com.powerprogramers.product.domain.model.Load;
import br.com.powerprogramers.product.domain.service.LoadService;
import br.com.powerprogramers.product.domain.utils.LoadHelper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class LoadServiceImplTest {

  private AutoCloseable openMocks;
  @Mock private ProductBatchExecutor productBatchExecutor;
  @Mock private LoadService loadService;

  @BeforeEach
  void setUp() {
    openMocks = MockitoAnnotations.openMocks(this);
    loadService = new LoadServiceImpl(productBatchExecutor);
  }

  @AfterEach
  void tearDown() throws Exception {
    openMocks.close();
  }

  @Test
  void mustCarryProductSuccessfully() throws IOException {
    Load load = LoadHelper.generateFileSuccessfully();

    doNothing().when(productBatchExecutor).execute(load);

    loadService.load(load);

    verify(productBatchExecutor, times(1)).execute(load);

    Path filePath = load.getFullPath();
    assertThat(Files.exists(filePath)).isTrue();

    Files.delete(filePath);
    assertThat(Files.exists(filePath)).isFalse();
  }

  @Test
  void mustGenerateException_WhenCarryProduct() {
    Load load = LoadHelper.generateFileWithError();

    assertThatThrownBy(() -> this.loadService.load(load))
        .isInstanceOf(ProductException.class)
        .hasMessageContaining("Illegal char <?> at index 4: test?.csv");
  }

  @Test
  void mustScheduleJobSuccessfully() {
    LocalDateTime startTime = LocalDateTime.now().plusDays(1);
    Load load = LoadHelper.generateFileSuccessfully();

    LoadJobDto result = loadService.scheduleJob(startTime, load);

    assertThat(result).isNotNull();
    assertThat(result.getId()).startsWith("job-");
    assertThat(result.getScheduledDate()).isEqualTo(startTime.atOffset(ZoneOffset.UTC));
  }

  @Test
  void mustGenerateException_WhenScheduleJob_WithInvalidDate() {
    LocalDateTime startTime = LocalDateTime.now().minusDays(1);
    Load load = LoadHelper.generateFileSuccessfully();

    assertThatThrownBy(() -> loadService.scheduleJob(startTime, load))
        .isInstanceOf(ProductLoadJobException.class)
        .hasMessage("invalid execution date");
  }

  @Test
  void mustCancelJobSuccessfully() {
    LocalDateTime startTime = LocalDateTime.now().plusDays(1);
    Load load = LoadHelper.generateFileSuccessfully();

    LoadJobDto result = loadService.scheduleJob(startTime, load);
    String jobId = result.getId();

    assertThat(loadService.cancelJob(jobId)).isTrue();
  }

  @Test
  void mustCancelJobThatNotExists() {
    assertThat(loadService.cancelJob("1")).isFalse();
  }
}
