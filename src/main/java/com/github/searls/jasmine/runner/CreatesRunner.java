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
import com.github.searls.jasmine.io.scripts.BasicScriptResolver;
import com.github.searls.jasmine.io.scripts.ContextPathScriptResolver;
import com.github.searls.jasmine.io.scripts.FindsScriptLocationsInDirectory;
import com.github.searls.jasmine.io.scripts.ResolvesLocationOfPreloadSources;
import com.github.searls.jasmine.io.scripts.ScriptResolver;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.IOException;

@Named
public class CreatesRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(CreatesRunner.class);

  private final SpecRunnerHtmlGenerator specRunnerHtmlGenerator;
  private final HtmlGeneratorConfigurationFactory htmlGeneratorConfigurationFactory;
  private final FindsScriptLocationsInDirectory findsScriptLocation;
  private final ResolvesLocationOfPreloadSources resolvesPreloadSources;
  private final IoUtilities ioUtilities;

  @Inject
  public CreatesRunner(SpecRunnerHtmlGenerator specRunnerHtmlGenerator,
                       HtmlGeneratorConfigurationFactory htmlGeneratorConfigurationFactory,
                       FindsScriptLocationsInDirectory findsScriptLocation,
                       ResolvesLocationOfPreloadSources resolvesPreloadSources,
                       IoUtilities ioUtilities) {
    this.specRunnerHtmlGenerator = specRunnerHtmlGenerator;
    this.htmlGeneratorConfigurationFactory = htmlGeneratorConfigurationFactory;
    this.findsScriptLocation = findsScriptLocation;
    this.resolvesPreloadSources = resolvesPreloadSources;
    this.ioUtilities = ioUtilities;
  }

  public void create(JasmineConfiguration config) throws IOException {
    ScriptResolver resolver = createScriptResolver(config);

    String newRunnerHtml = specRunnerHtmlGenerator.generate(
      htmlGeneratorConfigurationFactory.create(config, resolver)
    );

    File runnerDestination = ioUtilities.createFile(config.getJasmineTargetDir(), config.getSpecRunnerHtmlFileName());
    if (this.newRunnerDiffersFromOldRunner(runnerDestination, newRunnerHtml)) {
      this.saveRunner(runnerDestination, newRunnerHtml, config.getSourceEncoding());
    } else {
      LOGGER.info("Skipping spec runner generation, because an identical spec runner already exists.");
    }
  }

  private ScriptResolver createScriptResolver(JasmineConfiguration config) {
    ScriptResolver resolver = new BasicScriptResolver(
      this.resolvesPreloadSources,
      this.findsScriptLocation,
      config.getBasedir(),
      config.getSources(),
      config.getSpecs(),
      config.getPreloadSources()
    );
    return new ContextPathScriptResolver(
      resolver,
      config.getSrcDirectoryName(),
      config.getSpecDirectoryName()
    );
  }

  private String existingRunner(File destination) throws IOException {
    String existingRunner = null;
    try {
      if (destination.exists()) {
        existingRunner = ioUtilities.readFileToString(destination);
      }
    } catch (IOException e) {
      LOGGER.warn("An error occurred while trying to open an existing manual spec runner. Continuing.");
    }
    return existingRunner;
  }

  private boolean newRunnerDiffersFromOldRunner(File runnerDestination, String newRunner) throws IOException {
    return !StringUtils.equals(newRunner, this.existingRunner(runnerDestination));
  }

  private void saveRunner(File runnerDestination, String newRunner, String encoding) throws IOException {
    ioUtilities.writeStringToFile(runnerDestination, newRunner, encoding);
  }
}
