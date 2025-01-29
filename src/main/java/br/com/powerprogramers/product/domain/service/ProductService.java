package br.com.powerprogramers.product.domain.service;

import br.com.powerprogramers.product.domain.dto.CreateProductDto;
import br.com.powerprogramers.product.domain.dto.PagedProductDto;
import br.com.powerprogramers.product.domain.dto.ProductDto;
import br.com.powerprogramers.product.domain.dto.UpdateProductDto;

/** Interface containing the contract for the product's service methods. */
public interface ProductService {

  /**
   * Find a product by its id.
   *
   * @param id product id for search
   * @return the product corresponding to the given id
   */
  ProductDto findById(Long id);

  /**
   * Find all products by some filters.
   *
   * @param page page to be displayed
   * @param perPage number of items per page
   * @param name product name
   * @param description product description
   * @param active Product status
   * @return paginated product object
   */
  PagedProductDto findAll(
      Integer page, Integer perPage, String name, String description, Boolean active);

  /**
   * Saves the product to the database.
   *
   * @param createProductDto product to be kept in database
   * @return the product that was saved
   */
  ProductDto save(CreateProductDto createProductDto);

  /**
   * Updates the existing product in the database.
   *
   * @param id product id
   * @param updateProductDto product data to be updated
   * @return the product that was updated
   */
  ProductDto update(Long id, UpdateProductDto updateProductDto);

  /**
   * Activates an existing product at the base that is deactivated.
   *
   * @param id product id for activate
   * @return the product that was updated
   */
  ProductDto activate(Long id);

  /**
   * Deactivates an existing product at the base that is activated.
   *
   * @param id product id for deactivate
   * @return the product that was updated
   */
  ProductDto deactivate(Long id);
}
