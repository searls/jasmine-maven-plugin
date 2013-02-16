package com.github.searls.jasmine.driver;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 */
class CustomDriverWithCapabilities extends HtmlUnitDriver {
  public final Capabilities capabilities;

  public CustomDriverWithCapabilities(Capabilities capabilities) {
    super(capabilities);
    this.capabilities = capabilities;
  }
}
