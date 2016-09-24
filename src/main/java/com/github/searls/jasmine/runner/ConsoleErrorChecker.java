package com.github.searls.jasmine.runner;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;

@Named
class ConsoleErrorChecker {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleErrorChecker.class);

  void checkForConsoleErrors(final WebDriver driver) {
    final WebElement head = driver.findElement(By.tagName("head"));
    if (head != null) {
      String jserrors = head.getAttribute("jmp_jserror");
      if (StringUtils.isNotBlank(jserrors)) {
        String errors = "JavaScript Console Errors:\n\n  * " + jserrors.replaceAll(":!:", "\n  * ") + "\n\n";
        LOGGER.warn(errors);
        throw new RuntimeException("There were javascript console errors.\n\n" + errors);
      }
    }
  }
}
