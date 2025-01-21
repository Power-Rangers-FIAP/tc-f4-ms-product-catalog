package br.com.powerprogramers.product.application.consumer;

import lombok.Getter;
import lombok.NoArgsConstructor;

/** Class that represents the product requested for the load. */
@NoArgsConstructor
@Getter
public class ProductRequested {  
  private Long id;
  private String name;
  private String description;
  private int amount;
  private Double price;
  private boolean active;
}
