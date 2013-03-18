package com.github.searls.jasmine.mojo;

import static com.github.searls.jasmine.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
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

  @InjectMocks @Spy
  private final AbstractJasmineMojo subject = new AbstractJasmineMojo() {
    @Override
    public void run() throws Exception {}
  };

  @Mock private final StringifiesStackTraces stringifiesStackTraces = new StringifiesStackTraces();

  @Rule public ExpectedException expectedException = ExpectedException.none();

  @Mock
  private File customRunnerConfiguration;

  @Mock
  private File customRunnerTemplate;

  @Mock
  private File baseDir;

  @Mock
  private MavenProject mavenProject;

  @Before
  public void before() {
    subject.sourceEncoding = ENCODING;
    subject.scriptLoaderPath = null;

    when(customRunnerConfiguration.exists()).thenReturn(true);
    when(customRunnerConfiguration.canRead()).thenReturn(true);
    when(customRunnerTemplate.exists()).thenReturn(true);
    when(customRunnerTemplate.canRead()).thenReturn(true);
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
  public void testGetCustomRunnerConfiguration() {
    assertThat(subject.getCustomRunnerConfiguration(), is(this.customRunnerConfiguration));
  }

  @Test
  public void testGetCustomRunnerTemplate() {
    assertThat(subject.getCustomRunnerTemplate(), is(this.customRunnerTemplate));
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
