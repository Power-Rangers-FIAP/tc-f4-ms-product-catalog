package br.com.powerprogramers.product.domain.batch.job;

import br.com.powerprogramers.product.domain.model.ProductLoad;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.lang.NonNull;

/** Class that maps the fields of the product load file. */
public class ProductLoadMapper implements FieldSetMapper<ProductLoad> {
  @Override
  @NonNull
  public ProductLoad mapFieldSet(FieldSet fieldSet) {
    return ProductLoad.builder()
        .name(fieldSet.readString("name"))
        .description(fieldSet.readString("description"))
        .amount(fieldSet.readInt("amount"))
        .price(fieldSet.readBigDecimal("price"))
        .active(fieldSet.readBoolean("active"))
        .build();
  }
}
