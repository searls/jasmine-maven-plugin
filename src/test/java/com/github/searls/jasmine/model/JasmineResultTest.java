package com.github.searls.jasmine.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JasmineResultTest {

  private JasmineResult subject = new JasmineResult();
  ;

  @Test
  public void shouldParseDescriptionWhenSuccessful() {
    subject.setDetails(
      "Some results\n" +
        "More results\n" +
        "1 spec, 0 failures");

    boolean success = subject.didPass();

    assertThat(success).isTrue();
  }

  @Test
  public void shouldFailWhenFail() {
    subject.setDetails(
      "Describe Kaka wants 0 failures \n" +
        "it is Swedish for cookie\n" +
        "Results: 2 specs, 1 failure");

    boolean success = subject.didPass();

    assertThat(success).isFalse();
  }


  @Test
  public void shouldFailWhenMultipleOfTenFails() {
    subject.setDetails("Results: 2 specs, 10 failures");

    boolean success = subject.didPass();

    assertThat(success).isFalse();
  }

}
