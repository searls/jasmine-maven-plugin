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
import com.github.searls.jasmine.io.scripts.FindsScriptLocationsInDirectory;
import com.github.searls.jasmine.io.scripts.ResolvesLocationOfPreloadSources;
import com.github.searls.jasmine.io.scripts.ScriptResolver;
import com.github.searls.jasmine.model.ScriptSearch;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CreatesRunnerTest {

  private static final String SOURCE_ENCODING = "UTF-Pandaz";
  private static final String MANUAL_RUNNER_NAME = "Jerry. That's a nice name.";

  @Mock
  private ScriptSearch sources;

  @Mock
  private ScriptSearch specs;

  @Mock
  private File sourceDirectory;

  @Mock
  private File specDirectory;

  @Mock
  private File runnerDestination;

  @Mock
  private File jasmineTargetDir;

  @Mock
  private ResolvesLocationOfPreloadSources resolvesPreloadSources;

  @Mock
  private FindsScriptLocationsInDirectory findsScriptLocations;

  @Mock
  private SpecRunnerHtmlGenerator specRunnerHtmlGenerator;

  @Mock
  private HtmlGeneratorConfigurationFactory htmlGeneratorConfigurationFactory;

  @Mock
  private HtmlGeneratorConfiguration htmlGeneratorConfiguration;

  @Mock
  private JasmineConfiguration config;

  @Mock
  private IoUtilities ioUtilities;

  @InjectMocks
  private CreatesRunner subject;

  @Before
  public void before() {
    when(this.config.getSources()).thenReturn(this.sources);
    when(this.config.getSpecs()).thenReturn(this.specs);
    when(this.config.getSourceEncoding()).thenReturn(SOURCE_ENCODING);
    when(this.config.getJasmineTargetDir()).thenReturn(this.jasmineTargetDir);
    when(this.config.getSpecRunnerHtmlFileName()).thenReturn(MANUAL_RUNNER_NAME);
  }

  @Before
  public void fakeOutDirectories() {
    when(this.sources.getDirectory()).thenReturn(this.sourceDirectory);
    when(this.specs.getDirectory()).thenReturn(this.specDirectory);
  }

  @Before
  public void stubConstructionOfExistingRunnerFile() throws Exception {
    when(ioUtilities.createFile(this.jasmineTargetDir, MANUAL_RUNNER_NAME))
      .thenReturn(this.runnerDestination);
  }

  @Before
  public void stubConstructionOfHtmlGenerator() throws Exception {
    when(htmlGeneratorConfigurationFactory.create(
      any(JasmineConfiguration.class),
      any(ScriptResolver.class)
    )).thenReturn(this.htmlGeneratorConfiguration);
  }

  @Test
  public void whenRunnerDoesNotExistThenCreateNewRunner() throws Exception {
    String expected = "I'm a new spec runner yay!";
    when(this.runnerDestination.exists()).thenReturn(false);
    when(this.specRunnerHtmlGenerator.generate(htmlGeneratorConfiguration)).thenReturn(expected);

    this.subject.create(config);

    verify(ioUtilities).writeStringToFile(this.runnerDestination, expected, SOURCE_ENCODING);
  }

  @Test
  public void whenRunnerExistsAndDiffersThenWriteNewOne() throws IOException {
    String expected = "HTRML!!!!111!111oneoneone";
    when(this.runnerDestination.exists()).thenReturn(true);
    when(ioUtilities.readFileToString(this.runnerDestination)).thenReturn("old and crusty runner");
    when(this.specRunnerHtmlGenerator.generate(htmlGeneratorConfiguration)).thenReturn(expected);

    this.subject.create(config);

    verify(ioUtilities).writeStringToFile(this.runnerDestination, expected, SOURCE_ENCODING);
  }

  @Test
  public void whenRunnerExistsAndIsSameThenDoNothing() throws IOException {
    String existing = "HTRML!!!!111!111oneoneone";
    when(this.runnerDestination.exists()).thenReturn(true);
    when(ioUtilities.readFileToString(this.runnerDestination)).thenReturn(existing);
    when(this.specRunnerHtmlGenerator.generate(htmlGeneratorConfiguration)).thenReturn(existing);

    this.subject.create(config);

    this.neverWriteAFile();
  }

  @Test
  public void whenExistingRunnerFailsToLoadThenWriteNewOne() throws IOException {
    String expected = "HTRML!!!!111!111oneoneone";
    when(this.runnerDestination.exists()).thenReturn(true);
    when(ioUtilities.readFileToString(this.runnerDestination)).thenThrow(new IOException());
    when(this.specRunnerHtmlGenerator.generate(htmlGeneratorConfiguration)).thenReturn(expected);

    this.subject.create(config);

    verify(ioUtilities).writeStringToFile(this.runnerDestination, expected, SOURCE_ENCODING);
  }

  private void neverWriteAFile() throws IOException {
    verify(ioUtilities, never()).writeStringToFile(any(File.class), anyString(), anyString());
  }
}
