package br.com.powerprogramers.product.domain.controller;

import br.com.powerprogramers.product.api.ProductsApi;
import br.com.powerprogramers.product.domain.dto.CreateProductDto;
import br.com.powerprogramers.product.domain.dto.PagedProductDto;
import br.com.powerprogramers.product.domain.dto.ProductDto;
import br.com.powerprogramers.product.domain.dto.UpdateProductDto;
import br.com.powerprogramers.product.domain.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Class that controls product application endpoints. */
@RestController
@RequestMapping
@RequiredArgsConstructor
public class ProductController implements ProductsApi {

  private final ProductService productService;

  @Override
  public ResponseEntity<ProductDto> activateProductById(Long id) {
    return ResponseEntity.ok(this.productService.activate(id));
  }

  @Override
  public ResponseEntity<ProductDto> deactivateProductById(Long id) {
    return ResponseEntity.ok(this.productService.deactivate(id));
  }

  @Override
  public ResponseEntity<PagedProductDto> findAllProducts(
      Integer page, Integer perPage, String name, String description, Boolean active) {
    return ResponseEntity.ok(this.productService.findAll(page, perPage, name, description, active));
  }

  @Override
  public ResponseEntity<ProductDto> findProductById(Long id) {
    return ResponseEntity.ok(this.productService.findById(id));
  }

  @Override
  public ResponseEntity<ProductDto> registerProduct(CreateProductDto createProductDto) {
    return ResponseEntity.ok(this.productService.save(createProductDto));
  }

  @Override
  public ResponseEntity<ProductDto> updateProduct(Long id, UpdateProductDto body) {
    return ResponseEntity.ok(this.productService.update(id, body));
  }
}
