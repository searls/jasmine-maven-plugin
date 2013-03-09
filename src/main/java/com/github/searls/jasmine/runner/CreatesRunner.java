package com.github.searls.jasmine.runner;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;

import com.github.searls.jasmine.config.JasmineConfiguration;
import com.github.searls.jasmine.io.scripts.BasicScriptResolver;
import com.github.searls.jasmine.io.scripts.ContextPathScriptResolver;
import com.github.searls.jasmine.io.scripts.ScriptResolver;

public class CreatesRunner {

  private final JasmineConfiguration config;

  private Log log;
  private final String runnerFileName;
  private final ReporterType reporterType;

  public CreatesRunner(JasmineConfiguration config, String runnerFileName, ReporterType reporterType) {
    this.config = config;
    this.runnerFileName = runnerFileName;
    this.reporterType = reporterType;
    this.log = config.getLog();
  }

  public void create() throws IOException {
    File runnerDestination = new File(this.config.getJasmineTargetDir(),this.runnerFileName);
    ScriptResolver resolver = new BasicScriptResolver(
        config.getSources(),
        config.getSpecs(),
        config.getPreloadSources());
    resolver = new ContextPathScriptResolver(
        resolver,
        config.getSrcDirectoryName(),
        config.getSpecDirectoryName());

    SpecRunnerHtmlGenerator generator = new SpecRunnerHtmlGeneratorFactory().create(this.reporterType, this.config, resolver);

    String newRunnerHtml = generator.generate();
    if(this.newRunnerDiffersFromOldRunner(runnerDestination, newRunnerHtml)) {
      this.saveRunner(runnerDestination, newRunnerHtml);
    } else {
      this.log.info("Skipping spec runner generation, because an identical spec runner already exists.");
    }
  }

  private String existingRunner(File destination) throws IOException {
    String existingRunner = null;
    try {
      if(destination.exists()) {
        existingRunner = FileUtils.readFileToString(destination);
      }
    } catch(Exception e) {
      this.log.warn("An error occurred while trying to open an existing manual spec runner. Continuing.");
    }
    return existingRunner;
  }

  private boolean newRunnerDiffersFromOldRunner(File runnerDestination, String newRunner) throws IOException {
    return !StringUtils.equals(newRunner, this.existingRunner(runnerDestination));
  }

  private void saveRunner(File runnerDestination, String newRunner) throws IOException {
    FileUtils.writeStringToFile(runnerDestination, newRunner, this.config.getSourceEncoding());
  }

  public void setLog(Log log) {
    this.log = log;
  }
}
