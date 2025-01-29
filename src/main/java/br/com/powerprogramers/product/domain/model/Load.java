package br.com.powerprogramers.product.domain.model;

import br.com.powerprogramers.product.domain.exceptions.ProductLoadJobException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

/** Class that represents a load of products. */
@Getter
public class Load {
  private final String name;
  private final byte[] binary;
  private final Path path;
  private final LocalDateTime dateTime;

  public Load(MultipartFile multipartFile, String directory) throws IOException {
    this.binary = multipartFile.getBytes();
    this.dateTime = LocalDateTime.now();
    var nameAux = Objects.requireNonNull(multipartFile.getOriginalFilename());
    if (!nameAux.endsWith(".csv")) {
      throw new ProductLoadJobException("File name must end with .csv");
    }
    this.name = nameAux;
    this.path = Paths.get(directory);
  }

  public Path getFullPath() {
    return this.path.resolve(this.name);
  }
}
