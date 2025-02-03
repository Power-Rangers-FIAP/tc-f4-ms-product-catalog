package br.com.powerprogramers.product.domain.batch.job;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import br.com.powerprogramers.product.domain.exceptions.ProductLoadMoveFileException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.test.util.ReflectionTestUtils;

class LoadConfigurationTest {

  private AutoCloseable openMocks;
  @Mock private StepContribution stepContribution;
  @Mock private ChunkContext chunkContext;

  private LoadConfiguration loadConfiguration;
  private static final String DIRECTORY = "src/test/resources/load";

  @BeforeAll
  static void beforeAll() throws IOException {
    Path path = Paths.get(DIRECTORY);
    if (!path.toFile().exists()) {
      Files.createDirectory(path);
    }
  }

  @BeforeEach
  void setUp() {
    openMocks = MockitoAnnotations.openMocks(this);
    loadConfiguration = new LoadConfiguration(null, null);
    ReflectionTestUtils.setField(loadConfiguration, "directory", DIRECTORY);
  }

  @AfterEach
  void tearDown() throws Exception {
    openMocks.close();
  }

  @Test
  void testMoverArquivosTasklet_DestinyFolderDoesNotExist() throws Exception {
    Path destinyPath = Paths.get(DIRECTORY + "/processed");
    Files.deleteIfExists(destinyPath);

    RepeatStatus status =
        loadConfiguration.moverArquivosTasklet().execute(stepContribution, chunkContext);

    assertThat(Files.exists(destinyPath)).isTrue();
    assertThat(status).isEqualTo(RepeatStatus.FINISHED);
  }

  @Test
  void testMoverArquivosTasklet_DestinyFolderExists() throws Exception {
    Path destinyPath = Paths.get(DIRECTORY + "/processed");
    Files.createDirectories(destinyPath);

    RepeatStatus status =
        loadConfiguration.moverArquivosTasklet().execute(stepContribution, chunkContext);

    assertThat(Files.exists(destinyPath)).isTrue();
    assertThat(status).isEqualTo(RepeatStatus.FINISHED);
  }

  @Test
  void testMoverArquivosTasklet_NoCsvFiles() throws Exception {
    File originFolder = new File(DIRECTORY);
    for (File file : Objects.requireNonNull(originFolder.listFiles())) {
      if (file.getName().endsWith(".csv")) {
        assertTrue(file.delete());
      }
    }

    RepeatStatus status =
        loadConfiguration.moverArquivosTasklet().execute(stepContribution, chunkContext);

    assertThat(status).isEqualTo(RepeatStatus.FINISHED);
  }

  @Test
  void testMoverArquivosTasklet_WithCsvFiles() throws Exception {
    File originFolder = new File(DIRECTORY);
    File csvFile = new File(originFolder, "test.csv");

    if (csvFile.createNewFile()) {
      RepeatStatus status =
          loadConfiguration.moverArquivosTasklet().execute(stepContribution, chunkContext);
      Optional<File> oFile =
          Arrays.stream(
                  Objects.requireNonNull(Paths.get(DIRECTORY + "/processed").toFile().listFiles()))
              .findFirst();
      File movedFile = oFile.orElse(null);

      assert movedFile != null;
      assertTrue(movedFile.exists());
      assertThat(status).isEqualTo(RepeatStatus.FINISHED);
      assertTrue(movedFile.delete());
    }
  }

  @Test
  void testMoverArquivosTasklet_FileCannotBeMoved() throws Exception {
    File originFolder = new File(DIRECTORY);
    File csvFile = new File(originFolder, "test.csv");

    if (csvFile.createNewFile()) {
      LoadConfiguration spyLoadConfiguration = spy(loadConfiguration);
      doReturn(false).when(spyLoadConfiguration).renameFile(any(File.class), any(File.class));

      assertThatThrownBy(
              () ->
                  spyLoadConfiguration
                      .moverArquivosTasklet()
                      .execute(stepContribution, chunkContext))
          .isInstanceOf(ProductLoadMoveFileException.class);

      assertTrue(csvFile.delete());
    }
  }
}
