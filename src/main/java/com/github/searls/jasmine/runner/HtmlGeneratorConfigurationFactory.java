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
