package com.github.searls.jasmine.mojo;

import com.github.searls.jasmine.model.FileSystemReporter;
import com.github.searls.jasmine.model.Reporter;
import com.google.common.base.Optional;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
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

  @Before
  public void setUp() throws Exception {
    subject = new ReporterRetriever(resourceRetriever);

    when(resourceRetriever.getResourceAsFile("reporter", ReporterRetriever.STANDARD_REPORTER, mavenProject))
      .thenReturn(Optional.of(standardReporter));
    when(resourceRetriever.getResourceAsFile(
      "reporter",
      ReporterRetriever.JUNIT_XML_REPORTER,
      mavenProject
    )).thenReturn(Optional.of(junitXmlReporter));
  }

  @Test
  public void itShouldRetrieveReporters() throws Exception {
    String customReporterPath = "/foo/bar";
    File customReporter = mock(File.class);
    when(resourceRetriever.getResourceAsFile("reporter", customReporterPath, mavenProject)).thenReturn(Optional.of(customReporter));
    List<Reporter> incomingReporters = Arrays.asList(new Reporter("STANDARD"), new Reporter(customReporterPath));

    List<Reporter> reporters = subject.retrieveReporters(incomingReporters, mavenProject);

    assertThat(reporters).hasSize(incomingReporters.size());
    assertThat(reporters.get(0).getReporterFile()).isEqualTo(standardReporter);
    assertThat(reporters.get(1).getReporterFile()).isEqualTo(customReporter);
  }

  @Test
  public void itShouldRetrieveStandardReporterAsDefault() throws Exception {
    List<Reporter> reporters = subject.retrieveReporters(new ArrayList<Reporter>(), mavenProject);

    assertThat(reporters).hasSize(1);
    assertThat(reporters.get(0).getReporterFile()).isEqualTo(standardReporter);
  }

  @Test
  public void itShouldRetrieveFileSystemReporters() throws Exception {
    String customReporterPath = "/foo/bar";
    File customReporter = mock(File.class);
    when(resourceRetriever.getResourceAsFile("reporter", customReporterPath, mavenProject))
      .thenReturn(Optional.of(customReporter));
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
    List<FileSystemReporter> reporters = subject.retrieveFileSystemReporters(
      new ArrayList<FileSystemReporter>(),
      targetDir,
      mavenProject
    );

    assertThat(reporters).hasSize(1);
    assertThat(reporters.get(0).getReporterFile()).isEqualTo(junitXmlReporter);
    assertThat(reporters.get(0).getFile().getAbsolutePath()).endsWith("TEST-jasmine.xml");
  }
}
