package br.com.powerprogramers.product.domain.mappers;

import br.com.powerprogramers.product.domain.dto.CreateProductDto;
import br.com.powerprogramers.product.domain.dto.ProductDto;
import br.com.powerprogramers.product.domain.entity.ProductEntity;
import br.com.powerprogramers.product.domain.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/** Product class mapping class. */
@Mapper
public interface ProductMapper {

  ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

  /**
   * Converts a product Entity to a product DTO.
   *
   * @param product product to be converted
   * @return DTO product
   */
  ProductDto toDto(ProductEntity product);

  /**
   * Converts a product DTO to a product.
   *
   * @param product product DTO to be converted
   * @return product
   */
  Product toModel(ProductDto product);

  /**
   * Converts a CreateProductDto to a product.
   *
   * @param product CreateProductDto to be converted
   * @return product
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "active", ignore = true)
  Product toModel(CreateProductDto product);

  /**
   * Converts a product to a product Entity.
   *
   * @param product product to be converted
   * @return product
   */
  ProductEntity toEntity(Product product);
}
