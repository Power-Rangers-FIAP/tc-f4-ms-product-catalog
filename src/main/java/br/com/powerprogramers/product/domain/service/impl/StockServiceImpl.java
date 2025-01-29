package br.com.powerprogramers.product.domain.service.impl;

import br.com.powerprogramers.product.domain.dto.ProductDto;
import br.com.powerprogramers.product.domain.mappers.ProductMapper;
import br.com.powerprogramers.product.domain.model.Product;
import br.com.powerprogramers.product.domain.repository.ProductRepository;
import br.com.powerprogramers.product.domain.service.StockService;
import br.com.powerprogramers.product.domain.service.usecase.update.UpdateStockUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Class that represents the stock service implementation. */
@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

  private static final ProductMapper productMapper = ProductMapper.INSTANCE;
  private final ProductRepository productRepository;
  private final UpdateStockUseCase updateStockUseCase;

  @Override
  public ProductDto updateStock(Long id, Integer amount) {
    ProductDto productDto = this.updateStockUseCase.execute(id);
    Product product = productMapper.toModel(productDto);
    product.updateAmount(amount);
    return productMapper.toDto(this.productRepository.save(productMapper.toEntity(product)));
  }
}
