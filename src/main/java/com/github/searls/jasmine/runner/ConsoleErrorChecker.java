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
