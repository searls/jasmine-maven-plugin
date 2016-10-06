package com.github.searls.jasmine.model;

import org.immutables.value.Value;

@Value.Immutable
public abstract class AbstractJasmineResult implements JasmineResult {

  @Override
  public boolean didPass() {
    return getDescription().contains(" 0 failures");
  }

  private String getDescription() {
    return last(getDetails().split("\n"));
  }

  private <T> T last(T[] array) {
    return array[array.length - 1];
  }

}
