package br.com.powerprogramers.product.domain.controller;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
class StockControllerIT {

  @LocalServerPort protected int port;

  @BeforeEach
  void setup() {
    RestAssured.port = port;
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  @Test
  void mustUpdateStockSuccessfully() {

    given()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .param("amount", 10)
        .when()
        .patch("/stock/{id}", 1L)
        .then()
        .statusCode(HttpStatus.OK.value())
        .body(matchesJsonSchemaInClasspath("schemas/product.schema.json"));
  }

  @Test
  void mustGenerateException_WhenUpdateStockFails() {
    var errorMessage = "The mandatory parameter 'amount' it is absent";
    var erro =
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .patch("/stock/{id}", 1L)
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body(matchesJsonSchemaInClasspath("schemas/domain.exception.schema.json"))
            .extract()
            .response()
            .jsonPath()
            .get("message");

    assertEquals(errorMessage, erro);
  }
}
