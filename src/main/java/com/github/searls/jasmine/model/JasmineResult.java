package com.github.searls.jasmine.model;


public class JasmineResult {
  private String details;

  public boolean didPass() {
    return getDetails().contains(" 0 failures");
  }

  public String getDetails() {
    return details;
  }

  public void setDetails(String details) {
    this.details = details;
  }

  private <T> T last(T[] array) {
      return array[array.length - 1];
  }

}
