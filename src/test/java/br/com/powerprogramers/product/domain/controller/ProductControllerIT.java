package br.com.powerprogramers.product.domain.controller;

import static br.com.powerprogramers.product.domain.utils.JsonUtil.toJson;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.assertEquals;

import br.com.powerprogramers.product.domain.dto.CreateProductDto;
import br.com.powerprogramers.product.domain.dto.UpdateProductDto;
import br.com.powerprogramers.product.domain.utils.ProductHelper;
import io.restassured.RestAssured;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@Transactional
@RequiredArgsConstructor
class ProductControllerIT {

  @LocalServerPort protected int port;

  @BeforeEach
  void setup() {
    RestAssured.port = port;
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  @Nested
  class Create {
    @Test
    void mustRegisterProductSuccessfully() {
      CreateProductDto createProductDto = ProductHelper.generateCreateProductDto();

      given()
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .body(toJson(createProductDto))
          .when()
          .post("/products")
          .then()
          .statusCode(HttpStatus.CREATED.value())
          .body(matchesJsonSchemaInClasspath("schemas/product.schema.json"));
    }

    @Test
    void mustGenerateException_WhenRegisterProduct_WithNameIsEmpty() {
      var erroMessage = "Product name cannot be empty";
      CreateProductDto createProductDto = ProductHelper.generateCreateProductDto();
      createProductDto.name("");

      var erro =
          given()
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .body(toJson(createProductDto))
              .when()
              .post("/products")
              .then()
              .statusCode(HttpStatus.BAD_REQUEST.value())
              .body(matchesJsonSchemaInClasspath("schemas/domain.exception.schema.json"))
              .extract()
              .response()
              .jsonPath()
              .get("message");

      assertEquals(erroMessage, erro);
    }

    @Test
    void mustGenerateException_WhenRegisterProduct_WithDescriptionIsEmpty() {
      var erroMessage = "Product description cannot be empty";
      CreateProductDto createProductDto = ProductHelper.generateCreateProductDto();
      createProductDto.description("");

      var erro =
          given()
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .body(toJson(createProductDto))
              .when()
              .post("/products")
              .then()
              .statusCode(HttpStatus.BAD_REQUEST.value())
              .body(matchesJsonSchemaInClasspath("schemas/domain.exception.schema.json"))
              .extract()
              .response()
              .jsonPath()
              .get("message");

      assertEquals(erroMessage, erro);
    }

    @Test
    void mustGenerateException_WhenRegisterProduct_WithAmountIsZeroOrNegative() {
      var erroMessage = "Product amount cannot be zero or negative";
      CreateProductDto createProductDto = ProductHelper.generateCreateProductDto();
      createProductDto.amount(-1);

      var erro =
          given()
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .body(toJson(createProductDto))
              .when()
              .post("/products")
              .then()
              .statusCode(HttpStatus.BAD_REQUEST.value())
              .body(matchesJsonSchemaInClasspath("schemas/domain.exception.schema.json"))
              .extract()
              .response()
              .jsonPath()
              .get("message");

      assertEquals(erroMessage, erro);
    }

    @Test
    void mustGenerateException_WhenRegisterProduct_WithPriceIsZeroOrNegative() {
      var erroMessage = "Product price cannot be zero or negative";
      CreateProductDto createProductDto = ProductHelper.generateCreateProductDto();
      createProductDto.price(BigDecimal.ZERO);

      var erro =
          given()
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .body(toJson(createProductDto))
              .when()
              .post("/products")
              .then()
              .statusCode(HttpStatus.BAD_REQUEST.value())
              .body(matchesJsonSchemaInClasspath("schemas/domain.exception.schema.json"))
              .extract()
              .response()
              .jsonPath()
              .get("message");

      assertEquals(erroMessage, erro);
    }
  }

  @Nested
  class Update {
    @Test
    void mustUpdateProductSuccessfully() {
      UpdateProductDto updateProductDto = ProductHelper.generateUpdateProductDto();

      given()
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .body(toJson(updateProductDto))
          .when()
          .patch("/products/{id}", 1L)
          .then()
          .statusCode(HttpStatus.OK.value())
          .body(matchesJsonSchemaInClasspath("schemas/product.schema.json"));
    }

    @Test
    void mustGenerateException_WhenUpdateProduct_WithHttpMessageNotReadable() {
      var erroMessage = "Failed to read request";
      var erro =
          given()
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .when()
              .patch("/products/{id}", 1L)
              .then()
              .statusCode(HttpStatus.BAD_REQUEST.value())
              .body(matchesJsonSchemaInClasspath("schemas/domain.exception.schema.json"))
              .extract()
              .response()
              .jsonPath()
              .get("message");

      assertEquals(erroMessage, erro);
    }

    @Test
    void mustGenerateException_WhenUpdateProduct_WithMethodArgumentNotValid() {
      var erroMessage =
          "{price=não deve ser nulo, name=não deve ser nulo, description=não deve ser nulo}";
      var erro =
          given()
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .body("{}")
              .when()
              .patch("/products/{id}", 1L)
              .then()
              .statusCode(HttpStatus.BAD_REQUEST.value())
              .body(matchesJsonSchemaInClasspath("schemas/domain.exception.schema.json"))
              .extract()
              .response()
              .jsonPath()
              .get("message");

      assertEquals(erroMessage, erro);
    }

    @Test
    void mustGenerateException_WhenUpdateProduct_WithHttpMediaTypeNotSupported() {
      var erroMessage = "Content-Type 'null' is not supported.";
      var erro =
          given()
              .when()
              .patch("/products/{id}", 1L)
              .then()
              .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
              .body(matchesJsonSchemaInClasspath("schemas/domain.exception.schema.json"))
              .extract()
              .response()
              .jsonPath()
              .get("message");

      assertEquals(erroMessage, erro);
    }
  }

  @Nested
  class Find {

    @Test
    void mustFindProductByIdSuccessfully() {
      given()
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .when()
          .get("/products/{id}", 1L)
          .then()
          .statusCode(HttpStatus.OK.value())
          .body(matchesJsonSchemaInClasspath("schemas/product.schema.json"));
    }

    @Test
    void mustGenerateException_WhenFindProductById_WithIllegalArgument() {
      var erroMessage = "Error converting id with value of type a";
      var erro =
          given()
              .when()
              .get("/products/{id}", "a")
              .then()
              .statusCode(HttpStatus.BAD_REQUEST.value())
              .body(matchesJsonSchemaInClasspath("schemas/domain.exception.schema.json"))
              .extract()
              .response()
              .jsonPath()
              .get("message");

      assertEquals(erroMessage, erro);
    }

    @Test
    void mustFindAllProductsSuccessfully() {
      Map<String, String> params = new HashMap<>();
      params.put("page", "0");
      params.put("perPage", "10");
      params.put("name", "");
      params.put("description", "");
      params.put("active", "true");

      given()
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .queryParams(params)
          .when()
          .get("/products")
          .then()
          .statusCode(HttpStatus.OK.value())
          .body(matchesJsonSchemaInClasspath("schemas/paged.product.schema.json"));
    }

    @Test
    void mustGenerateException_WhenFindAllProducts_WithArgumentTypeMismatch() {
      Map<String, String> params = new HashMap<>();
      params.put("page", "a");

      given()
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .queryParams(params)
          .when()
          .get("/products")
          .then()
          .statusCode(HttpStatus.BAD_REQUEST.value())
          .body(matchesJsonSchemaInClasspath("schemas/domain.exception.schema.json"));
    }
  }

  @Nested
  class Activate {
    @Test
    void mustActivateProductSuccessfully() {
      given()
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .when()
          .get("/products/activate/{id}", 2L)
          .then()
          .statusCode(HttpStatus.OK.value())
          .body(matchesJsonSchemaInClasspath("schemas/product.schema.json"));
    }
  }

  @Test
  void mustDeactivateProductSuccessfully() {
    given()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .get("/products/deactivate/{id}", 1L)
        .then()
        .statusCode(HttpStatus.OK.value())
        .body(matchesJsonSchemaInClasspath("schemas/product.schema.json"));
  }
}
