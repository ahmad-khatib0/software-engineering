package restassured;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static io.restassured.http.ContentType.JSON;

class ItemAPITest extends TestConfig {

  @Test
  void getAllItems_ShouldReturn200WithItemsList() {
    given()
        .when()
        .get("/items")
        .then()
        .statusCode(200)
        .body("$", hasSize(greaterThanOrEqualTo(1)))
        .body("[0].sku", equalTo("984058981"))
        .body("[0].color", equalTo("Green"))
        .body("[0].size", equalTo("M"));
  }

  @Test
  void getAllItems_ShouldContainSpecificItem() {
    Response response = given()
        .when()
        .get("/items")
        .then()
        .extract().response();

    // Using AssertJ for more complex assertions
    assertEquals(200, response.statusCode());

    String firstItemSku = response.jsonPath().getString("[0].sku");
    assertEquals("984058981", firstItemSku);

    String firstItemColor = response.jsonPath().getString("[0].color");
    assertEquals("Green", firstItemColor);
  }

  @Test
  void getAllItems_ShouldHaveCorrectContentType() {
    given()
        .when()
        .get("/items")
        .then()
        .statusCode(200)
        .contentType("application/json");
  }

  @Test
  void getAllItems_ShouldHaveExpectedStructure() {
    given()
        .when()
        .get("/items")
        .then()
        .body("[0]", hasKey("sku"))
        .body("[0]", hasKey("color"))
        .body("[0]", hasKey("size"));
  }

  @Test
  void createItem_ShouldReturn201WithCreatedItem() {
    String requestBody = """
        {
            "sku": "987654321",
            "color": "Yellow",
            "size": "XS"
        }
        """;

    given()
        .contentType(JSON)
        .body(requestBody)
        .when()
        .post("/items")
        .then()
        .statusCode(201)
        .body("sku", equalTo("987654321"))
        .body("color", equalTo("Yellow"))
        .body("size", equalTo("XS"));
  }
}
