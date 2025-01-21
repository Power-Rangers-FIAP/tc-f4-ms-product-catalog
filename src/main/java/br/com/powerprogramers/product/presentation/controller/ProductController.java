package br.com.powerprogramers.product.presentation.controller;

import br.com.powerprogramers.product.api.ProductsApi;
import br.com.powerprogramers.product.model.CreateProductDTO;
import br.com.powerprogramers.product.model.PagedProductDTO;
import br.com.powerprogramers.product.model.ProductDTO;
import br.com.powerprogramers.product.model.UpdateProductDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/** Class that controls product application endpoints. */
public class ProductController implements ProductsApi {

  @Override
  public ResponseEntity<ProductDTO> activateProductById(String id) {
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new ProductDTO());
  }

  @Override
  public ResponseEntity<ProductDTO> deactivateProductById(String id) {
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new ProductDTO());
  }

  @Override
  public ResponseEntity<PagedProductDTO> findAllProducts(
      Integer page, Integer perPage, String name, String description, Boolean active) {
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new PagedProductDTO());
  }

  @Override
  public ResponseEntity<ProductDTO> findProductById(String id) {
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new ProductDTO());
  }

  @Override
  public ResponseEntity<ProductDTO> registerProduct(CreateProductDTO body) {
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new ProductDTO());
  }

  @Override
  public ResponseEntity<ProductDTO> updateProduct(String id, UpdateProductDTO body) {
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new ProductDTO());
  }
}
