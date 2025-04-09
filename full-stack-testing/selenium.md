## Commonly Used WebDriver Methods

### For interacting with web elements:
1. `findElement(By locator)` / `findElements(By locator)` - Find element(s)
2. `getText()` - Get visible text of an element
3. `getAttribute(String name)` - Get attribute value
4. `sendKeys(CharSequence... keysToSend)` - Type into input fields
5. `click()` - Click on an element
6. `isDisplayed()` - Check if element is visible
7. `isEnabled()` - Check if element is enabled
8. `isSelected()` - Check if checkbox/radio is selected
9. `clear()` - Clear input field
10. `submit()` - Submit a form

### For manipulating browser behavior:
1. `get(String url)` - Navigate to a URL
2. `getCurrentUrl()` - Get current page URL
3. `getTitle()` - Get page title
4. `navigate().to(String url)` - Alternative navigation
5. `navigate().back()` / `forward()` / `refresh()` - Browser history navigation
6. `manage().window().maximize()` - Maximize window
7. `manage().window().setSize(Dimension size)` - Set window size
8. `manage().timeouts()` - Set various timeout configurations
9. `manage().getCookies()` / `addCookie(Cookie cookie)` - Cookie management
10. `switchTo().alert()` - Handle JavaScript alerts
11. `switchTo().frame()` - Switch to iframes
12. `quit()` - Close browser and end session

