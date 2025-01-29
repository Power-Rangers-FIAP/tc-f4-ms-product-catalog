package br.com.powerprogramers.product.domain.batch.job;

import br.com.powerprogramers.product.domain.exceptions.ProductLoadMoveFileException;
import br.com.powerprogramers.product.domain.model.ProductLoad;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

/** Class that represents the configuration of the batch job to load products. */
@Configuration
@RequiredArgsConstructor
public class LoadConfiguration {

  private final PlatformTransactionManager transactionManager;
  private final JobRepository jobRepository;

  @Value("${load.input-path}")
  private String directory;

  /**
   * Creates a job to load products.
   *
   * @param initialStep initial step
   * @param moveStepFiles
   * @return
   */
  @Bean
  public Job jobLoadProduct(
      @Qualifier("initialStep") Step initialStep, @Qualifier("moveStepFiles") Step moveStepFiles) {
    return new JobBuilder("importFile", jobRepository)
        .start(initialStep)
        .next(moveStepFiles)
        .incrementer(new RunIdIncrementer())
        .build();
  }

  @Bean
  public Step initialStep(
      @Qualifier("reader") ItemReader<ProductLoad> reader,
      @Qualifier("writer") ItemWriter<ProductLoad> writer) {
    return new StepBuilder("initialStep", jobRepository)
        .<ProductLoad, ProductLoad>chunk(200, transactionManager)
        .reader(reader)
        .writer(writer)
        .allowStartIfComplete(true)
        .build();
  }

  @Bean
  @StepScope
  public FlatFileItemReader<ProductLoad> reader(
      @Value("#{jobParameters['file']}") String filePath) {
    return new FlatFileItemReaderBuilder<ProductLoad>()
        .name("readingCsv")
        .resource(new FileSystemResource(filePath))
        .comments("--")
        .delimited()
        .delimiter(";")
        .names("name", "description", "amount", "price", "active")
        .fieldSetMapper(new ProductLoadMapper())
        .build();
  }

  @Bean
  public ItemWriter<ProductLoad> writer(DataSource dataSource) {
    return new JdbcBatchItemWriterBuilder<ProductLoad>()
        .dataSource(dataSource)
        .sql(
            " INSERT INTO product (name, description, amount, price, active) "
                + " VALUES (:name, :description, :amount, :price, :active) ")
        .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
        .build();
  }

  @Bean
  public Tasklet moverArquivosTasklet() {
    return (contribution, chunkContext) -> {
      File originFolder = new File(directory);
      File destinyFolder = new File(directory + "/processed");

      if (!destinyFolder.exists()) {
        Files.createDirectories(destinyFolder.toPath());
      }

      File[] archives = originFolder.listFiles((dir, name) -> name.endsWith(".csv"));

      if (archives != null) {
        for (File file : archives) {
          File arquivoDestino = new File(destinyFolder, newFileName(file.getName()));
          if (!file.renameTo(arquivoDestino)) {
            throw new ProductLoadMoveFileException(file.getName());
          }
        }
      }
      return RepeatStatus.FINISHED;
    };
  }

  private String newFileName(String fileName) {
    ZoneId zoneId = ZoneId.systemDefault();
    LocalDateTime localDateTime = LocalDateTime.now(zoneId);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
    String formattedDate = localDateTime.format(formatter);
    return fileName.replace(".csv", "-processed-at-") + formattedDate + ".csv";
  }

  @Bean
  public Step moveStepFiles() {
    return new StepBuilder("moveFile", jobRepository)
        .tasklet(moverArquivosTasklet(), transactionManager)
        .allowStartIfComplete(true)
        .build();
  }
}
