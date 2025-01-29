package br.com.powerprogramers.product.domain.service.usecase.update;

import br.com.powerprogramers.product.domain.dto.ProductDto;
import br.com.powerprogramers.product.domain.service.ProductService;
import br.com.powerprogramers.product.domain.service.usecase.UseCase;
import lombok.RequiredArgsConstructor;

/** Use case that represents the update stock use case. */
@RequiredArgsConstructor
public class UpdateStockUseCase implements UseCase<Long, ProductDto> {
  private final ProductService productService;

  @Override
  public ProductDto execute(Long input) {
    return this.productService.findById(input);
  }
}
