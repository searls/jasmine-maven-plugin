package com.github.searls.jasmine.model;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class JasmineResultTest {

  private JasmineResult subject = new JasmineResult();;

  @Test
  public void shouldParseDescriptionWhenSuccessful() {
    subject.setDetails(
        "Some results\n" +
        "More results\n" +
        "1 spec, 0 failures");

    boolean success = subject.didPass();

    assertThat(success, is(true));
  }

  @Test
  public void shouldFailWhenFail() {
    subject.setDetails(
        "Describe Kaka wants 0 failures \n" +
        "it is Swedish for cookie\n" +
        "Results: 2 specs, 1 failure");

    boolean success = subject.didPass();

    assertThat(success, is(false));
  }


  @Test
  public void shouldFailWhenMultipleOfTenFails() {
    subject.setDetails("Results: 2 specs, 10 failures");

    boolean success = subject.didPass();

    assertThat(success, is(false));
  }

}
