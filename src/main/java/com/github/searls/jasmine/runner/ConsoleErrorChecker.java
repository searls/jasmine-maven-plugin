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

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import java.util.Collections;
import java.util.List;

@Named
class ConsoleErrorChecker {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleErrorChecker.class);

  void checkForConsoleErrors(final WebDriver driver) {
    List<String> errorMessages = getErrors(driver);

    if (!errorMessages.isEmpty()) {
      String errors = "JavaScript Console Errors:\n\n  * " + String.join("\n  * ", errorMessages) + "\n\n";
      LOGGER.warn(errors);
      throw new RuntimeException("There were javascript console errors.\n\n" + errors);
    }
  }

  private List<String> getErrors(WebDriver driver) {
    JavascriptExecutor executor = (JavascriptExecutor) driver;

    @SuppressWarnings("unchecked")
    List<String> errorMessages = (List<String>) executor.executeScript(
      "return window.getConsoleErrors ? window.getConsoleErrors() : [];"
    );

    return errorMessages != null ? errorMessages : Collections.emptyList();
  }
}
