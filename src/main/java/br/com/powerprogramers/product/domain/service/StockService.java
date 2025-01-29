package br.com.powerprogramers.product.domain.service;

import br.com.powerprogramers.product.domain.dto.ProductDto;

/** Interface that represents the stock service. */
public interface StockService {
  /**
   * Update the stock of a product.
   *
   * @param id product id
   * @param amount amount to be updated
   * @return the product that was updated
   */
  ProductDto updateStock(Long id, Integer amount);
}
