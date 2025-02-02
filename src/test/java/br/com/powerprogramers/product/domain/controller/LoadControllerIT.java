package br.com.powerprogramers.product.domain.controller;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.assertEquals;

import br.com.powerprogramers.product.domain.utils.LoadHelper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@Transactional
@RequiredArgsConstructor
class LoadControllerIT {

  @LocalServerPort protected int port;

  @BeforeEach
  void setup() {
    RestAssured.port = port;
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  @SneakyThrows
  @Test
  void mustLoadDataSuccessfully() {

    MultipartFile multipartFile = LoadHelper.generateMultipartFile("test.csv");

    given()
        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
        .multiPart("file", multipartFile.getOriginalFilename(), multipartFile.getBytes())
        .when()
        .post("/load")
        .then()
        .statusCode(HttpStatus.OK.value());

    deleteFiles();
  }

  @Test
  void mustGenerateException_WhenLoadDataFails() throws IOException {

    MultipartFile multipartFile = LoadHelper.generateMultipartFile("test?.csv");

    given()
        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
        .multiPart("file", multipartFile.getOriginalFilename(), multipartFile.getBytes())
        .when()
        .post("/load")
        .then()
        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .body(matchesJsonSchemaInClasspath("schemas/domain.exception.schema.json"));
  }

  @Test
  void mustGenerateException_WhenLoadNotCsv() {
    given()
        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
        .multiPart("file", "test.txt", new byte[0])
        .when()
        .post("/load")
        .then()
        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .body(matchesJsonSchemaInClasspath("schemas/domain.exception.schema.json"));
  }

  @Test
  void mustScheduleJobSuccessfully() throws IOException {
    OffsetDateTime offsetDateTime = OffsetDateTime.now().plusMinutes(30L);
    MultipartFile multipartFile = LoadHelper.generateMultipartFile("test.csv");

    createJob(offsetDateTime.toString(), multipartFile)
        .then()
        .statusCode(HttpStatus.OK.value())
        .body(matchesJsonSchemaInClasspath("schemas/load.job.dto.schema.json"));
  }

  @Test
  void mustCancelJobSuccessfully() throws IOException {
    OffsetDateTime offsetDateTime = OffsetDateTime.now().plusMinutes(30L);
    MultipartFile multipartFile = LoadHelper.generateMultipartFile("test.csv");

    var jobId =
        createJob(offsetDateTime.toString(), multipartFile)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(matchesJsonSchemaInClasspath("schemas/load.job.dto.schema.json"))
            .extract()
            .response()
            .jsonPath()
            .get("id");

    var msg =
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .delete("/load/job/{id}", jobId)
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .body()
            .asString();

    assertEquals("Job successfully canceled!", msg);
  }

  @Test
  void mustCancelJob_WhenJobIdDoNotExists() {
    var msg =
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .delete("/load/job/{id}", "15")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .extract()
            .body()
            .asString();

    assertEquals("Job not found", msg);
  }

  private Response createJob(String offsetDateTime, MultipartFile multipartFile)
      throws IOException {
    return given()
        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
        .param("dateTime", offsetDateTime)
        .multiPart("file", multipartFile.getOriginalFilename(), multipartFile.getBytes())
        .when()
        .post("/load/job");
  }

  private void deleteFiles() {
    String absolutePath =
        new FileSystemResource("").getFile().getAbsolutePath()
            + "/src/test/resources/load/processed";
    Path path = Paths.get(absolutePath);
    try (Stream<Path> sPaths =
        Files.list(path).filter(f -> f.toFile().getName().endsWith(".csv"))) {
      for (Path p : sPaths.toList()) {
        Files.deleteIfExists(p);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
