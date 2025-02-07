package br.com.powerprogramers.product.bdd;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import br.com.powerprogramers.product.domain.entity.ProductEntity;
import br.com.powerprogramers.product.domain.utils.ProductHelper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@RequiredArgsConstructor
@AutoConfigureTestDatabase
public class StockStepDefinition extends StepDefsDefault {

  @LocalServerPort private int port;

  private ProductEntity productEntity;
  private Response response;

  @Given("that a product has already been registered with a valid stock")
  public void that_a_product_has_already_been_registered_with_a_valid_stock() {
    if (Objects.isNull(productEntity)) {
      var body = ProductHelper.generateCreateProductDto();

      productEntity =
          given()
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .body(body)
              .when()
              .post("http://localhost:%d/products".formatted(port))
              .then()
              .extract()
              .as(ProductEntity.class);
    }
  }

  @When("sending a valid product stock update request")
  public void sending_a_valid_product_stock_update_request() {
    response =
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .queryParams("amount", 75)
            .when()
            .patch("http://localhost:%d/stock/%s".formatted(port, productEntity.getId()));
  }

  @Then("the product stock is displayed successfully")
  public void the_product_stock_is_displayed_successfully() {
    response
        .then()
        .statusCode(HttpStatus.OK.value())
        .body(matchesJsonSchemaInClasspath("schemas/product.schema.json"));
  }
}
