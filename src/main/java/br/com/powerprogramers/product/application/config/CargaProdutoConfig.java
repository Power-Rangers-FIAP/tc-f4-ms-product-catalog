package br.com.powerprogramers.product.application.config;

import br.com.powerprogramers.product.application.job.Product;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

/** Spring batch load configuration class to load products in batch. */
@Configuration
public class CargaProdutoConfig {

  @Value("${load.input-path}")
  private String directory;

  /**
   * Creates and configures a Job for the product load process.
   *
   * @param jobRepository Repository of jobs that will be used to create the job.
   * @param initialStep First step of the job.
   * @return Configured instance of the Job.
   */
  @Bean
  public Job processProductLoad(
      JobRepository jobRepository, @Qualifier("initialStep") Step initialStep) {
    return new JobBuilder("import-products", jobRepository).start(initialStep).build();
  }

  /**
   * Creates and configures the initial step for the job.
   *
   * @param jobRepository Repository of jobs that will be used to create the step.
   * @param transactionManager Transaction manager for handling transactions within the step.
   * @param reader Item reader for reading Product items.
   * @param writer Item writer for writing Product items.
   * @return Configured instance of the Step.
   */
  @Bean
  public Step initialStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      @Qualifier("reader") ItemReader<Product> reader,
      @Qualifier("writer") ItemWriter<Product> writer) {
    return new StepBuilder("initial-step", jobRepository)
        .<Product, Product>chunk(200, transactionManager)
        .reader(reader)
        .writer(writer)
        .taskExecutor(new SimpleAsyncTaskExecutor())
        .build();
  }

  /**
   * Creates and configures the ItemReader for reading Product items from a CSV file.
   *
   * @return Configured instance of the ItemReader.
   */
  @Bean
  public ItemReader<Product> reader() {
    BeanWrapperFieldSetMapper<Product> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
    fieldSetMapper.setTargetType(Product.class);

    return new FlatFileItemReaderBuilder<Product>()
        .name("productItemReader")
        .resource(new FileSystemResource(directory + "/product.csv"))
        .delimited()
        .names("name", "description", "amount", "price", "active")
        .fieldSetMapper(fieldSetMapper)
        .build();
  }

  /**
   * Creates and configures the ItemWriter for writing Product items to a database.
   *
   * @param dataSource Data source for connecting to the database.
   * @return Configured instance of the ItemWriter.
   */
  @Bean
  public ItemWriter<Product> writer(DataSource dataSource) {
    return new JdbcBatchItemWriterBuilder<Product>()
        .dataSource(dataSource)
        .sql(
            """
            INSERT INTO products(name, description, amount, price, active)
            VALUES (:name, :description, :amount, :price, :active)
            """)
        .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
        .build();
  }
}
