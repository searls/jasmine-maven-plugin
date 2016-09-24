package com.github.searls.jasmine.runner;

import com.github.searls.jasmine.config.JasmineConfiguration;
import com.github.searls.jasmine.io.IoUtilities;
import com.github.searls.jasmine.io.scripts.ScriptResolver;
import com.google.common.base.Optional;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.IOException;

@Named
public class HtmlGeneratorConfigurationFactory {

  private final IoUtilities ioUtilities;

  @Inject
  public HtmlGeneratorConfigurationFactory(IoUtilities ioUtilities) {
    this.ioUtilities = ioUtilities;
  }

  public HtmlGeneratorConfiguration create(JasmineConfiguration configuration,
                                           ScriptResolver resolver) throws IOException{

    return ImmutableHtmlGeneratorConfiguration.builder()
      .sourceEncoding(configuration.getSourceEncoding())
      .autoRefreshInterval(configuration.getAutoRefreshInterval())
      .runnerTemplate(getRunnerTemplate(configuration))
      .customRunnerConfiguration(getCustomRunnerConfiguration(configuration))
      .specRunnerTemplate((configuration.getSpecRunnerTemplate()))
      .reporterType(configuration.getReporterType())
      .scriptResolver(resolver)
      .build();
  }

  private String getRunnerTemplate(JasmineConfiguration configuration) throws IOException {
    String template;
    if (configuration.getCustomRunnerTemplate().isPresent()) {
      template = readCustomRunnerTemplate(configuration);
    } else {
      template = readSpecRunnerTemplate(configuration);
    }
    return template;
  }

  private String getCustomRunnerConfiguration(JasmineConfiguration configuration) throws IOException {
    Optional<File> configFile = configuration.getCustomRunnerConfiguration();
    return configFile.isPresent() ? ioUtilities.readFileToString(configFile.get()) : "";
  }

  private String readCustomRunnerTemplate(JasmineConfiguration configuration) throws IOException {
    return ioUtilities.readFileToString(configuration.getCustomRunnerTemplate().get());
  }

  private String readSpecRunnerTemplate(JasmineConfiguration configuration) throws IOException {
    return ioUtilities.resourceToString(configuration.getSpecRunnerTemplate().getTemplate());
  }
}
