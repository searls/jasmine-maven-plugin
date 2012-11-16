package com.github.searls.jasmine.runner;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import com.github.searls.jasmine.AbstractJasmineMojo;
import com.github.searls.jasmine.io.FileUtilsWrapper;
import com.github.searls.jasmine.io.IOUtilsWrapper;
import com.github.searls.jasmine.io.scripts.ScriptResolver;

public class HtmlGeneratorConfiguration {
  private final String sourceEncoding;
  private final ReporterType reporterType;
  private final File customRunnerTemplate;
    private FileUtilsWrapper fileUtilsWrapper;
  private IOUtilsWrapper ioUtilsWrapper;
  private SpecRunnerTemplate specRunnerTemplate;
  private ScriptResolver scriptResolver;
    private String scriptLoaderPath;
  private File customRunnerConfiguration;


  public HtmlGeneratorConfiguration(ReporterType reporterType, AbstractJasmineMojo configuration, ScriptResolver scriptResolver) throws IOException {
    scriptResolver.resolveScripts();
    this.sourceEncoding = configuration.getSourceEncoding();
    this.reporterType = reporterType;
    this.customRunnerTemplate = configuration.getCustomRunnerTemplate();
    this.specRunnerTemplate = configuration.getSpecRunnerTemplate();
    this.scriptResolver = scriptResolver;
    this.customRunnerConfiguration = configuration.getCustomRunnerConfiguration();
    this.fileUtilsWrapper = new FileUtilsWrapper();
    this.ioUtilsWrapper  = new IOUtilsWrapper();
    this.scriptLoaderPath = configuration.getScriptLoaderPath();
  }

  public String getSourceEncoding() {
    return sourceEncoding;
  }

  public ReporterType getReporterType() {
    return reporterType;
  }

  public File getCustomRunnerTemplate() {
    return customRunnerTemplate;
  }

  public String readFileToString(File customRunnerTemplate) throws IOException {
    return fileUtilsWrapper.readFileToString(customRunnerTemplate);
  }

  public String IOtoString(String defaultHtmlTemplatePath) throws IOException {
    return ioUtilsWrapper.toString(defaultHtmlTemplatePath);
  }

  public String getRunnerTemplate() throws IOException {
  	
    if (getCustomRunnerTemplate() != null) {
      return readFileToString(getCustomRunnerTemplate());
    } else {
    	SpecRunnerTemplate template = getSpecRunnerTemplate();
    	if (template == null) {
    		template = SpecRunnerTemplate.DEFAULT;
    	}
      return IOtoString(template.getTemplate());
    }
  }

  public SpecRunnerTemplate getSpecRunnerTemplate() {
    return specRunnerTemplate;
  }

  public ScriptResolver getScriptResolver() {
  	return this.scriptResolver;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    HtmlGeneratorConfiguration that = (HtmlGeneratorConfiguration) o;

    if (customRunnerTemplate != null ? !customRunnerTemplate.equals(that.customRunnerTemplate) : that.customRunnerTemplate != null)
      return false;
    if (reporterType != that.reporterType) return false;
    if (sourceEncoding != null ? !sourceEncoding.equals(that.sourceEncoding) : that.sourceEncoding != null)
      return false;
    if (specRunnerTemplate != null ? !specRunnerTemplate.equals(that.specRunnerTemplate) : that.specRunnerTemplate != null)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = sourceEncoding != null ? sourceEncoding.hashCode() : 0;
    result = 31 * result + (reporterType != null ? reporterType.hashCode() : 0);
    result = 31 * result + (customRunnerTemplate != null ? customRunnerTemplate.hashCode() : 0);
    result = 31 * result + (specRunnerTemplate != null ? specRunnerTemplate.hashCode() : 0);
    return result;
  }

  public Set<String> getSpecsRelativePath() throws IOException {
    return scriptResolver.getSpecsRelativePath  ();
  }

  public String getSourceDirectoryRelativePath() throws IOException {
    return scriptResolver.getSourceDirectoryRelativePath();
  }

  public Set<String> getPreloadsRelativePath() throws IOException {
      return scriptResolver.getPreloadsRelativePath();
  }

  public String getCustomRunnerConfiguration() throws IOException {
    if(null != customRunnerConfiguration) {
      return fileUtilsWrapper.readFileToString(customRunnerConfiguration);
    }  else {
      return null;
    }
  }

  public void setFileUtilsWrapper(FileUtilsWrapper fileUtilsWrapper) {
      this.fileUtilsWrapper = fileUtilsWrapper;
  }

  public void setIoUtilsWrapper(IOUtilsWrapper ioUtilsWrapper) {
      this.ioUtilsWrapper = ioUtilsWrapper;
  }

  public String getScriptLoaderPath() {
      return scriptLoaderPath;
  }
}


