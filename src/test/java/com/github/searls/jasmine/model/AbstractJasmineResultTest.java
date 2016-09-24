package com.github.searls.jasmine.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractJasmineResultTest {

  @Test
  public void shouldParseDescriptionWhenSuccessful() {
    AbstractJasmineResult result = ImmutableJasmineResult.builder()
      .details("Some results\nMore results\n1 spec, 0 failures")
      .build();

    assertThat(result.didPass()).isTrue();
  }

  @Test
  public void shouldFailWhenFail() {
    AbstractJasmineResult result = ImmutableJasmineResult.builder()
      .details("Describe Kaka wants 0 failures \nit is Swedish for cookie\nResults: 2 specs, 1 failure")
      .build();

    assertThat(result.didPass()).isFalse();
  }


  @Test
  public void shouldFailWhenMultipleOfTenFails() {
    AbstractJasmineResult result = ImmutableJasmineResult.builder()
      .details("Results: 2 specs, 10 failures")
      .build();

    assertThat(result.didPass()).isFalse();
  }

}
