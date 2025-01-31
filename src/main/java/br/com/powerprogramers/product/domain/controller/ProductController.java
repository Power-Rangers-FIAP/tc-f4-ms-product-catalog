package br.com.powerprogramers.product.domain.controller;

import br.com.powerprogramers.product.api.ProductsApi;
import br.com.powerprogramers.product.domain.dto.CreateProductDto;
import br.com.powerprogramers.product.domain.dto.PagedProductDto;
import br.com.powerprogramers.product.domain.dto.ProductDto;
import br.com.powerprogramers.product.domain.dto.UpdateProductDto;
import br.com.powerprogramers.product.domain.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Class that controls product application endpoints. */
@RestController
@RequestMapping
@RequiredArgsConstructor
public class ProductController implements ProductsApi {

  private final ProductService productService;

  /**
   * Activates a product by its ID.
   *
   * @param id the ID of the product to activate
   * @return the activated product
   */
  @Override
  public ResponseEntity<ProductDto> activateProductById(Long id) {
    return ResponseEntity.ok(this.productService.activate(id));
  }

  /**
   * Deactivates a product by its ID.
   *
   * @param id the ID of the product to deactivate
   * @return the deactivated product
   */
  @Override
  public ResponseEntity<ProductDto> deactivateProductById(Long id) {
    return ResponseEntity.ok(this.productService.deactivate(id));
  }

  /**
   * Finds all products with optional filters.
   *
   * @param page the page number
   * @param perPage the number of items per page
   * @param name the name filter
   * @param description the description filter
   * @param active the active status filter
   * @return a paged list of products
   */
  @Override
  public ResponseEntity<PagedProductDto> findAllProducts(
      Integer page, Integer perPage, String name, String description, Boolean active) {
    return ResponseEntity.ok(this.productService.findAll(page, perPage, name, description, active));
  }

  /**
   * Finds a product by its ID.
   *
   * @param id the ID of the product to find
   * @return the found product
   */
  @Override
  public ResponseEntity<ProductDto> findProductById(Long id) {
    return ResponseEntity.ok(this.productService.findById(id));
  }

  /**
   * Registers a new product.
   *
   * @param createProductDto the product data to create
   * @return the created product
   */
  @Override
  public ResponseEntity<ProductDto> registerProduct(CreateProductDto createProductDto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(this.productService.save(createProductDto));
  }

  /**
   * Updates an existing product.
   *
   * @param id the ID of the product to update
   * @param body the updated product data
   * @return the updated product
   */
  @Override
  public ResponseEntity<ProductDto> updateProduct(Long id, UpdateProductDto body) {
    return ResponseEntity.ok(this.productService.update(id, body));
  }
}
