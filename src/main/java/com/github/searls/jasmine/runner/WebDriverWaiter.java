package com.github.searls.jasmine.runner;

import com.google.common.base.Predicate;
import org.apache.maven.plugin.logging.Log;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

class WebDriverWaiter {
  static final String EXECUTION_FINISHED_SCRIPT = "return (window.jsApiReporter === undefined) ? false : window.jsApiReporter.finished";

  void waitForRunnerToFinish(final WebDriver driver, final int timeout, final boolean debug, final Log log) throws InterruptedException {
    final JavascriptExecutor executor = (JavascriptExecutor) driver;
    try {
      new WebDriverWait(driver, timeout, 1000).until(new Predicate<WebDriver>() {
        @Override
        public boolean apply(WebDriver input) {
          return executionFinished(executor);
        }
      });
    } catch (TimeoutException e) {
      handleTimeout(timeout, debug, log);
    }

  }

  private Boolean executionFinished(final JavascriptExecutor driver) {
    return (Boolean) driver.executeScript(EXECUTION_FINISHED_SCRIPT);
  }

  private void handleTimeout(final int timeout, final boolean debug, final Log log) {
    log.warn("Attempted to wait for your specs to finish processing over the course of " +
      timeout +
      " seconds, but it still appears to be running.");
    if (debug) {
      log.warn("Debug mode: will attempt to parse the incomplete spec runner results");
    } else {
      throw new IllegalStateException("Timeout occurred. Aborting execution of specs. (Try configuring 'debug' to 'true' for more details.)");
    }
  }

}
