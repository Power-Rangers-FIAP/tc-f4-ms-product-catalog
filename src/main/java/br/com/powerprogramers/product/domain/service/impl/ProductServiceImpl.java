package br.com.powerprogramers.product.domain.service.impl;

import br.com.powerprogramers.product.domain.dto.CreateProductDto;
import br.com.powerprogramers.product.domain.dto.PagedProductDto;
import br.com.powerprogramers.product.domain.dto.ProductDto;
import br.com.powerprogramers.product.domain.dto.UpdateProductDto;
import br.com.powerprogramers.product.domain.entity.ProductEntity;
import br.com.powerprogramers.product.domain.exceptions.ProductNotFoundException;
import br.com.powerprogramers.product.domain.mappers.ProductMapper;
import br.com.powerprogramers.product.domain.model.Product;
import br.com.powerprogramers.product.domain.repository.ProductRepository;
import br.com.powerprogramers.product.domain.service.ProductService;
import br.com.powerprogramers.product.domain.service.usecase.create.CreateProductUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/** Service class for the product. */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private static final ProductMapper productMapper = ProductMapper.INSTANCE;
  private final ProductRepository productRepository;
  private final CreateProductUseCase createProductUseCase;

  @Override
  public ProductDto findById(Long id) {
    ProductEntity productEntity =
        this.productRepository
            .findById(id)
            .orElseThrow(() -> new ProductNotFoundException("/products"));
    return productMapper.toDto(productEntity);
  }

  @Override
  public PagedProductDto findAll(
      Integer page, Integer perPage, String name, String description, Boolean active) {
    Pageable pageable = PageRequest.of(page, perPage);
    Page<ProductEntity> pageOfEntity =
        this.productRepository.findAllProducts(pageable, name, description, active);
    return new PagedProductDto()
        .page(pageOfEntity.getPageable().getPageNumber())
        .perPage(pageOfEntity.getPageable().getPageSize())
        .total(pageOfEntity.getTotalElements())
        .items(pageOfEntity.get().map(productMapper::toDto).toList());
  }

  @Override
  public ProductDto save(CreateProductDto createProductDto) {
    Product product = this.createProductUseCase.execute(productMapper.toModel(createProductDto));
    product.activate();
    return this.persist(product);
  }

  @Override
  public ProductDto update(Long id, UpdateProductDto updateProductDto) {
    ProductDto productDto = this.findById(id);
    Product product = productMapper.toModel(productDto);
    product.update(updateProductDto);
    return this.persist(product);
  }

  @Override
  public ProductDto activate(Long id) {
    ProductDto productDto = this.findById(id);
    Product product = productMapper.toModel(productDto);
    product.activate();
    return this.persist(product);
  }

  @Override
  public ProductDto deactivate(Long id) {
    ProductDto productDto = this.findById(id);
    Product product = productMapper.toModel(productDto);
    product.deactivate();
    return this.persist(product);
  }

  private ProductDto persist(Product product) {
    return productMapper.toDto(this.productRepository.save(productMapper.toEntity(product)));
  }
}
