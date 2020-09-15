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
package com.github.searls.jasmine.driver;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Set;

class CustomDriverWithDefaultConstructor implements WebDriver {

  public CustomDriverWithDefaultConstructor() {

  }

  @Override
  public void get(String url) {
    throw new UnsupportedOperationException("Method not supported: get(String)");
  }

  @Override
  public String getCurrentUrl() {
    throw new UnsupportedOperationException("Method not supported: getCurrentUrl()");
  }

  @Override
  public String getTitle() {
    throw new UnsupportedOperationException("Method not supported: getTitle()");
  }

  @Override
  public List<WebElement> findElements(By by) {
    throw new UnsupportedOperationException("Method not supported: findElements(By)");
  }

  @Override
  public WebElement findElement(By by) {
    throw new UnsupportedOperationException("Method not supported: findElement(By)");
  }

  @Override
  public String getPageSource() {
    throw new UnsupportedOperationException("Method not supported: getPageSource()");
  }

  @Override
  public void close() {
    throw new UnsupportedOperationException("Method not supported: close()");
  }

  @Override
  public void quit() {
    throw new UnsupportedOperationException("Method not supported: quit()");
  }

  @Override
  public Set<String> getWindowHandles() {
    throw new UnsupportedOperationException("Method not supported: getWindowHandles()");
  }

  @Override
  public String getWindowHandle() {
    throw new UnsupportedOperationException("Method not supported: getWindowHandle()");
  }

  @Override
  public TargetLocator switchTo() {
    throw new UnsupportedOperationException("Method not supported: switchTo()");
  }

  @Override
  public Navigation navigate() {
    throw new UnsupportedOperationException("Method not supported: navigate()");
  }

  @Override
  public Options manage() {
    throw new UnsupportedOperationException("Method not supported: manage()");
  }
}
