package com.github.searls.jasmine.mojo;

import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReporterRetrieverTest {

  @Mock
  ResourceRetriever resourceRetriever;

  @Mock
  MavenProject mavenProject;

  @Mock
  File standardReporter;

  ReporterRetriever subject;

  @Before
  public void setUp() throws Exception {
    subject = new ReporterRetriever(resourceRetriever);
    when(resourceRetriever.getResourceAsFile("reporter", ReporterRetriever.STANDARD_REPORTER, mavenProject)).thenReturn(standardReporter);
  }

  @Test
  public void itShouldRetrieveReporters() throws Exception {
    String customReporterPath = "/foo/bar";
    File customReporter = mock(File.class);
    when(resourceRetriever.getResourceAsFile("reporter", customReporterPath, mavenProject)).thenReturn(customReporter);
    List<File> reporters = subject.retrieveReporters(Arrays.asList("STANDARD", customReporterPath), mavenProject);

    assertThat(reporters, is(Arrays.asList(standardReporter, customReporter)));
  }

  @Test
  public void itShouldRetrieveStandardReporterAsDefault() throws Exception {
    List<File> reporters = subject.retrieveReporters(Collections.<String>emptyList(), mavenProject);

    assertThat(reporters, is(Collections.singletonList(standardReporter)));
  }
}
