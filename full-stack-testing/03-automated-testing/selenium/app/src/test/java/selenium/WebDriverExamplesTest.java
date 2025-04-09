package selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class WebDriverExamplesTest {
  private WebDriver driver;
  private WebDriverWait wait;
  private String samplePageUrl;

  @BeforeMethod
  public void setUp() {
    WebDriverManager.chromedriver().setup();

    ChromeOptions options = new ChromeOptions();
    options.setBinary("/usr/bin/chromium-browser");
    options.addArguments(
        "--no-sandbox",
        "--disable-dev-shm-usage",
        // "--headless=new", // Remove this if you want to see the browser
        "--window-size=1920,1080");

    driver = new ChromeDriver(options);

    wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    // Get the path to the sample HTML file
    samplePageUrl = getClass().getClassLoader().getResource("sample_page.html").toString();
    driver.get(samplePageUrl);
  }

  @AfterMethod
  public void tearDown() {
    if (driver != null) {
      driver.quit();
    }
  }

  @Test(timeOut = 30000)
  public void testElementInteractionMethods() {
    // 1. Finding elements
    WebElement header = driver.findElement(By.id("main-header"));
    WebElement usernameInput = driver.findElement(By.name("username"));
    WebElement loginButton = driver.findElement(By.cssSelector("#login-btn"));
    List<WebElement> listItems = driver.findElements(By.className("item"));

    // 2. Getting element properties
    System.out.println("Header text: " + header.getText());
    System.out.println("Username placeholder: " + usernameInput.getAttribute("placeholder"));
    System.out.println("Is register button enabled: " + driver.findElement(By.id("register-btn")).isEnabled());
    System.out.println("Number of list items: " + listItems.size());

    // 3. Interacting with elements
    usernameInput.sendKeys("testuser");
    driver.findElement(By.id("password")).sendKeys("password123");
    loginButton.click();

    // Handle alert
    Alert alert = wait.until(ExpectedConditions.alertIsPresent());
    System.out.println("Alert text: " + alert.getText());
    alert.accept();

    // 4. Working with dropdown
    Select dropdown = new Select(driver.findElement(By.id("dropdown")));
    dropdown.selectByVisibleText("Option 2");
    System.out.println("Selected option: " + dropdown.getFirstSelectedOption().getText());

    // 5. Working with lists
    for (WebElement item : listItems) {
      System.out.println("List item: " + item.getText());
    }
  }

  @Test
  public void testBrowserManipulationMethods() {
    // 1. Window management
    System.out.println("Current URL: " + driver.getCurrentUrl());
    System.out.println("Page title: " + driver.getTitle());

    // 2. Navigation
    driver.navigate().refresh();
    driver.navigate().to("https://www.google.com");
    driver.navigate().back();
    System.out.println("After navigation, URL is: " + driver.getCurrentUrl());

    // 3. Window size and position
    driver.manage().window().maximize();
    Point position = driver.manage().window().getPosition();
    Dimension size = driver.manage().window().getSize();
    System.out.println("Window position: " + position);
    System.out.println("Window size: " + size);

    // 4. Timeouts
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));

    // 5. Cookies
    // Start on a real website that accepts cookies
    driver.get("https://example.com");
    driver.manage().addCookie(new Cookie("test_cookie", "cookie_value"));
    System.out.println("Cookie value: " + driver.manage().getCookieNamed("test_cookie").getValue());

    // 6. JavaScript execution
    driver.get(samplePageUrl);
    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript("toggleElement();");
    WebElement hiddenElement = driver.findElement(By.id("hidden-element"));
    wait.until(ExpectedConditions.visibilityOf(hiddenElement));
    System.out.println("Hidden element is now visible: " + hiddenElement.isDisplayed());

    // 7. Screenshot (would need additional dependencies for saving)
    // File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);

    // 8. Actions class for complex interactions
    Actions actions = new Actions(driver);
    WebElement visibleElement = driver.findElement(By.id("visible-element"));
    actions.moveToElement(visibleElement).contextClick().perform();
  }

  @Test
  public void testWaitConditions() {
    // Explicit wait examples
    WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));
    wait.until(ExpectedConditions.elementToBeClickable(By.id("login-btn")));
    wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("main-header"), "Welcome"));
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("visible-element")));
  }
}
