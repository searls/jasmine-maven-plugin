package com.github.searls.jasmine.model;

import java.io.File;

public class Reporter {

  private String reporterName;
  private File reporterFile;

  @SuppressWarnings("unused")
  public Reporter() {
    // no-args constructor required by maven
  }

  public Reporter(String reporterName) {
    this(reporterName, null);
  }

  public Reporter(File reporterFile) {
    this(null, reporterFile);
  }

  public Reporter(String reporterName,
                  File reporterFile) {
    this.reporterName = reporterName;
    this.reporterFile = reporterFile;
  }

  public String getReporterName() {
    return reporterName;
  }

  public File getReporterFile() {
    return reporterFile;
  }
}
