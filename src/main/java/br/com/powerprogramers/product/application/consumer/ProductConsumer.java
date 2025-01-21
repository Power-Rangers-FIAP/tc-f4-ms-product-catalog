package br.com.powerprogramers.product.application.consumer;

import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductConsumer {

  private final JobLauncher jobLauncher;
  private final Job processProductLoad;

  @Value("${load.input-path}")
  private String directory;

  @Bean(name = "loadProducts")
  public Consumer<ProductRequested> loadProducts() {
    JobParameters jobParameters = new JobParameters();
    return product -> {
      try {
        this.jobLauncher.run(processProductLoad, jobParameters);
      } catch (JobExecutionAlreadyRunningException
          | JobParametersInvalidException
          | JobInstanceAlreadyCompleteException
          | JobRestartException e) {
        throw new RuntimeException(e);
      }
    };
  }
}
