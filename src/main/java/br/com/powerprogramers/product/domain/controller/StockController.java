package br.com.powerprogramers.product.domain.controller;

import br.com.powerprogramers.product.api.StockApi;
import br.com.powerprogramers.product.domain.dto.ProductDto;
import br.com.powerprogramers.product.domain.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Class that controls stock application endpoints. */
@RestController
@RequestMapping
@RequiredArgsConstructor
public class StockController implements StockApi {

  private final StockService stockService;

  /**
   * Updates the stock of a product.
   *
   * @param id the ID of the product to update
   * @param amount the new stock amount
   * @return the updated product
   */
  @Override
  public ResponseEntity<ProductDto> updateStock(Long id, Integer amount) {
    return ResponseEntity.ok(this.stockService.updateStock(id, amount));
  }
}
