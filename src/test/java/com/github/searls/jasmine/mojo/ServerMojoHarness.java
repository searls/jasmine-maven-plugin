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
package com.github.searls.jasmine.mojo;

import com.github.searls.jasmine.format.FormatsScriptTags;
import com.github.searls.jasmine.io.IoUtilities;
import com.github.searls.jasmine.io.RelativizesFilePaths;
import com.github.searls.jasmine.io.ScansDirectory;
import com.github.searls.jasmine.io.scripts.ConvertsFileToUriString;
import com.github.searls.jasmine.io.scripts.FindsScriptLocationsInDirectory;
import com.github.searls.jasmine.io.scripts.ResolvesLocationOfPreloadSources;
import com.github.searls.jasmine.model.Reporters;
import com.github.searls.jasmine.runner.CreatesRunner;
import com.github.searls.jasmine.runner.DefaultSpecRunnerHtmlGenerator;
import com.github.searls.jasmine.runner.HtmlGeneratorConfigurationFactory;
import com.github.searls.jasmine.server.ResourceHandlerConfigurator;
import com.github.searls.jasmine.server.ServerManagerFactory;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ServerMojoHarness {

  @Mock
  private MavenProject mavenProject;

  @Mock
  private ResourceRetriever resourceRetriever;

  @Mock
  private ReporterRetriever reporterRetriever;

  @Test
  @Disabled
  public void testServer() throws MojoFailureException, MojoExecutionException {
    String baseDir = "/Volumes/sandbox/jasmine-maven-plugin/target/test-classes/examples/jasmine-webapp-passing";

    File jasmineTargetDir = new File(baseDir + "/target/jasmine");

    File sourceDirectory = new File(baseDir + "/src/main/javascript");
    File specDirectory = new File(baseDir + "/src/test/javascript");

    IoUtilities ioUtilities = new IoUtilities();
    RelativizesFilePaths relativizesFilePaths = new RelativizesFilePaths();
    ConvertsFileToUriString convertsFileToUriString = new ConvertsFileToUriString();
    CreatesRunner createsRunner = new CreatesRunner(
      new DefaultSpecRunnerHtmlGenerator(new FormatsScriptTags()),
      new HtmlGeneratorConfigurationFactory(ioUtilities),
      new FindsScriptLocationsInDirectory(
        new ScansDirectory(),
        convertsFileToUriString
      ),
      new ResolvesLocationOfPreloadSources(convertsFileToUriString),
      ioUtilities
    );

    when(mavenProject.getBasedir()).thenReturn(new File(baseDir));
    when(resourceRetriever.getResourceAsFile(anyString(), anyString(), eq(mavenProject))).thenReturn(Optional.empty());

    when(reporterRetriever.retrieveReporters(anyList(), eq(mavenProject)))
      .thenReturn(Collections.singletonList(Reporters.STANDARD_REPORTER));

    when(reporterRetriever.retrieveFileSystemReporters(anyList(), eq(jasmineTargetDir), eq(mavenProject)))
      .thenReturn(Collections.singletonList(Reporters.JUNIT_REPORTER));

    ServerMojo mojo = new ServerMojo(
      mavenProject,
      resourceRetriever,
      reporterRetriever,
      relativizesFilePaths,
      new ResourceHandlerConfigurator(relativizesFilePaths, createsRunner),
      new ServerManagerFactory()
    );

    mojo.setJsSrcDir(sourceDirectory);
    mojo.setJsTestSrcDir(specDirectory);
    mojo.setJasmineTargetDir(jasmineTargetDir);

    mojo.execute();
  }
}
