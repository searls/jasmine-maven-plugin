package com.github.searls.jasmine.exception;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class StringifiesStackTracesTest {

  private StringifiesStackTraces subject = new StringifiesStackTraces();

  @Test
  public void stringifiesStackTrace() {
    Exception deepest = new Exception("Sad");
    Exception deeper = new Exception(deepest);
    Exception deep = new Exception(deeper);

    String result = subject.stringify(deep);

    Assertions.assertThat(result).contains(deepest.getMessage());
  }

}
