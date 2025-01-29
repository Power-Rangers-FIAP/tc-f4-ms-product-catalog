package br.com.powerprogramers.product.domain.service.usecase.create;

import br.com.powerprogramers.product.domain.exceptions.CreateProductUseCaseException;
import br.com.powerprogramers.product.domain.model.Product;
import br.com.powerprogramers.product.domain.service.usecase.UseCase;
import java.math.BigDecimal;
import org.springframework.util.ObjectUtils;

/** Class that controls product creation. */
public class CreateProductUseCase implements UseCase<Product, Product> {

  @Override
  public Product execute(Product input) {

    if (ObjectUtils.isEmpty(input.getName())) {
      throw new CreateProductUseCaseException("Product name cannot be empty");
    }

    if (ObjectUtils.isEmpty(input.getDescription())) {
      throw new CreateProductUseCaseException("Product description cannot be empty");
    }

    if (input.getAmount() <= 0) {
      throw new CreateProductUseCaseException("Product amount cannot be zero or negative");
    }

    if (input.getPrice().compareTo(BigDecimal.ZERO) == 0) {
      throw new CreateProductUseCaseException("Product price cannot be zero or negative");
    }

    return input;
  }
}
