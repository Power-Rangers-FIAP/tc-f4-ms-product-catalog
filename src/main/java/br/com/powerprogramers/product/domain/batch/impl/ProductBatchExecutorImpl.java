package br.com.powerprogramers.product.domain.batch.impl;

import br.com.powerprogramers.product.domain.batch.ProductBatchExecutor;
import br.com.powerprogramers.product.domain.exceptions.ProductLoadJobException;
import br.com.powerprogramers.product.domain.model.Load;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Class that represents a product batch executor implementation. */
@Component
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class ProductBatchExecutorImpl implements ProductBatchExecutor {

  private final JobLauncher jobLauncher;
  private final Job jobLoadProduct;

  @Override
  public void execute(Load load) {

    try {
      JobParameters jobParameters =
          new JobParametersBuilder()
              .addLong("startAt", System.currentTimeMillis())
              .addString("file", load.getFullPath().toString())
              .toJobParameters();
      JobExecution jobEx = jobLauncher.run(jobLoadProduct, jobParameters);

      if (jobEx.getStatus() == BatchStatus.FAILED) {
        String errorMessage =
            jobEx.getAllFailureExceptions().stream()
                .map(Throwable::getMessage)
                .collect(Collectors.joining(", "));
        throw new ProductLoadJobException("Error processing file: " + errorMessage);
      }

    } catch (Exception e) {
      throw new ProductLoadJobException(e.getMessage());
    }
  }
}
