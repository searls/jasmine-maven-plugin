/*-
 * #%L
 * jasmine-maven-plugin
 * %%
 * Copyright (C) 2010 - 2017 Justin Searls
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
