package com.github.searls.jasmine.model;

import java.io.File;

public class FileSystemReporter extends Reporter {

  private String fileName;
  private File file;

  @SuppressWarnings("unused")
  public FileSystemReporter() {
    // no-args constructor required by maven
  }

  public FileSystemReporter(String fileName, String reporter) {
    super(reporter);
    this.fileName = fileName;
    this.file = null;
  }

  public FileSystemReporter(File file, File reporterFile) {
    super(reporterFile);
    this.file = file;
    this.fileName = null;
  }

  public String getFileName() {
    return fileName;
  }

  public File getFile() {
    return file;
  }
}
