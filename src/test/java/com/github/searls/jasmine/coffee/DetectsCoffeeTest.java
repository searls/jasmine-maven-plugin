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

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class DetectsCoffeeTest {

  private final DetectsCoffee subject = new DetectsCoffee();

  @Test
  public void whenAStringEndingInCoffeeThatsCoffee() {
    assertThat("/some/path/to/pants.coffee", is(this.coffee()));
  }

  @Test
  public void whenAStringDoesNotEndInCoffeeThatsNotCoffee() {
    assertThat("/some/path/to/pants.cafe", is(not(this.coffee())));
  }

  @Test
  public void whenCoffeeHasAQueryStringThatsCoffee() {
    assertThat("/some/path/to/pants.coffee?stillCoffee=true", is(this.coffee()));
  }

  @Test
  public void whenJavaScriptHasACoffeeQueryThatsNotCoffee() {
    assertThat("/some/path/to/pants.js?extension=lulz.coffee", is(not(this.coffee())));
  }

  private TypeSafeMatcher<String> coffee() {
    return new TypeSafeMatcher<String>() {
      @Override
      public boolean matchesSafely(String path) {
        return DetectsCoffeeTest.this.subject.detect(path);
      }

      @Override
      public void describeTo(Description desc) {
      }
    };
  }
}
