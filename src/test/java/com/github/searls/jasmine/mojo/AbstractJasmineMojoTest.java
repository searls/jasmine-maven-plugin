package com.github.searls.jasmine.mojo;

import static com.github.searls.jasmine.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.InputStream;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.resource.ResourceManager;
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

import com.github.searls.jasmine.exception.StringifiesStackTraces;

@RunWith(MockitoJUnitRunner.class)
public class AbstractJasmineMojoTest {

  private static final String ENCODING = "UTF-8";
  private static final String SCRIPT_LOADER_PATH = "scriptloaderpath";
  private static final String PARENT_PROJECT_PATH = "/parent/project/path";

  @InjectMocks @Spy
  private final AbstractJasmineMojo subject = new AbstractJasmineMojo() {
    @Override
    public void run() throws Exception {}
  };

  @Mock private final StringifiesStackTraces stringifiesStackTraces = new StringifiesStackTraces();

  @Rule public ExpectedException expectedException = ExpectedException.none();

  private static final String CUSTOM_RUNNER_CONFIG = "customRunnerConfiguration";

  private static final String CUSTOM_RUNNER_TEMPLATE = "customRunnerTemplate";

  @Mock
  private File baseDir;

  @Mock
  private MavenProject mavenProject;

  @Mock
  private File projectFile;

  @Mock
  private File parentProjectFile;

  @Mock
  private ResourceManager locator;

  @Before
  public void before() {
    subject.sourceEncoding = ENCODING;
    subject.scriptLoaderPath = null;
    subject.locator = locator;
  }

  @Test
  public void executeStringifiesStackTraces() throws Exception {
    String expected = "panda";
    expectedException.expectMessage(expected);
    Exception e = new Exception();
    when(stringifiesStackTraces.stringify(e)).thenReturn(expected);
    doThrow(e).when(subject).run();

    subject.execute();

    verify(stringifiesStackTraces).stringify(e);
  }

  @Test
  public void rethrowsMojoFailureExceptions() throws Exception {
    String expected = "panda";
    expectedException.expect(MojoFailureException.class);
    expectedException.expectMessage(expected);
    MojoFailureException e = new MojoFailureException(expected);
    doThrow(e).when(subject).run();

    subject.execute();
  }

  @Test
  public void setsSourceIncludes() throws Exception {
    subject.execute();

    assertThat(subject.sources.getIncludes(),hasItem("**"+File.separator+"*.js"));
  }

  @Test
  public void setsSourceExcludes() throws Exception {
    subject.execute();

    assertThat(subject.sources.getExcludes(),is(empty()));
  }

  @Test
  public void setsSpecIncludes() throws Exception {
    subject.execute();

    assertThat(subject.specs.getIncludes(),hasItem("**"+File.separator+"*.js"));
  }

  @Test
  public void setsSpecExcludes() throws Exception {
    subject.execute();

    assertThat(subject.specs.getExcludes(),is(empty()));
  }

  @Test
  public void testGetSourceEncoding() {
    assertThat(subject.getSourceEncoding(), is(ENCODING));
  }

  @Test
  public void testGetCustomRunnerConfiguration() throws ResourceNotFoundException, MojoExecutionException, MojoFailureException {
    InputStream customRunnerConfigurationStream = mock(InputStream.class);
    subject.customRunnerConfiguration = CUSTOM_RUNNER_CONFIG;
    when(mavenProject.getFile()).thenReturn(projectFile);
    when(projectFile.getParentFile()).thenReturn(parentProjectFile);
    when(parentProjectFile.getAbsolutePath()).thenReturn(PARENT_PROJECT_PATH);
    when(locator.getResourceAsInputStream(CUSTOM_RUNNER_CONFIG)).thenReturn(customRunnerConfigurationStream);
    subject.execute();
    assertThat(subject.getCustomRunnerConfiguration(), is(customRunnerConfigurationStream));
  }

  @Test
  public void testGetCustomRunnerTemplate() throws ResourceNotFoundException, MojoExecutionException, MojoFailureException {
    InputStream customRunnerTemplateStream = mock(InputStream.class);
    subject.customRunnerTemplate = CUSTOM_RUNNER_TEMPLATE;
    when(mavenProject.getFile()).thenReturn(projectFile);
    when(projectFile.getParentFile()).thenReturn(parentProjectFile);
    when(parentProjectFile.getAbsolutePath()).thenReturn(PARENT_PROJECT_PATH);
    when(locator.getResourceAsInputStream(CUSTOM_RUNNER_TEMPLATE)).thenReturn(customRunnerTemplateStream);
    subject.execute();
    assertThat(subject.getCustomRunnerTemplate(), is(customRunnerTemplateStream));
  }

  @Test(expected=MojoExecutionException.class)
  public void testGetCustomRunnerTemplateNotFound() throws ResourceNotFoundException, MojoExecutionException, MojoFailureException {
    subject.customRunnerTemplate = CUSTOM_RUNNER_TEMPLATE;
    when(mavenProject.getFile()).thenReturn(projectFile);
    when(projectFile.getParentFile()).thenReturn(parentProjectFile);
    when(parentProjectFile.getAbsolutePath()).thenReturn(PARENT_PROJECT_PATH);
    when(locator.getResourceAsInputStream(CUSTOM_RUNNER_TEMPLATE)).thenThrow(new ResourceNotFoundException(CUSTOM_RUNNER_TEMPLATE));
    subject.execute();
  }

  @Test
  public void testGetBaseDir() {
    when(mavenProject.getBasedir()).thenReturn(baseDir);
    assertThat(subject.getBasedir(),is(baseDir));
  }

  @Test
  public void testGetScriptLoaderPath() {
    subject.scriptLoaderPath = SCRIPT_LOADER_PATH;
    assertThat(subject.getScriptLoaderPath(),is(SCRIPT_LOADER_PATH));
  }

  @Test
  public void testGetMavenProject() {
    assertThat(subject.getMavenProject(), is(mavenProject));
  }

  @Test
  public void testGetAutoRefreshInterval() {
    subject.autoRefreshInterval = 5;
    assertThat(subject.getAutoRefreshInterval(), is(5));
  }
}
