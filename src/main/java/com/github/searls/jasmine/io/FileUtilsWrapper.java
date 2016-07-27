package com.github.searls.jasmine.io;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class FileUtilsWrapper {
  public String readFileToString(final File file) throws IOException {
    return FileUtils.readFileToString(file, "UTF-8");
  }

  public void writeStringToFile(final File file, final String contents) throws IOException {
    FileUtils.writeStringToFile(file, contents, "UTF-8");
  }
}
