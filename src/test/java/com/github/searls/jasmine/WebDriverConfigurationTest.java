package com.github.searls.jasmine;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class WebDriverConfigurationTest {
  private String webDriverClassName;
  private String remoteWebDriverUrl;
  private String browserVersion = "FIREFOX_3_6";
  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Before
  public void setUp() {
    webDriverClassName = "org.openqa.selenium.htmlunit.HtmlUnitDriver";

  }

  private WebDriver createWebDriver() {
    return new WebDriverConfiguration(webDriverClassName, remoteWebDriverUrl, browserVersion, false).createWebDriver();
  }

  @Test
  public void createsDefaultWebDriverIfClassNameIsDefault() {
    assertTrue(createWebDriver() instanceof HtmlUnitDriver);
  }

  @Test
  public void createsCustomWebDriverIfSpecified() {
    WebDriver mockDriver = mock(WebDriver.class);
    webDriverClassName = mockDriver.getClass().getName();

    assertEquals(mockDriver.getClass(), createWebDriver().getClass());
  }

  @Test
  public void throwsExceptionIfCustomWebDriverCannotBeCreated() {
    expectedException.expect(RuntimeException.class);
    webDriverClassName = "fooBar";
    expectedException.expectMessage("Couldn't instantiate " + webDriverClassName);

    createWebDriver();
  }

  @Test
  public void createsRemoteWebDriverIfRemoteUrlIsConfigured() {
    expectedException.expect(UnreachableBrowserException.class);
    remoteWebDriverUrl = "http://localhost:1234";
    RemoteWebDriver remoteWebDriver = (RemoteWebDriver) createWebDriver();

    assertEquals(remoteWebDriverUrl, remoteWebDriver.getCurrentUrl());
  }
}
