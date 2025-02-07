package br.com.powerprogramers.product.bdd;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import br.com.powerprogramers.product.domain.utils.LoadHelper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import java.io.File;
import java.io.IOException;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.hamcrest.core.IsEqual;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@RequiredArgsConstructor
@AutoConfigureTestDatabase
public class LoadStepDefinition {

  @LocalServerPort private int port;

  private Response response;
  private File multipartFile;

  @Given("that a csv file is available and valid")
  public void that_a_csv_file_is_available_and_valid() throws IOException {
    this.multipartFile = LoadHelper.generateFile();
  }

  @When("sending a request to load the file")
  public void sending_a_request_to_load_the_file() {
    response =
        given().multiPart(multipartFile).when().post("http://localhost:%d/load".formatted(port));
  }

  @Then("the file is loaded successfully")
  public void the_file_is_loaded_successfully() {
    response.then().statusCode(HttpStatus.OK.value());
    var path = multipartFile.toPath().toString();
    path = path.substring(0, path.lastIndexOf("\\"));
    LoadHelper.deleteTestFiles(path);
  }

  @When("scheduling the load of the file")
  public void scheduling_the_load_of_the_file() {
    var dateTime = OffsetDateTime.now().plusMinutes(15L).toString();
    response =
        given()
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .param("dateTime", dateTime)
            .multiPart("file", multipartFile)
            .when()
            .post("http://localhost:%d/load/job".formatted(port));
  }

  @Then("the file is scheduled to be loaded")
  public void the_file_is_scheduled_to_be_loaded() {
    response
        .then()
        .statusCode(HttpStatus.OK.value())
        .body(matchesJsonSchemaInClasspath("schemas/load.job.dto.schema.json"));
  }

  @When("cancelling the load of the file")
  public void cancelling_the_load_of_the_file() {
    var id = response.jsonPath().getString("id");
    response = given().when().delete("http://localhost:%d/load/job/%s".formatted(port, id));
  }

  @Then("the file is not loaded")
  public void the_file_is_not_loaded() {
    response
        .then()
        .statusCode(HttpStatus.OK.value())
        .body(IsEqual.equalTo("Job successfully canceled!"));
    multipartFile.deleteOnExit();
  }
}
