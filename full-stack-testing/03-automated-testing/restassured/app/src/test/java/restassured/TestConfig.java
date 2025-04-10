package restassured;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

public class TestConfig {

  @BeforeAll
  public static void setup() {
    // Configure REST Assured to point to your running API
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = 4000;
    RestAssured.basePath = "/";

    // Enable logging if validation fails
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }
}
