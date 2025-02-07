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
public class ProductStepDefinition extends StepDefsDefault {

  @LocalServerPort private int port;

  private Response response;
  private ProductEntity productEntity;

  @When("sending a valid product")
  public void sending_a_valid_product() {
    var body = ProductHelper.generateCreateProductDto();

    response =
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(body)
            .when()
            .post("http://localhost:%d/products".formatted(port));
  }

  @Then("the product is successfully registered")
  public void the_product_is_successfully_registered() {
    response.then().statusCode(HttpStatus.CREATED.value());
  }

  @Given("that a product has already been registered")
  public void that_a_product_has_already_been_registered() {
    if (Objects.isNull(productEntity)) {
      sending_a_valid_product();
      productEntity = response.then().extract().as(ProductEntity.class);
    }
  }

  @When("sending a valid product update request")
  public void sending_a_valid_product_update_request() {
    var body = ProductHelper.generateUpdateProductDto();

    response =
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(body)
            .when()
            .patch("http://localhost:%d/products/%d".formatted(port, productEntity.getId()));
  }

  @Then("the product is displayed successfully")
  public void the_product_is_displayed_successfully() {
    response
        .then()
        .statusCode(HttpStatus.OK.value())
        .body(matchesJsonSchemaInClasspath("schemas/product.schema.json"));
  }

  @When("searching for the product")
  public void searching_for_the_product() {
    response =
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("http://localhost:%d/products/%d".formatted(port, productEntity.getId()));
  }

  @Given("that two products has already been registered")
  public void that_two_products_has_already_been_registered() {
    sending_a_valid_product();
    sending_a_valid_product();
  }

  @When("searching for all products")
  public void searching_for_all_products() {
    response =
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("http://localhost:%d/products".formatted(port));
  }

  @Then("the product are displayed successfully")
  public void the_product_are_displayed_successfully() {
    response
        .then()
        .statusCode(HttpStatus.OK.value())
        .body(matchesJsonSchemaInClasspath("schemas/paged.product.schema.json"));
  }

  @When("sending a request to disable the product")
  public void sending_a_request_to_disable_the_product() {
    response =
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get(
                "http://localhost:%d/products/deactivate/%d"
                    .formatted(port, productEntity.getId()));
  }

  @When("sending a request to activate the product")
  public void sending_a_request_to_activate_the_product() {
    response =
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("http://localhost:%d/products/activate/%d".formatted(port, productEntity.getId()));
  }
}
