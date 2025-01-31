package br.com.powerprogramers.product.domain.controller;

import static br.com.powerprogramers.product.domain.utils.JsonUtil.toJson;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.powerprogramers.product.domain.dto.CreateProductDto;
import br.com.powerprogramers.product.domain.dto.PagedProductDto;
import br.com.powerprogramers.product.domain.dto.ProductDto;
import br.com.powerprogramers.product.domain.dto.UpdateProductDto;
import br.com.powerprogramers.product.domain.exceptions.CreateProductUseCaseException;
import br.com.powerprogramers.product.domain.service.ProductService;
import br.com.powerprogramers.product.domain.utils.ProductHelper;
import com.callibrity.logging.test.LogTracker;
import com.callibrity.logging.test.LogTrackerStub;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

class ProductControllerTest {

  @RegisterExtension
  LogTrackerStub logTracker =
      LogTrackerStub.create()
          .recordForLevel(LogTracker.LogLevel.INFO)
          .recordForType(ProductController.class);

  private MockMvc mockMvc;
  private AutoCloseable openMocks;

  @Mock private ProductService productService;

  @BeforeEach
  void setUp() {
    this.openMocks = MockitoAnnotations.openMocks(this);
    ProductController productController = new ProductController(productService);
    mockMvc =
        MockMvcBuilders.standaloneSetup(productController)
            .setControllerAdvice(new ProductExceptionHandler())
            .addFilter(
                (req, resp, chain) -> {
                  resp.setCharacterEncoding("UTF-8");
                  chain.doFilter(req, resp);
                },
                "/*")
            .build();
  }

  @AfterEach
  void tearDown() throws Exception {
    openMocks.close();
  }

  @Nested
  class Create {
    @Test
    void mustRegisterProductSuccessfully() throws Exception {
      CreateProductDto createProductDto = ProductHelper.generateCreateProductDto();
      ProductDto productDto = ProductHelper.generateProductDto(true);

      when(productService.save(any(CreateProductDto.class))).thenReturn(productDto);

      mockMvc
          .perform(
              post("/products")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(toJson(createProductDto)))
          .andExpect(status().isCreated());

      verify(productService, times(1)).save(any(CreateProductDto.class));
    }

    @Test
    void mustGenerateException_WhenRegisterProduct_WithNameIsEmpty() throws Exception {
      var erroMessage = "Product name cannot be empty";
      CreateProductDto createProductDto = ProductHelper.generateCreateProductDto();
      createProductDto.name("");

      doThrow(new CreateProductUseCaseException(erroMessage))
          .when(productService)
          .save(any(CreateProductDto.class));

      mockMvc
          .perform(
              post("/products")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(toJson(createProductDto)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
          .andExpect(jsonPath("$.message").value(erroMessage))
          .andExpect(jsonPath("$.path").value("/products"));

      verify(productService, times(1)).save(any(CreateProductDto.class));
    }

    @Test
    void mustGenerateException_WhenRegisterProduct_WithDescriptionIsEmpty() throws Exception {
      var erroMessage = "Product description cannot be empty";
      CreateProductDto createProductDto = ProductHelper.generateCreateProductDto();
      createProductDto.description("");

      doThrow(new CreateProductUseCaseException(erroMessage))
          .when(productService)
          .save(any(CreateProductDto.class));

      mockMvc
          .perform(
              post("/products")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(toJson(createProductDto)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
          .andExpect(jsonPath("$.message").value(erroMessage))
          .andExpect(jsonPath("$.path").value("/products"));

      verify(productService, times(1)).save(any(CreateProductDto.class));
    }

    @Test
    void mustGenerateException_WhenRegisterProduct_WithAmountIsZeroOrNegative() throws Exception {
      var erroMessage = "Product amount cannot be zero or negative";
      CreateProductDto createProductDto = ProductHelper.generateCreateProductDto();
      createProductDto.amount(-1);

      doThrow(new CreateProductUseCaseException(erroMessage))
          .when(productService)
          .save(any(CreateProductDto.class));

      mockMvc
          .perform(
              post("/products")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(toJson(createProductDto)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
          .andExpect(jsonPath("$.message").value(erroMessage))
          .andExpect(jsonPath("$.path").value("/products"));

      verify(productService, times(1)).save(any(CreateProductDto.class));
    }

    @Test
    void mustGenerateException_WhenRegisterProduct_WithPriceIsZeroOrNegative() throws Exception {
      var erroMessage = "Product price cannot be zero or negative";
      CreateProductDto createProductDto = ProductHelper.generateCreateProductDto();
      createProductDto.price(BigDecimal.ZERO);

      doThrow(new CreateProductUseCaseException(erroMessage))
          .when(productService)
          .save(any(CreateProductDto.class));

      mockMvc
          .perform(
              post("/products")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(toJson(createProductDto)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
          .andExpect(jsonPath("$.message").value(erroMessage))
          .andExpect(jsonPath("$.path").value("/products"));

      verify(productService, times(1)).save(any(CreateProductDto.class));
    }
  }

  @Nested
  class Update {
    @Test
    void mustUpdateProductSuccessfully() throws Exception {
      UpdateProductDto updateProductDto = ProductHelper.generateUpdateProductDto();
      ProductDto productDto = ProductHelper.generateProductDtoUpdated();

      when(productService.update(any(Long.class), any(UpdateProductDto.class)))
          .thenReturn(productDto);

      mockMvc
          .perform(
              patch("/products/{id}", ProductHelper.ID)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(toJson(updateProductDto)))
          .andExpect(status().isOk());

      verify(productService, times(1)).update(any(Long.class), any(UpdateProductDto.class));
    }

    @Test
    void mustGenerateException_WhenUpdateProduct_WithHttpMessageNotReadable() throws Exception {
      mockMvc
          .perform(
              patch("/products/{id}", ProductHelper.ID).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
          .andExpect(jsonPath("$.message").value("Failed to read request"))
          .andExpect(jsonPath("$.path").value("/products/" + ProductHelper.ID));

      verify(productService, never()).update(any(Long.class), any(UpdateProductDto.class));
    }

    @Test
    void mustGenerateException_WhenUpdateProduct_WithMethodArgumentNotValid() throws Exception {
      mockMvc
          .perform(
              patch("/products/{id}", ProductHelper.ID)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("{}"))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
          .andExpect(
              jsonPath("$.message")
                  .value(
                      "{price=must not be null, name=must not be null, description=must not be null}"))
          .andExpect(jsonPath("$.path").value("/products/" + ProductHelper.ID));

      verify(productService, never()).update(any(Long.class), any(UpdateProductDto.class));
    }

    @Test
    void mustGenerateException_WhenUpdateProduct_WithHttpMediaTypeNotSupported() throws Exception {
      mockMvc
          .perform(patch("/products/{id}", ProductHelper.ID))
          .andExpect(status().isUnsupportedMediaType())
          .andExpect(jsonPath("$.status").value(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()))
          .andExpect(jsonPath("$.message").value("Content-Type 'null' is not supported."))
          .andExpect(jsonPath("$.path").value("/products/" + ProductHelper.ID));

      verify(productService, never()).activate(any(Long.class));
    }
  }

  @Nested
  class Find {

    @Test
    void mustFindProductByIdSuccessfully() throws Exception {
      ProductDto productDto = ProductHelper.generateProductDto(true);

      when(productService.findById(any(Long.class))).thenReturn(productDto);

      mockMvc
          .perform(get("/products/{id}", ProductHelper.ID).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk());

      verify(productService, times(1)).findById(any(Long.class));
    }

    @Test
    void mustGenerateException_WhenFindProductById_WithIllegalArgument() throws Exception {
      mockMvc
          .perform(get("/products/{id}", "a").contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
          .andExpect(jsonPath("$.message").value("Error converting id with value of type a"))
          .andExpect(jsonPath("$.path").value("/products/a"));

      verify(productService, never()).findById(any(Long.class));
    }

    @Test
    void mustFindAllProductsSuccessfully() throws Exception {
      ProductDto productDto = ProductHelper.generateProductDto(true);
      PagedProductDto pagination =
          new PagedProductDto().page(0).perPage(10).total(1L).items(List.of(productDto));

      when(productService.findAll(anyInt(), anyInt(), anyString(), anyString(), anyBoolean()))
          .thenReturn(pagination);

      MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
      params.add("page", "0");
      params.add("perPage", "10");
      params.add("name", "");
      params.add("description", "");
      params.add("active", "true");

      mockMvc
          .perform(get("/products").contentType(MediaType.APPLICATION_JSON).params(params))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.page").value(pagination.getPage()))
          .andExpect(jsonPath("$.perPage").value(pagination.getPerPage()))
          .andExpect(jsonPath("$.total").value(pagination.getTotal()))
          .andExpect(jsonPath("$.items[0].id").value(productDto.getId()))
          .andExpect(jsonPath("$.items[0].name").value(productDto.getName()))
          .andExpect(jsonPath("$.items[0].description").value(productDto.getDescription()))
          .andExpect(jsonPath("$.items[0].amount").value(productDto.getAmount()))
          .andExpect(jsonPath("$.items[0].price").value(productDto.getPrice()))
          .andExpect(jsonPath("$.items[0].active").value(productDto.isActive()));

      verify(productService, times(1))
          .findAll(anyInt(), anyInt(), anyString(), anyString(), anyBoolean());
    }

    @Test
    void mustGenerateException_WhenFindAllProducts_WithArgumentTypeMismatch() throws Exception {
      ProductDto productDto = ProductHelper.generateProductDto(true);
      PagedProductDto pagination =
          new PagedProductDto().page(0).perPage(10).total(1L).items(List.of(productDto));

      when(productService.findAll(anyInt(), anyInt(), anyString(), anyString(), anyBoolean()))
          .thenReturn(pagination);

      MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
      params.add("page", "a");

      mockMvc
          .perform(get("/products").contentType(MediaType.APPLICATION_JSON).params(params))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
          .andExpect(jsonPath("$.message").value("Error converting page with value of type a"))
          .andExpect(jsonPath("$.path").value("/products"));

      verify(productService, never())
          .findAll(anyInt(), anyInt(), anyString(), anyString(), anyBoolean());
    }
  }

  @Nested
  class Activate {
    @Test
    void mustActivateProductSuccessfully() throws Exception {
      ProductDto productDto = ProductHelper.generateProductDto(true);

      when(productService.activate(any(Long.class))).thenReturn(productDto);

      mockMvc
          .perform(
              get("/products/activate/{id}", ProductHelper.ID)
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.active").value(true));

      verify(productService, times(1)).activate(any(Long.class));
    }
  }

  @Test
  void mustDeactivateProductSuccessfully() throws Exception {
    ProductDto productDto = ProductHelper.generateProductDto(false);

    when(productService.deactivate(any(Long.class))).thenReturn(productDto);

    mockMvc
        .perform(
            get("/products/deactivate/{id}", ProductHelper.ID)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.active").value(false));

    verify(productService, times(1)).deactivate(any(Long.class));
  }
}
