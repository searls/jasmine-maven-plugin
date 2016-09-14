package com.github.searls.jasmine.io.scripts;

import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class ConvertsFileToUriStringIntegrationTest {

  private final ConvertsFileToUriString subject = new ConvertsFileToUriString();

  @Test
  public void presentsUrlRepresentationOfFile() throws IOException {
    String expected = "pants";
    File file = File.createTempFile("blerg", expected);

    String result = this.subject.convert(file);

    assertThat(result)
      .startsWith("file:")
      .endsWith(expected);
  }

  @Test
  @Ignore("Can't mock URI (final) and can't think of a File instance whose URI would throw malformed URL. Untestable??")
  public void wrapsMalformedUrlExceptionIntoRuntime() {
    try {
      this.subject.convert(new File("C:\\Program Files\\Acme\\parsers\\acme_full.dtd"));
      fail("RuntimeException expected for invalid URI=L");
    } catch (RuntimeException e) {
      assertThat(e).hasCauseInstanceOf(MalformedURLException.class);
    }
  }
}
