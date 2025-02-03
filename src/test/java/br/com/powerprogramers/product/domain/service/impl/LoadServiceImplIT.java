package br.com.powerprogramers.product.domain.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.powerprogramers.product.domain.batch.ProductBatchExecutor;
import br.com.powerprogramers.product.domain.dto.LoadJobDto;
import br.com.powerprogramers.product.domain.exceptions.ProductException;
import br.com.powerprogramers.product.domain.exceptions.ProductLoadJobException;
import br.com.powerprogramers.product.domain.model.Load;
import br.com.powerprogramers.product.domain.service.LoadService;
import br.com.powerprogramers.product.domain.utils.LoadHelper;
import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureTestDatabase
class LoadServiceImplIT {

  private final LoadService loadService;

  @Autowired
  public LoadServiceImplIT(ProductBatchExecutor productBatchExecutor) {
    this.loadService = new LoadServiceImpl(productBatchExecutor);
  }

  @Test
  void mustCarryProductSuccessfully() {
    Load load = LoadHelper.generateFileSuccessfully();

    loadService.load(load);

    File[] files = Path.of(load.getPath() + "/processed").toFile().listFiles();
    assertThat(files).isNotEmpty();

    LoadHelper.deleteTestFiles(load);
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
