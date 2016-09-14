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
