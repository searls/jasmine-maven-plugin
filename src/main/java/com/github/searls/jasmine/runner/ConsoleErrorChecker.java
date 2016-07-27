package com.github.searls.jasmine.runner;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

class ConsoleErrorChecker {
  void checkForConsoleErrors(final WebDriver driver, final Log log) {
    final WebElement head = driver.findElement(By.tagName("head"));
    if (head != null) {
      String jserrors = head.getAttribute("jmp_jserror");
      if (StringUtils.isNotBlank(jserrors)) {
        String errors = "JavaScript Console Errors:\n\n  * " + jserrors.replaceAll(":!:", "\n  * ") + "\n\n";
        log.warn(errors);
        throw new RuntimeException("There were javascript console errors.\n\n" + errors);
      }
    }
  }
}
