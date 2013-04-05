package com.github.searls.jasmine.config;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.apache.maven.plugin.logging.Log;

import com.github.searls.jasmine.model.ScriptSearch;
import com.github.searls.jasmine.runner.SpecRunnerTemplate;

public interface JasmineConfiguration {
  File getBasedir();
  File getJasmineTargetDir();

  String getSrcDirectoryName();
  String getSpecDirectoryName();

  ScriptSearch getSources();
  ScriptSearch getSpecs();

  List<String> getPreloadSources();

  String getSourceEncoding();

  Log getLog();

  SpecRunnerTemplate getSpecRunnerTemplate();

  InputStream getCustomRunnerTemplate();
  InputStream getCustomRunnerConfiguration();

  String getScriptLoaderPath();

  int getAutoRefreshInterval();
}
