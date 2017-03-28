/*-
 * #%L
 * jasmine-maven-plugin
 * %%
 * Copyright (C) 2010 - 2017 Justin Searls
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.github.searls.jasmine.mojo;

import com.github.klieber.phantomjs.locate.PhantomJsLocatorOptions;

import java.io.File;

public class PhantomJsOptions implements PhantomJsLocatorOptions {

  private static final String DEFAULT_PHANTOMJS_VERSION = "2.0.0";
  private static final String DEFAULT_OUTPUT_DIRECTORY = "target/phantomjs";

  private Source source = Source.REPOSITORY;

  private String version;
  private boolean checkSystemPath;
  private String enforceVersion;

  private String baseUrl;
  private File outputDirectory;

  public PhantomJsOptions() {
    this.source = Source.REPOSITORY;
    this.version = DEFAULT_PHANTOMJS_VERSION;
    this.checkSystemPath = true;
    this.enforceVersion = Boolean.TRUE.toString();
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

  public String getEnforceVersion() {
    return enforceVersion;
  }

  public void setEnforceVersion(String enforceVersion) {
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
