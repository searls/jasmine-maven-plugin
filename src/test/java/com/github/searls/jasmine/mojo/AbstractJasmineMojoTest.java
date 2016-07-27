package com.github.searls.jasmine.mojo;

import com.github.searls.jasmine.exception.StringifiesStackTraces;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.resource.loader.FileResourceCreationException;
import org.codehaus.plexus.resource.loader.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static com.github.searls.jasmine.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AbstractJasmineMojoTest {

  private static final String ENCODING = "UTF-8";

  @InjectMocks
  @Spy
  private final AbstractJasmineMojo subject = new AbstractJasmineMojo() {
    @Override
    public void run() throws Exception {
    }
  };

  @Mock
  private final StringifiesStackTraces stringifiesStackTraces = new StringifiesStackTraces();

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  private File baseDir;

  @Mock
  private MavenProject mavenProject;

  @Mock
  private File projectFile;

  @Mock
  private File parentProjectFile;

  @Mock
  private ResourceRetriever resourceRetriever;

  @Mock
  private ReporterRetriever reporterRetriever;

  @Before
  public void before() {
    this.subject.sourceEncoding = ENCODING;
    this.subject.resourceRetriever = resourceRetriever;
    this.subject.reporterRetriever = reporterRetriever;
  }

  @Test
  public void executeStringifiesStackTraces() throws Exception {
    String expected = "panda";
    this.expectedException.expectMessage(expected);
    Exception e = new Exception();
    when(this.stringifiesStackTraces.stringify(e)).thenReturn(expected);
    doThrow(e).when(this.subject).run();

    this.subject.execute();

    verify(this.stringifiesStackTraces).stringify(e);
  }

  @Test
  public void rethrowsMojoFailureExceptions() throws Exception {
    String expected = "panda";
    this.expectedException.expect(MojoFailureException.class);
    this.expectedException.expectMessage(expected);
    MojoFailureException e = new MojoFailureException(expected);
    doThrow(e).when(this.subject).run();

    this.subject.execute();
  }

  @Test
  public void setsSourceIncludes() throws Exception {
    this.subject.execute();

    assertThat(this.subject.sources.getIncludes(), hasItem("**" + File.separator + "*.js"));
  }

  @Test
  public void setsSourceExcludes() throws Exception {
    this.subject.execute();

    assertThat(this.subject.sources.getExcludes(), is(empty()));
  }

  @Test
  public void setsSpecIncludes() throws Exception {
    this.subject.execute();

    assertThat(this.subject.specs.getIncludes(), hasItem("**" + File.separator + "*.js"));
  }

  @Test
  public void setsSpecExcludes() throws Exception {
    this.subject.execute();

    assertThat(this.subject.specs.getExcludes(), is(empty()));
  }

  @Test
  public void testGetSourceEncoding() {
    assertThat(this.subject.getSourceEncoding(), is(ENCODING));
  }

  @Test
  public void testGetCustomRunnerConfiguration() throws ResourceNotFoundException, MojoExecutionException, MojoFailureException, FileResourceCreationException {
    File configFile = mock(File.class);
    String config = "/my/fancy/pants/config";
    subject.customRunnerConfiguration = config;
    when(resourceRetriever.getResourceAsFile("customRunnerConfiguration", config, mavenProject)).thenReturn(configFile);

    subject.execute();

    assertThat(subject.getCustomRunnerConfiguration(), is(configFile));
  }

  @Test
  public void testGetCustomRunnerTemplate() throws ResourceNotFoundException, MojoExecutionException, MojoFailureException, FileResourceCreationException {
    File templateFile = mock(File.class);
    String template = "/my/super/sweet/template";
    subject.customRunnerTemplate = template;
    when(resourceRetriever.getResourceAsFile("customRunnerTemplate", template, mavenProject)).thenReturn(templateFile);

    subject.execute();

    assertThat(subject.getCustomRunnerTemplate(), is(templateFile));
  }

  @Test
  public void testGetReporters() throws ResourceNotFoundException, MojoExecutionException, MojoFailureException, FileResourceCreationException {
    File reporterFile = mock(File.class);
    List<File> reporterFiles = Collections.singletonList(reporterFile);
    String reporter = "/my/super/custom/reporter";
    List<String> reporters = Collections.singletonList(reporter);
    subject.reporters = reporters;
    when(reporterRetriever.retrieveReporters(eq(reporters), eq(mavenProject))).thenReturn(reporterFiles);

    subject.execute();

    assertThat(subject.getReporters(), is(reporterFiles));
  }

  @Test
  public void testGetBaseDir() {
    when(this.mavenProject.getBasedir()).thenReturn(this.baseDir);
    assertThat(this.subject.getBasedir(), is(this.baseDir));
  }

  @Test
  public void testGetMavenProject() {
    assertThat(this.subject.getMavenProject(), is(this.mavenProject));
  }

  @Test
  public void testGetAutoRefreshInterval() {
    this.subject.autoRefreshInterval = 5;
    assertThat(this.subject.getAutoRefreshInterval(), is(5));
  }
}
