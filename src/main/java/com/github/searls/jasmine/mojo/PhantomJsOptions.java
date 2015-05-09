package com.github.searls.jasmine.mojo;

import com.github.klieber.phantomjs.locate.PhantomJsLocatorOptions;

import java.io.File;

public class PhantomJsOptions implements PhantomJsLocatorOptions {

  private static final String DEFAULT_PHANTOMJS_VERSION = "2.0.0";
  private static final String DEFAULT_OUTPUT_DIRECTORY = "target/phantomjs";

  private Source source = Source.REPOSITORY;

  private String version;
  private boolean checkSystemPath;
  private boolean enforceVersion;

  private String baseUrl;
  private File outputDirectory;

  public PhantomJsOptions() {
    this.source = Source.REPOSITORY;
    this.version = DEFAULT_PHANTOMJS_VERSION;
    this.checkSystemPath = true;
    this.enforceVersion = true;
    this.outputDirectory = new File(DEFAULT_OUTPUT_DIRECTORY);
  }

  public Source getSource() {
    return source;
  }

  public void setSource(Source source) {
    this.source = source;
  }

  public boolean isCheckSystemPath() {
    return checkSystemPath;
  }

  public void setCheckSystemPath(boolean checkSystemPath) {
    this.checkSystemPath = checkSystemPath;
  }

  public boolean isEnforceVersion() {
    return enforceVersion;
  }

  public void setEnforceVersion(boolean enforceVersion) {
    this.enforceVersion = enforceVersion;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public File getOutputDirectory() {
    return outputDirectory;
  }

  public void setOutputDirectory(File outputDirectory) {
    this.outputDirectory = outputDirectory;
  }
}
