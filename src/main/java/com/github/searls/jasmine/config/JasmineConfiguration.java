package com.github.searls.jasmine.config;

import com.github.searls.jasmine.model.FileSystemReporter;
import com.github.searls.jasmine.model.Reporter;
import com.github.searls.jasmine.model.ScriptSearch;
import com.github.searls.jasmine.mojo.Context;
import com.github.searls.jasmine.runner.ReporterType;
import com.github.searls.jasmine.runner.SpecRunnerTemplate;
import com.google.common.base.Optional;
import org.immutables.value.Value;

import java.io.File;
import java.util.List;

@Value.Immutable
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

  SpecRunnerTemplate getSpecRunnerTemplate();

  Optional<File> getCustomRunnerTemplate();

  Optional<File> getCustomRunnerConfiguration();

  String getSpecRunnerHtmlFileName();

  ReporterType getReporterType();

  List<Reporter> getReporters();

  List<FileSystemReporter> getFileSystemReporters();

  int getAutoRefreshInterval();

  ClassLoader getProjectClassLoader();
}
