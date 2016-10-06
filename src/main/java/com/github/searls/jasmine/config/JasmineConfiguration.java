package com.github.searls.jasmine.config;

import com.github.searls.jasmine.model.FileSystemReporter;
import com.github.searls.jasmine.model.Reporter;
import com.github.searls.jasmine.model.ScriptSearch;
import com.github.searls.jasmine.mojo.Context;
import com.github.searls.jasmine.runner.SpecRunnerTemplate;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.util.List;

public interface JasmineConfiguration {

  File getBasedir();

  File getJasmineTargetDir();

  String getSrcDirectoryName();

  String getSpecDirectoryName();

  ScriptSearch getSources();

  ScriptSearch getSpecs();

  List<Context> getContexts();

  List<String> getPreloadSources();

  String getSourceEncoding();

  Log getLog();

  SpecRunnerTemplate getSpecRunnerTemplate();

  File getCustomRunnerTemplate();

  File getCustomRunnerConfiguration();

  List<Reporter> getReporters();

  List<FileSystemReporter> getFileSystemReporters();

  int getAutoRefreshInterval();

  ClassLoader getProjectClassLoader();
}
