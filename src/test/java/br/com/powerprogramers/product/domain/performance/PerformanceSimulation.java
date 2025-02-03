package br.com.powerprogramers.product.domain.performance;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import br.com.powerprogramers.product.domain.utils.JsonUtil;
import br.com.powerprogramers.product.domain.utils.ProductHelper;
import io.gatling.javaapi.core.ActionBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import java.time.Duration;

public class PerformanceSimulation extends Simulation {
  private final HttpProtocolBuilder httpProtocolBuilder =
      http.baseUrl("http://localhost:8081").header("Content-Type", "application/json");

  private final String createProductDto = JsonUtil.toJson(ProductHelper.generateCreateProductDto());
  private final String updateProductDto = JsonUtil.toJson(ProductHelper.generateUpdateProductDto());

  ActionBuilder registerProduct =
      http("register product")
          .post("/products")
          .body(StringBody(createProductDto))
          .check(status().is(201))
          .check(jsonPath("$.id").saveAs("productId"));

  ActionBuilder findProduct =
      http("find product").get("/products/%s".formatted("#{productId}")).check(status().is(200));

  ActionBuilder updateProduct =
      http("update product")
          .patch("/products/%s".formatted("#{productId}"))
          .body(StringBody(updateProductDto))
          .check(status().is(200));

  ActionBuilder deactivateProduct =
      http("deactivate product")
          .get("/products/deactivate/%s".formatted("#{productId}"))
          .check(status().is(200));

  ActionBuilder activateProduct =
      http("activate product")
          .get("/products/activate/%s".formatted("#{productId}"))
          .check(status().is(200));

  ActionBuilder updateStock =
      http("update stock")
          .patch("/stock/%s".formatted("#{productId}"))
          .queryParam("amount", 10)
          .check(status().is(200));

  ScenarioBuilder scenarioCrudProduct =
      scenario("product crud")
          .exec(registerProduct)
          .exec(findProduct)
          .exec(updateProduct)
          .exec(deactivateProduct)
          .exec(activateProduct)
          .exec(updateStock);

  {
    setUp(
            scenarioCrudProduct.injectOpen(
                rampUsersPerSec(1).to(5).during(Duration.ofSeconds(5)),
                constantUsersPerSec(5).during(Duration.ofSeconds(15)),
                rampUsersPerSec(5).to(1).during(Duration.ofSeconds(5))))
        .protocols(httpProtocolBuilder)
        .assertions(
            global().responseTime().max().lt(200), global().failedRequests().count().is(0L));
  }
}
