package br.com.powerprogramers.product.domain.consumer;

import static br.com.powerprogramers.product.domain.utils.JsonUtil.toJson;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import br.com.powerprogramers.product.domain.model.ProductRequested;
import io.restassured.RestAssured;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
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
class ProductConsumerIT {

  @LocalServerPort protected int port;

  @BeforeEach
  void setup() {
    RestAssured.port = port;
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  @Test
  void mustConsumeProductSuccessfully() {
    ProductRequested productRequested = new ProductRequested(1L, 10);

    given()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(toJson(productRequested))
        .when()
        .post("/api/consumer-remove-stock")
        .then()
        .statusCode(HttpStatus.ACCEPTED.value());
  }

  @Test
  void mustThrowException_WhenInvalidAmount() {
    var erroMessage = "The amount cannot be negative or zero.";
    ProductRequested productRequested = new ProductRequested(1L, 0);

    var msg =
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(toJson(productRequested))
            .when()
            .post("/api/consumer-remove-stock")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .extract()
            .jsonPath()
            .get("message");

    assertEquals(erroMessage, msg);
  }

  @Test
  void mustThrowException_WhenTotalAmountIsNegative() {
    var erroMessage = "The amount total cannot be negative.";
    ProductRequested productRequested = new ProductRequested(1L, 155);

    var msg =
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(toJson(productRequested))
            .when()
            .post("/api/consumer-remove-stock")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .extract()
            .jsonPath()
            .get("message");

    assertEquals(erroMessage, msg);
  }
}
