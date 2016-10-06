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
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;

@Named
class WebDriverWaiter {

  private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverWaiter.class);

  protected static final String EXECUTION_FINISHED_SCRIPT =
    "return (window.jsApiReporter === undefined) ? false : window.jsApiReporter.finished";

  private static final String TIMEOUT_WARNING =
    "Attempted to wait for your specs to finish processing over the course of {} seconds, but it still appears to be running.";

  private static final String DEBUG_MODE_TIMEOUT_WARNING =
    "Debug mode: will attempt to parse the incomplete spec runner results";

  private static final String TIMEOUT_ERROR_MESSAGE =
    "Timeout occurred. Aborting execution of specs. (Try configuring 'debug' to 'true' for more details.)";

  public void waitForRunnerToFinish(final WebDriver driver,
                                    final int timeout,
                                    final boolean debug) throws InterruptedException {
    final JavascriptExecutor executor = (JavascriptExecutor) driver;
    try {
      new WebDriverWait(driver, timeout, 1000).until(new Predicate<WebDriver>() {
        @Override
        public boolean apply(WebDriver input) {
          return executionFinished(executor);
        }
      });
    } catch (TimeoutException e) {
      handleTimeout(timeout, debug);
    }

  }

  private Boolean executionFinished(final JavascriptExecutor driver) {
    return (Boolean) driver.executeScript(EXECUTION_FINISHED_SCRIPT);
  }

  private void handleTimeout(final int timeout, final boolean debug) {
    LOGGER.warn(String.format(TIMEOUT_WARNING, timeout));
    if (debug) {
      LOGGER.warn(DEBUG_MODE_TIMEOUT_WARNING);
    } else {
      throw new IllegalStateException(TIMEOUT_ERROR_MESSAGE);
    }
  }
}
