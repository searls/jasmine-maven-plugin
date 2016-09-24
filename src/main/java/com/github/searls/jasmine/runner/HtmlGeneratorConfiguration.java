package com.github.searls.jasmine.runner;

import com.github.searls.jasmine.io.scripts.ScriptResolver;
import org.immutables.value.Value;

@Value.Immutable
public abstract class HtmlGeneratorConfiguration {
  public abstract String getSourceEncoding();

  public abstract ReporterType getReporterType();

  public abstract String getRunnerTemplate();

  public abstract SpecRunnerTemplate getSpecRunnerTemplate();

  public abstract ScriptResolver getScriptResolver();

  public abstract String getCustomRunnerConfiguration();

  public abstract int getAutoRefreshInterval();

  public boolean isAutoRefresh() {
    return getAutoRefreshInterval() > 0 && ReporterType.HtmlReporter.equals(getReporterType());
  }
}
