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

import com.github.searls.jasmine.model.FileSystemReporter;
import com.github.searls.jasmine.model.Reporter;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReporterRetrieverTest {

  @Mock
  private ResourceRetriever resourceRetriever;

  @Mock
  private MavenProject mavenProject;

  private File targetDir = new File(".");

  @Mock
  private File standardReporter;

  @Mock
  private File junitXmlReporter;

  private ReporterRetriever subject;

  @BeforeEach
  public void beforeEach() {
    subject = new ReporterRetriever(resourceRetriever);
  }

  @Test
  public void itShouldRetrieveReporters() throws Exception {
    String customReporterPath = "/foo/bar";
    File customReporter = mock(File.class);
    when(resourceRetriever.getResourceAsFile("reporter", customReporterPath, mavenProject))
      .thenReturn(Optional.of(customReporter));
    when(resourceRetriever.getResourceAsFile("reporter", ReporterRetriever.STANDARD_REPORTER, mavenProject))
      .thenReturn(Optional.of(standardReporter));
    List<Reporter> incomingReporters = Arrays.asList(new Reporter("STANDARD"), new Reporter(customReporterPath));

    List<Reporter> reporters = subject.retrieveReporters(incomingReporters, mavenProject);

    assertThat(reporters).hasSize(incomingReporters.size());
    assertThat(reporters.get(0).getReporterFile()).isEqualTo(standardReporter);
    assertThat(reporters.get(1).getReporterFile()).isEqualTo(customReporter);
  }

  @Test
  public void itShouldRetrieveStandardReporterAsDefault() throws Exception {
    when(resourceRetriever.getResourceAsFile("reporter", ReporterRetriever.STANDARD_REPORTER, mavenProject))
      .thenReturn(Optional.of(standardReporter));

    List<Reporter> reporters = subject.retrieveReporters(new ArrayList<>(), mavenProject);

    assertThat(reporters).hasSize(1);
    assertThat(reporters.get(0).getReporterFile()).isEqualTo(standardReporter);
  }

  @Test
  public void itShouldRetrieveFileSystemReporters() throws Exception {
    String customReporterPath = "/foo/bar";
    File customReporter = mock(File.class);
    when(resourceRetriever.getResourceAsFile("reporter", customReporterPath, mavenProject))
      .thenReturn(Optional.of(customReporter));
    when(resourceRetriever.getResourceAsFile("reporter", ReporterRetriever.JUNIT_XML_REPORTER, mavenProject))
      .thenReturn(Optional.of(junitXmlReporter));
    List<FileSystemReporter> incomingReporters = Arrays.asList(
      new FileSystemReporter("TEST-file.xml", "JUNIT_XML"),
      new FileSystemReporter("TEST-custom.file", customReporterPath)
    );

    List<FileSystemReporter> reporters = subject.retrieveFileSystemReporters(
      incomingReporters,
      targetDir,
      mavenProject
    );

    assertThat(reporters).hasSize(incomingReporters.size());
    assertThat(reporters.get(0).getReporterFile()).isEqualTo(junitXmlReporter);
    assertThat(reporters.get(0).getFile().getAbsolutePath()).endsWith("TEST-file.xml");
    assertThat(reporters.get(1).getReporterFile()).isEqualTo(customReporter);
    assertThat(reporters.get(1).getFile().getAbsolutePath()).endsWith("TEST-custom.file");
  }

  @Test
  public void itShouldRetrieveJUnitFileReporterAsDefault() throws Exception {
    when(resourceRetriever.getResourceAsFile("reporter", ReporterRetriever.JUNIT_XML_REPORTER, mavenProject))
      .thenReturn(Optional.of(junitXmlReporter));
    List<FileSystemReporter> reporters = subject.retrieveFileSystemReporters(
      new ArrayList<>(),
      targetDir,
      mavenProject
    );

    assertThat(reporters).hasSize(1);
    assertThat(reporters.get(0).getReporterFile()).isEqualTo(junitXmlReporter);
    assertThat(reporters.get(0).getFile().getAbsolutePath()).endsWith("TEST-jasmine.xml");
  }
}
