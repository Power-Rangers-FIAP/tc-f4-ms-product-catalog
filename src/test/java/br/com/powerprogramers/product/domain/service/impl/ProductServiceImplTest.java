package br.com.powerprogramers.product.domain.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import br.com.powerprogramers.product.domain.utils.ProductHelper;
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
    Long productId = 1L;
    ProductEntity productEntity =
        ProductEntity.builder()
            .id(productId)
            .name("Orange")
            .description("Argentine sweet orange")
            .amount(100)
            .price(BigDecimal.valueOf(12.5))
            .active(true)
            .build();

    when(productRepository.findById(productId)).thenReturn(Optional.of(productEntity));

    ProductDto result = productServiceImpl.findById(productId);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(productId);
    assertThat(result.getName()).isEqualTo("Orange");
    assertThat(result.getDescription()).isEqualTo("Argentine sweet orange");
  }

  @Test
  void mustGenerateException_WhenFindProductById_WithInvalidProduct() {
    Long productId = 1L;

    when(productRepository.findById(productId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> productServiceImpl.findById(productId))
        .isInstanceOf(ProductNotFoundException.class)
        .hasMessage("product not found");
  }

  @Test
  void mustFindAllProductsSuccessfully() {
    PageRequest pageRequest = PageRequest.of(0, 10);
    ProductEntity productEntity =
        ProductEntity.builder()
            .id(1L)
            .name("Orange")
            .description("Argentine sweet orange")
            .amount(100)
            .price(BigDecimal.valueOf(12.5))
            .active(true)
            .build();
    Page<ProductEntity> page = new PageImpl<>(List.of(productEntity), pageRequest, 1);

    when(productRepository.findAllProducts(pageRequest, null, null, null)).thenReturn(page);

    PagedProductDto result = productServiceImpl.findAll(0, 10, null, null, null);

    assertThat(result).isNotNull();
    assertThat(result.getItems()).hasSize(1);
    assertThat(result.getItems().get(0).getName()).isEqualTo("Orange");
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
    assertThat(result.getName()).isEqualTo("Orange");
  }

  @Test
  void mustUpdateProductSuccessfully() {
    Long productId = 1L;
    UpdateProductDto updateProductDto = new UpdateProductDto();
    updateProductDto.setName("Orange 2");
    updateProductDto.setDescription("Argentine sweet orange");
    updateProductDto.setPrice(BigDecimal.valueOf(25.0));

    Product product = ProductHelper.generateProduct();

    when(productRepository.findById(productId))
        .thenReturn(Optional.of(ProductMapper.INSTANCE.toEntity(product)));
    when(productRepository.save(any())).thenAnswer(p -> p.getArgument(0));

    ProductDto result = productServiceImpl.update(productId, updateProductDto);

    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("Orange 2");
    assertThat(result.getAmount()).isEqualTo(150);
    assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(25.0));
  }

  @Test
  void mustActivateProductSuccessfully() {
    Long productId = 1L;
    Product product = ProductHelper.generateProduct();

    when(productRepository.findById(productId))
            .thenReturn(Optional.of(ProductMapper.INSTANCE.toEntity(product)));
    when(productRepository.save(any())).thenAnswer(p -> p.getArgument(0));

    ProductDto result = productServiceImpl.activate(productId);

    assertThat(result).isNotNull();
    assertThat(result.isActive()).isTrue();
  }

  @Test
  void mustDeactivateProductSuccessfully() {
    Long productId = 1L;
    Product product = ProductHelper.generateProduct();

    when(productRepository.findById(productId))
            .thenReturn(Optional.of(ProductMapper.INSTANCE.toEntity(product)));
    when(productRepository.save(any())).thenAnswer(p -> p.getArgument(0));

    ProductDto result = productServiceImpl.deactivate(productId);

    assertThat(result).isNotNull();
    assertThat(result.isActive()).isFalse();
  }
}
