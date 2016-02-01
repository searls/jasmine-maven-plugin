package com.github.searls.jasmine.io;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class IOUtilsWrapper {

  public String toString(InputStream inputStream) throws IOException {
    return IOUtils.toString(inputStream);
  }

  public String toString(String name) throws IOException {
    return this.toString(this.getClass().getResourceAsStream(name));
  }

}
