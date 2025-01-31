package br.com.powerprogramers.product.domain.controller;

import br.com.powerprogramers.product.api.LoadApi;
import br.com.powerprogramers.product.domain.dto.LoadJobDto;
import br.com.powerprogramers.product.domain.model.Load;
import br.com.powerprogramers.product.domain.service.LoadService;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** Class that controls product load application endpoints. */
@RestController
@RequestMapping
@RequiredArgsConstructor
public class LoadController implements LoadApi {

  private final LoadService loadService;

  @Value("${load.input-path}")
  private String directory;

  /**
   * Loads products from a file.
   *
   * @param file the file containing the products to load
   * @return a response entity indicating the result of the operation
   */
  @Override
  public ResponseEntity<Void> loadProducts(MultipartFile file) {
    this.loadService.load(new Load(file, directory));
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Schedules a job to load products at a specified time.
   *
   * @param dateTime the time to schedule the job
   * @param file the file containing the products to load
   * @return the scheduled job details
   */
  @Override
  public ResponseEntity<LoadJobDto> scheduleJob(OffsetDateTime dateTime, MultipartFile file) {
    return ResponseEntity.ok(
        this.loadService.scheduleJob(dateTime.toLocalDateTime(), new Load(file, directory)));
  }

  /**
   * Cancels a scheduled job by its ID.
   *
   * @param id the ID of the job to cancel
   * @return a response entity indicating the result of the operation
   */
  @Override
  public ResponseEntity<String> cancelJob(String id) {
    if (loadService.cancelJob(id)) {
      return ResponseEntity.ok("Job successfully canceled!");
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Job not found");
    }
  }
}
