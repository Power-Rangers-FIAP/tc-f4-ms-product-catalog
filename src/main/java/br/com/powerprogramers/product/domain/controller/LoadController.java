package br.com.powerprogramers.product.domain.controller;

import br.com.powerprogramers.product.api.LoadApi;
import br.com.powerprogramers.product.domain.dto.LoadJobDto;
import br.com.powerprogramers.product.domain.exceptions.ProductLoadJobException;
import br.com.powerprogramers.product.domain.model.Load;
import br.com.powerprogramers.product.domain.service.LoadService;
import java.io.IOException;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** LoadController. */
@RestController
@RequestMapping
@RequiredArgsConstructor
public class LoadController implements LoadApi {

  private final LoadService loadService;

  @Value("${load.input-path}")
  private String directory;

  @Override
  public ResponseEntity<Void> loadProducts(MultipartFile file) {

    try {
      this.loadService.load(new Load(file, directory));
    } catch (IOException e) {
      throw new ProductLoadJobException(e.getMessage());
    }

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Override
  public ResponseEntity<LoadJobDto> scheduleJob(OffsetDateTime dateTime, MultipartFile file) {
    try {
      return ResponseEntity.ok(
          this.loadService.scheduleJob(dateTime.toLocalDateTime(), new Load(file, directory)));
    } catch (IOException e) {
      throw new ProductLoadJobException(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<String> cancelJob(String id) {
    if (loadService.cancelJob(id)) {
      return ResponseEntity.ok("Job successfully canceled!");
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Job not found");
    }
  }
}
