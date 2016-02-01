package com.github.searls.jasmine.exception;

import org.junit.Test;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

public class StringifiesStackTracesTest {

  private StringifiesStackTraces subject = new StringifiesStackTraces();

  @Test
  public void stringifiesStackTrace() {
    Exception deepest = new Exception("Sad");
    Exception deeper = new Exception(deepest);
    Exception deep = new Exception(deeper);

    String result = subject.stringify(deep);

    assertThat(result, containsString(deepest.getMessage()));
  }

}
