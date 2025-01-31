package br.com.powerprogramers.product.domain.utils;

import br.com.powerprogramers.product.domain.model.Load;
import java.io.IOException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class LoadHelper {

  private static final String ABSOLUTE_PATH =
      new FileSystemResource("").getFile().getAbsolutePath() + "/src/test/resources/load";

  public static Load generateFileSuccessfully() throws IOException {
    var multipartFile = generateMultipartFile("test.csv");
    return new Load(multipartFile, ABSOLUTE_PATH);
  }

  public static Load generateFileWithError() throws IOException {
    var multipartFile = generateMultipartFile("test?.csv");
    return new Load(multipartFile, ABSOLUTE_PATH);
  }

  public static MultipartFile generateMultipartFile(String fileName) {
    return new MockMultipartFile("file", fileName, "text/csv", new byte[0]);
  }
}
