package com.github.searls.jasmine.driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

class DriverManagerAdapter {

  public void setupDriver(Class<? extends WebDriver> driverClass) {
    WebDriverManager.getInstance(driverClass).setup();
  }

  public WebDriver createChromeDriver(ChromeOptions chromeOptions) {
    return new ChromeDriver(chromeOptions);
  }

}
