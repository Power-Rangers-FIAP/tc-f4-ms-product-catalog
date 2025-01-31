package br.com.powerprogramers.product.domain.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.powerprogramers.product.domain.dto.LoadJobDto;
import br.com.powerprogramers.product.domain.exceptions.ProductLoadJobException;
import br.com.powerprogramers.product.domain.model.Load;
import br.com.powerprogramers.product.domain.service.LoadService;
import br.com.powerprogramers.product.domain.utils.LoadHelper;
import com.callibrity.logging.test.LogTracker;
import com.callibrity.logging.test.LogTrackerStub;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.yaml")
class LoadControllerTest {

  @RegisterExtension
  LogTrackerStub logTracker =
      LogTrackerStub.create()
          .recordForLevel(LogTracker.LogLevel.INFO)
          .recordForType(ProductController.class);

  private MockMvc mockMvc;
  private AutoCloseable openMocks;

  @Mock private LoadService loadService;

  @Value("${load.input-path}")
  private String directory;

  @BeforeEach
  void setUp() {
    this.openMocks = MockitoAnnotations.openMocks(this);
    LoadController loadController = new LoadController(loadService);
    ReflectionTestUtils.setField(loadController, "directory", directory);
    mockMvc =
        MockMvcBuilders.standaloneSetup(loadController)
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
  void mustLoadDataSuccessfully() throws Exception {

    doNothing().when(loadService).load(any(Load.class));

    MockMultipartFile multipartFile =
        (MockMultipartFile) LoadHelper.generateMultipartFile("test.csv");

    mockMvc.perform(multipart("/load").file(multipartFile)).andExpect(status().isOk());

    verify(loadService, times(1)).load(any(Load.class));
  }

  @Test
  void mustGenerateException_WhenLoadDataFails() throws Exception {
    doThrow(new ProductLoadJobException("Invalid fileName"))
        .when(loadService)
        .load(any(Load.class));

    MockMultipartFile multipartFile =
        (MockMultipartFile) LoadHelper.generateMultipartFile("test?.csv");

    mockMvc
        .perform(multipart("/load").file(multipartFile))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message").value("Invalid fileName"));

    verify(loadService, times(1)).load(any(Load.class));
  }

  @Test
  void mustGenerateException_WhenLoadNotCsv() throws Exception {
    MockMultipartFile multipartFile =
        (MockMultipartFile) LoadHelper.generateMultipartFile("test.txt");

    mockMvc
        .perform(multipart("/load").file(multipartFile))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message").value("File name must end with .csv"));

    verify(loadService, never()).load(any(Load.class));
  }

  @Test
  void mustScheduleJobSuccessfully() throws Exception {
    OffsetDateTime offsetDateTime = OffsetDateTime.now();
    MockMultipartFile multipartFile =
        (MockMultipartFile) LoadHelper.generateMultipartFile("test.csv");
    LoadJobDto loadJobDto = new LoadJobDto().id("job-123").scheduledDate(offsetDateTime);

    when(loadService.scheduleJob(any(LocalDateTime.class), any(Load.class))).thenReturn(loadJobDto);

    mockMvc
        .perform(
            multipart("/load/job").file(multipartFile).param("dateTime", offsetDateTime.toString()))
        .andExpect(status().isOk());

    verify(loadService, times(1)).scheduleJob(any(LocalDateTime.class), any(Load.class));
  }

  @Test
  void mustCancelJobSuccessfully() throws Exception {
    when(loadService.cancelJob(anyString())).thenReturn(true);
    mockMvc
        .perform(delete("/load/job/{id}", "15"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value("Job successfully canceled!"));

    verify(loadService, times(1)).cancelJob(anyString());
  }

  @Test
  void mustCancelJob_WhenJobIdDoNotExists() throws Exception {
    when(loadService.cancelJob(anyString())).thenReturn(false);
    mockMvc
        .perform(delete("/load/job/{id}", "15"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$").value("Job not found"));

    verify(loadService, times(1)).cancelJob(anyString());
  }
}
