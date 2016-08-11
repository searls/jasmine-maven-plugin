package com.github.searls.jasmine.model;

import java.io.File;

public class FileSystemReporter extends Reporter {
  public String fileName;
  public File file;

  public FileSystemReporter() {
  }

  public FileSystemReporter(String fileName, String reporter) {
    super(reporter);
    this.fileName = fileName;
  }

  public FileSystemReporter(File file, File reporterFile) {
    super(reporterFile);
    this.file = file;
  }
}
