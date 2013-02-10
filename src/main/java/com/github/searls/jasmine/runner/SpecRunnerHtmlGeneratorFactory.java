package com.github.searls.jasmine.runner;

import com.github.searls.jasmine.AbstractJasmineMojo;
import com.github.searls.jasmine.io.scripts.ScriptResolver;

import java.io.IOException;

public class SpecRunnerHtmlGeneratorFactory {

  public SpecRunnerHtmlGenerator create(ReporterType reporterType, AbstractJasmineMojo config, ScriptResolver projectDirScripResolver) {
    try {
      return createHtmlGenerator(new HtmlGeneratorConfiguration(reporterType, config, projectDirScripResolver));
    } catch (IOException e) {
      throw new InstantiationError(e.getMessage());
    }
  }

  public SpecRunnerHtmlGenerator createHtmlGenerator(HtmlGeneratorConfiguration configuration) throws IllegalArgumentException {
    return new DefaultSpecRunnerHtmlGenerator(configuration);
  }
}
