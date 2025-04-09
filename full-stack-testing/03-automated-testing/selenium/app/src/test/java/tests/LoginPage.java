package tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import selenium.HomePage;

public class LoginPage {
  private WebDriver driver;
  private By emailID = By.id("user_email");
  private By passwordField = By.id("user_password");
  private By signInButton = By.cssSelector("input.gr-button.gr-button--large");

  public LoginPage(WebDriver driver) {
    this.driver = driver;
  }

  public HomePage login(String email, String password) {
    driver.findElement(emailID).sendKeys(email);
    driver.findElement(passwordField).sendKeys(password);
    driver.findElement(signInButton).click();

    return new HomePage(driver);
  }

}
