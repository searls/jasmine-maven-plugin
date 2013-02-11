package com.github.searls.jasmine.coffee;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

public class DetectsCoffeeTest {

  private DetectsCoffee subject = new DetectsCoffee();

  @Test
  public void whenAStringEndingInCoffeeThatsCoffee() {
    assertThat("/some/path/to/pants.coffee",is(coffee()));
  }

  @Test
  public void whenAStringDoesNotEndInCoffeeThatsNotCoffee() {
    assertThat("/some/path/to/pants.cafe",is(not(coffee())));
  }

  @Test
  public void whenCoffeeHasAQueryStringThatsCoffee() {
    assertThat("/some/path/to/pants.coffee?stillCoffee=true",is(coffee()));
  }

  @Test
  public void whenJavaScriptHasACoffeeQueryThatsNotCoffee() {
    assertThat("/some/path/to/pants.js?extension=lulz.coffee",is(not(coffee())));
  }

  private TypeSafeMatcher<String> coffee() {
    return new TypeSafeMatcher<String>() {
      public boolean matchesSafely(String path) {
        return subject.detect(path);
      }
      public void describeTo(Description desc) {}
    };
  }
}
