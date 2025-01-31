package br.com.powerprogramers.product.domain.controller;

import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.powerprogramers.product.domain.dto.ProductDto;
import br.com.powerprogramers.product.domain.service.StockService;
import br.com.powerprogramers.product.domain.utils.ProductHelper;
import com.callibrity.logging.test.LogTracker;
import com.callibrity.logging.test.LogTrackerStub;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class StockControllerTest {

  @RegisterExtension
  LogTrackerStub logTracker =
      LogTrackerStub.create()
          .recordForLevel(LogTracker.LogLevel.INFO)
          .recordForType(ProductController.class);

  private MockMvc mockMvc;
  private AutoCloseable openMocks;

  @Mock private StockService stockService;

  @BeforeEach
  void setUp() {
    this.openMocks = MockitoAnnotations.openMocks(this);
    StockController stockController = new StockController(stockService);
    mockMvc =
        MockMvcBuilders.standaloneSetup(stockController)
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

  @Test
  void mustUpdateStockSuccessfully() throws Exception {
    ProductDto productDto = ProductHelper.generateProductDto(true);

    when(stockService.updateStock(anyLong(), anyInt())).thenReturn(productDto);

    mockMvc
        .perform(patch("/stock/{id}", ProductHelper.ID).param("amount", "50"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.amount").value(productDto.getAmount()));

    verify(stockService, times(1)).updateStock(anyLong(), anyInt());
  }

  @Test
  void mustGenerateException_WhenUpdateStockFails() throws Exception {

    doThrow(new RuntimeException()).when(stockService).updateStock(anyLong(), anyInt());

    mockMvc
        .perform(patch("/stock/{id}", ProductHelper.ID))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("The mandatory parameter 'amount' it is absent"));

    verify(stockService, never()).updateStock(anyLong(), anyInt());
  }
}
