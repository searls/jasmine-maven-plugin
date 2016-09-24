package com.github.searls.jasmine.io.scripts;

import javax.inject.Named;
import java.io.File;
import java.net.MalformedURLException;

@Named
public class ConvertsFileToUriString {

  public String convert(File file) {
    try {
      return file.toURI().toURL().toString();
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

}
