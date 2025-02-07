package br.com.powerprogramers.product.domain.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import br.com.powerprogramers.product.domain.dto.CreateProductDto;
import br.com.powerprogramers.product.domain.dto.PagedProductDto;
import br.com.powerprogramers.product.domain.dto.ProductDto;
import br.com.powerprogramers.product.domain.dto.UpdateProductDto;
import br.com.powerprogramers.product.domain.entity.ProductEntity;
import br.com.powerprogramers.product.domain.exceptions.ProductNotFoundException;
import br.com.powerprogramers.product.domain.mappers.ProductMapper;
import br.com.powerprogramers.product.domain.model.Product;
import br.com.powerprogramers.product.domain.repository.ProductRepository;
import br.com.powerprogramers.product.domain.service.usecase.create.CreateProductUseCase;
import br.com.powerprogramers.product.domain.utils.ProductHelper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

class ProductServiceImplTest {

  private AutoCloseable openMocks;

  @Mock private ProductRepository productRepository;
  @Mock private CreateProductUseCase createProductUseCase;

  @InjectMocks private ProductServiceImpl productServiceImpl;

  @BeforeEach
  void setUp() {
    openMocks = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  void tearDown() throws Exception {
    openMocks.close();
  }

  @Test
  void mustFindProductByIdSuccessfully() {
    ProductEntity productEntity = ProductHelper.generateProductEntity(true);

    when(productRepository.findById(anyLong())).thenReturn(Optional.of(productEntity));

    ProductDto result = productServiceImpl.findById(ProductHelper.ID);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(ProductHelper.ID);
    assertThat(result.getName()).isEqualTo(ProductHelper.NAME);
    assertThat(result.getDescription()).isEqualTo(ProductHelper.DESCRIPTION);
  }

  @Test
  void mustGenerateException_WhenFindProductById_WithInvalidProduct() {
    when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> productServiceImpl.findById(ProductHelper.ID))
        .isInstanceOf(ProductNotFoundException.class)
        .hasMessage("product not found");
  }

  @Test
  void mustFindAllProductsSuccessfully() {
    PageRequest pageRequest = PageRequest.of(0, 10);
    ProductEntity productEntity = ProductHelper.generateProductEntity(true);
    Page<ProductEntity> page = new PageImpl<>(List.of(productEntity), pageRequest, 1);

    when(productRepository.findAllProducts(pageRequest, null, null, null)).thenReturn(page);

    PagedProductDto result = productServiceImpl.findAll(0, 10, null, null, null);

    assertThat(result).isNotNull();
    assertThat(result.getItems()).hasSize(1);
    assertThat(result.getItems().get(0).getName()).isEqualTo(ProductHelper.NAME);
  }

  @Test
  void mustSaveProductSuccessfully() {
    CreateProductDto createProductDto = ProductHelper.generateCreateProductDto();
    Product product = ProductHelper.generateProduct();

    when(createProductUseCase.execute(any(Product.class))).thenReturn(product);
    when(productRepository.save(any(ProductEntity.class)))
        .thenReturn(ProductMapper.INSTANCE.toEntity(product));

    ProductDto result = productServiceImpl.save(createProductDto);

    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo(ProductHelper.NAME);
  }

  @Test
  void mustUpdateProductSuccessfully() {
    UpdateProductDto updateProductDto = ProductHelper.generateUpdateProductDto();

    Product product = ProductHelper.generateProduct();

    when(productRepository.findById(anyLong()))
        .thenReturn(Optional.of(ProductMapper.INSTANCE.toEntity(product)));
    when(productRepository.save(any())).thenAnswer(p -> p.getArgument(0));

    ProductDto result = productServiceImpl.update(ProductHelper.ID, updateProductDto);

    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo(updateProductDto.getName());
    assertThat(result.getAmount()).isEqualTo(product.getAmount());
    assertThat(result.getPrice()).isEqualTo(updateProductDto.getPrice());
  }

  @Test
  void mustActivateProductSuccessfully() {
    Product product = ProductHelper.generateProduct();

    when(productRepository.findById(anyLong()))
        .thenReturn(Optional.of(ProductMapper.INSTANCE.toEntity(product)));
    when(productRepository.save(any())).thenAnswer(p -> p.getArgument(0));

    ProductDto result = productServiceImpl.activate(ProductHelper.ID);

    assertThat(result).isNotNull();
    assertThat(result.isActive()).isTrue();
  }

  @Test
  void mustDeactivateProductSuccessfully() {
    Product product = ProductHelper.generateProduct();

    when(productRepository.findById(anyLong()))
        .thenReturn(Optional.of(ProductMapper.INSTANCE.toEntity(product)));
    when(productRepository.save(any())).thenAnswer(p -> p.getArgument(0));

    ProductDto result = productServiceImpl.deactivate(ProductHelper.ID);

    assertThat(result).isNotNull();
    assertThat(result.isActive()).isFalse();
  }
}
