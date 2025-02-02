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

/** Configuration class for the batch job to load products. */
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
   * @param initialStep the initial step of the job
   * @param moveStepFiles the step to move processed files
   * @return the configured job
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

  /**
   * Creates the initial step of the job.
   *
   * @param reader the item reader
   * @param writer the item writer
   * @return the configured step
   */
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

  /**
   * Creates a FlatFileItemReader to read product data from a CSV file.
   *
   * @param filePath the path of the file to read
   * @return the configured item reader
   */
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

  /**
   * Creates an ItemWriter to write product data to the database.
   *
   * @param dataSource the data source
   * @return the configured item writer
   */
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

  /**
   * Creates a Tasklet to move processed files to a different directory.
   *
   * @return the configured tasklet
   */
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
          if (!renameFile(file, arquivoDestino)) {
            throw new ProductLoadMoveFileException(file.getName());
          }
        }
      }
      return RepeatStatus.FINISHED;
    };
  }

  /**
   * Creates the step to move processed files.
   *
   * @return the configured step
   */
  @Bean
  public Step moveStepFiles() {
    return new StepBuilder("moveFile", jobRepository)
        .tasklet(moverArquivosTasklet(), transactionManager)
        .allowStartIfComplete(true)
        .build();
  }

  /**
   * Generates a new file name with a timestamp.
   *
   * @param fileName the original file name
   * @return the new file name
   */
  private String newFileName(String fileName) {
    ZoneId zoneId = ZoneId.systemDefault();
    LocalDateTime localDateTime = LocalDateTime.now(zoneId);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
    String formattedDate = localDateTime.format(formatter);
    return fileName.replace(".csv", "-processed-at-") + formattedDate + ".csv";
  }

  /**
   * Renames a file.
   *
   * @param source the source file
   * @param target the target file
   * @return true if the file was renamed, false otherwise
   */
  protected boolean renameFile(File source, File target) {
    return source.renameTo(target);
  }
}
