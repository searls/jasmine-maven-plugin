package com.github.searls.jasmine.driver;

import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
*/
class CustomDriverWithDefaultConstructor extends HtmlUnitDriver {
  public CustomDriverWithDefaultConstructor() {
    setJavascriptEnabled(false);
  }
}
