package com.github.searls.jasmine.io;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import javax.inject.Named;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Named
public class IoUtilities {


  public String readFileToString(final File file) throws IOException {
    return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
  }

  public void writeStringToFile(final File file, final String contents) throws IOException {
    FileUtils.writeStringToFile(file, contents, StandardCharsets.UTF_8);
  }

  public String toString(InputStream inputStream) throws IOException {
    return IOUtils.toString(inputStream);
  }

  public String resourceToString(String name) throws IOException {
    return toString(resourceToInputStream(name));
  }

  public InputStream resourceToInputStream(String name) throws IOException {
    return IoUtilities.class.getResourceAsStream(name);
  }
}
