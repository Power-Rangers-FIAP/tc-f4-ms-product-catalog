package br.com.powerprogramers.product.domain.utils;

import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.powerprogramers.product.domain.model.Load;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class LoadHelper {

  public static final String ABSOLUTE_PATH =
      new FileSystemResource("").getFile().getAbsolutePath() + "/src/test/resources/load";

  public static Load generateFileSuccessfully() {
    var multipartFile = generateMultipartFile("test.csv");
    return new Load(multipartFile, ABSOLUTE_PATH);
  }

  public static Load generateFileSuccessfullyFromRoot() throws IOException {
    File movedFile = generateFileFromRoot();
    return new Load(generateMultipartFileValid(movedFile.getName(), movedFile), ABSOLUTE_PATH);
  }

  public static Load generateFileWithErrorFromRoot() throws IOException {
    File movedFile = generateFileFromRoot();
    return new Load(generateMultipartFileValid("products?.csv", movedFile), ABSOLUTE_PATH);
  }

  public static Load generateFileWithError() {
    var multipartFile = generateMultipartFile("test?.csv");
    return new Load(multipartFile, ABSOLUTE_PATH);
  }

  public static MultipartFile generateMultipartFile(String fileName) {
    return new MockMultipartFile("file", fileName, "text/csv", new byte[0]);
  }

  public static File generateFile() throws IOException {
    return generateFileFromRoot();
  }

  private static File generateFileFromRoot() throws IOException {
    Path products =
        Paths.get(new FileSystemResource("").getFile().getAbsolutePath() + "/products.csv");
    return Files.write(Paths.get(ABSOLUTE_PATH + "/products.csv"), Files.readAllBytes(products))
        .toFile();
  }

  private static MultipartFile generateMultipartFileValid(String fileName, File file)
      throws IOException {
    return new MockMultipartFile("file", fileName, "text/csv", new FileInputStream(file));
  }

  public static void deleteTestFiles(String path) {
    Path filePath = Paths.get(Paths.get(path) + "/processed");
    try (Stream<Path> sPaths = Files.list(filePath)) {
      sPaths.forEach(
          f -> {
            assertTrue(f.toFile().exists());
            try {
              Files.deleteIfExists(f.toFile().toPath());
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
