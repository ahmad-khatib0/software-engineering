package tests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTests {
  protected WebDriver driver;

  @BeforeMethod
  public void setUp() {
    System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver");
    driver = new ChromeDriver();
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    driver.get("http://eCommerce.com/sign_in");
  }

  @AfterMethod
  public void teardown(ITestResult result) {
    if (ITestResult.FAILURE == result.getStatus()) {
      var camera = (TakesScreenshot) driver;
      File screenshot = camera.getScreenshotAs(OutputType.FILE);
      try {
        Files.move(screenshot.toPath(),
            new File("src/main/resources/screenshots/" + result.getName() + ".png").toPath());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    driver.quit();
  }
}
