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
package com.github.searls.jasmine.coffee;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DetectsCoffeeTest {

  private final DetectsCoffee subject = new DetectsCoffee();

  @Test
  public void whenAStringEndingInCoffeeThatsCoffee() {
    assertThat(subject.detect("/some/path/to/pants.coffee")).isTrue();
  }

  @Test
  public void whenAStringDoesNotEndInCoffeeThatsNotCoffee() {
    assertThat(subject.detect("/some/path/to/pants.cafe")).isFalse();
  }

  @Test
  public void whenCoffeeHasAQueryStringThatsCoffee() {
    assertThat(subject.detect("/some/path/to/pants.coffee?stillCoffee=true")).isTrue();
  }

  @Test
  public void whenJavaScriptHasACoffeeQueryThatsNotCoffee() {
    assertThat(subject.detect("/some/path/to/pants.cafe")).isFalse();
  }
}
