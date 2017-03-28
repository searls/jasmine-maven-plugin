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
package com.github.searls.jasmine.runner;

import com.github.searls.jasmine.config.JasmineConfiguration;
import com.github.searls.jasmine.io.IOUtilsWrapper;
import com.github.searls.jasmine.io.scripts.ScriptResolver;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class HtmlGeneratorConfiguration {
  private final String sourceEncoding;
  private final ReporterType reporterType;
  private final File customRunnerTemplate;
  private final File customRunnerConfiguration;
  private final IOUtilsWrapper ioUtilsWrapper;
  private final SpecRunnerTemplate specRunnerTemplate;
  private final ScriptResolver scriptResolver;
  private final String srcDirectoryName;
  private final String specDirectoryName;
  private final int autoRefreshInterval;
  private final boolean autoRefresh;

  public HtmlGeneratorConfiguration(ReporterType reporterType, JasmineConfiguration configuration, ScriptResolver scriptResolver) throws IOException {
    this(new IOUtilsWrapper(), reporterType, configuration, scriptResolver);
  }

  public HtmlGeneratorConfiguration(IOUtilsWrapper ioUtilsWrapper, ReporterType reporterType, JasmineConfiguration configuration, ScriptResolver scriptResolver) throws IOException {
    this.ioUtilsWrapper = ioUtilsWrapper;
    this.sourceEncoding = configuration.getSourceEncoding();
    this.reporterType = reporterType;
    this.customRunnerTemplate = configuration.getCustomRunnerTemplate();
    this.specRunnerTemplate = configuration.getSpecRunnerTemplate();
    this.scriptResolver = scriptResolver;
    this.customRunnerConfiguration = configuration.getCustomRunnerConfiguration();
    this.srcDirectoryName = configuration.getSrcDirectoryName();
    this.specDirectoryName = configuration.getSpecDirectoryName();
    this.autoRefreshInterval = configuration.getAutoRefreshInterval();
    this.autoRefresh = this.autoRefreshInterval > 0 && ReporterType.HtmlReporter.equals(reporterType);
  }

  public String getSourceEncoding() {
    return this.sourceEncoding;
  }

  public ReporterType getReporterType() {
    return this.reporterType;
  }

  public File getCustomRunnerTemplate() {
    return this.customRunnerTemplate;
  }

  public String IOtoString(String defaultHtmlTemplatePath) throws IOException {
    return this.ioUtilsWrapper.toString(defaultHtmlTemplatePath);
  }

  public String getRunnerTemplate() throws IOException {
    if (this.getCustomRunnerTemplate() != null) {
      return FileUtils.readFileToString(this.getCustomRunnerTemplate());
    } else {
      SpecRunnerTemplate template = this.getSpecRunnerTemplate();
      if (template == null) {
        template = SpecRunnerTemplate.DEFAULT;
      }
      return this.IOtoString(template.getTemplate());
    }
  }

  public SpecRunnerTemplate getSpecRunnerTemplate() {
    return this.specRunnerTemplate;
  }

  public ScriptResolver getScriptResolver() {
    return this.scriptResolver;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || this.getClass() != o.getClass()) return false;

    HtmlGeneratorConfiguration that = (HtmlGeneratorConfiguration) o;

    if (this.customRunnerTemplate != null ? !this.customRunnerTemplate.equals(that.customRunnerTemplate) : that.customRunnerTemplate != null)
      return false;
    if (this.reporterType != that.reporterType) return false;
    if (this.sourceEncoding != null ? !this.sourceEncoding.equals(that.sourceEncoding) : that.sourceEncoding != null)
      return false;
    if (this.specRunnerTemplate != null ? !this.specRunnerTemplate.equals(that.specRunnerTemplate) : that.specRunnerTemplate != null)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = this.sourceEncoding != null ? this.sourceEncoding.hashCode() : 0;
    result = 31 * result + (this.reporterType != null ? this.reporterType.hashCode() : 0);
    result = 31 * result + (this.customRunnerTemplate != null ? this.customRunnerTemplate.hashCode() : 0);
    result = 31 * result + (this.specRunnerTemplate != null ? this.specRunnerTemplate.hashCode() : 0);
    return result;
  }

  public String getCustomRunnerConfiguration() throws IOException {
    return this.customRunnerConfiguration == null ? null : FileUtils.readFileToString(this.customRunnerConfiguration);
  }

  public String getSrcDirectoryName() {
    return this.srcDirectoryName;
  }

  public String getSpecDirectoryName() {
    return this.specDirectoryName;
  }

  public int getAutoRefreshInterval() {
    return this.autoRefreshInterval;
  }

  public boolean getAutoRefresh() {
    return this.autoRefresh;
  }
}


