package br.com.powerprogramers.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The main application class for the Spring Boot application. This class serves as the entry point
 * for the application.
 */
@SpringBootApplication
@EnableScheduling
public class ProductApplication {

  /**
   * The main method that serves as the entry point for the Spring Boot application.
   *
   * @param args command-line arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(ProductApplication.class, args);
  }
}
