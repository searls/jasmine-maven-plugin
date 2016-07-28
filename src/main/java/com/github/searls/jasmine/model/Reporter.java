package com.github.searls.jasmine.model;

import java.io.File;

public class Reporter {
  public String reporterName;
  public File reporterFile;

  public Reporter() {}

  public Reporter(String reporterName) {
    this.reporterName = reporterName;
  }

  public Reporter(File reporterFile) {
    this.reporterFile = reporterFile;
  }
}
