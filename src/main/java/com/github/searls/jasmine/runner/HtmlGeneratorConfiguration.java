package com.github.searls.jasmine.runner;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.github.searls.jasmine.config.JasmineConfiguration;
import com.github.searls.jasmine.io.IOUtilsWrapper;
import com.github.searls.jasmine.io.scripts.ScriptResolver;

public class HtmlGeneratorConfiguration {
  private final String sourceEncoding;
  private final ReporterType reporterType;
  private final File customRunnerTemplate;
  private IOUtilsWrapper ioUtilsWrapper;
  private final SpecRunnerTemplate specRunnerTemplate;
  private final ScriptResolver scriptResolver;
  private final String scriptLoaderPath;
  private final File customRunnerConfiguration;
  private final String srcDirectoryName;
  private final String specDirectoryName;

  public HtmlGeneratorConfiguration(ReporterType reporterType, JasmineConfiguration configuration, ScriptResolver scriptResolver) throws IOException {
    this.sourceEncoding = configuration.getSourceEncoding();
    this.reporterType = reporterType;
    this.customRunnerTemplate = configuration.getCustomRunnerTemplate();
    this.specRunnerTemplate = configuration.getSpecRunnerTemplate();
    this.scriptResolver = scriptResolver;
    this.customRunnerConfiguration = configuration.getCustomRunnerConfiguration();
    this.ioUtilsWrapper  = new IOUtilsWrapper();
    this.scriptLoaderPath = configuration.getScriptLoaderPath();
    this.srcDirectoryName = configuration.getSrcDirectoryName();
    this.specDirectoryName = configuration.getSpecDirectoryName();
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

  public String readFileToString(File customRunnerTemplate) throws IOException {
    return FileUtils.readFileToString(customRunnerTemplate);
  }

  public String IOtoString(String defaultHtmlTemplatePath) throws IOException {
    return this.ioUtilsWrapper.toString(defaultHtmlTemplatePath);
  }

  public String getRunnerTemplate() throws IOException {

    if (this.getCustomRunnerTemplate() != null) {
      return this.readFileToString(this.getCustomRunnerTemplate());
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
    if(null != this.customRunnerConfiguration) {
      return FileUtils.readFileToString(this.customRunnerConfiguration);
    }  else {
      return null;
    }
  }

  public void setIoUtilsWrapper(IOUtilsWrapper ioUtilsWrapper) {
    this.ioUtilsWrapper = ioUtilsWrapper;
  }

  public String getScriptLoaderPath() {
    return this.scriptLoaderPath;
  }

  public String getSrcDirectoryName() {
    return this.srcDirectoryName;
  }

  public String getSpecDirectoryName() {
    return this.specDirectoryName;
  }
}


