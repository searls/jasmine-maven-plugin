package com.github.searls.jasmine.mojo;

import com.github.searls.jasmine.config.JasmineConfiguration;
import com.github.searls.jasmine.config.ServerConfiguration;
import com.github.searls.jasmine.model.FileSystemReporter;
import com.github.searls.jasmine.model.Reporter;
import com.github.searls.jasmine.runner.ReporterType;
import com.google.common.base.Optional;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.assertj.core.api.Assertions;
import org.codehaus.plexus.resource.loader.FileResourceCreationException;
import org.codehaus.plexus.resource.loader.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.github.searls.jasmine.mojo.AbstractJasmineMojo.CUSTOM_RUNNER_CONFIGURATION_PARAM;
import static com.github.searls.jasmine.mojo.AbstractJasmineMojo.CUSTOM_RUNNER_TEMPLATE_PARAM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.guava.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractJasmineMojoTest {

  private static final String ENCODING = "UTF-8";
  private static final String CUSTOM_RUNNER_TEMPLATE = "/my/super/sweet/template";
  private static final String CUSTOM_RUNNER_CONFIGURATION = "/my/fancy/pants/config";

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  private File baseDir;

  @Mock
  private File targetDir;

  @Mock
  private File projectFile;

  @Mock
  private File parentProjectFile;

  @Mock
  private File sourceDirectory;

  @Mock
  private File specDirectory;

  @Mock
  private File customRunnerConfiguration;

  @Mock
  private MavenProject mavenProject;

  @Mock
  private ResourceRetriever resourceRetriever;

  @Mock
  private ReporterRetriever reporterRetriever;

  @InjectMocks
  private MockJasmineMojo subject;

  @Before
  public void before() throws MojoExecutionException {
    Whitebox.setInternalState(this.subject, "sourceEncoding", ENCODING);
    Whitebox.setInternalState(this.subject, "jasmineTargetDir", targetDir);
    Whitebox.setInternalState(this.subject, "jsSrcDir", sourceDirectory);
    Whitebox.setInternalState(this.subject, "jsTestSrcDir", specDirectory);

    when(mavenProject.getBasedir()).thenReturn(baseDir);

    when(resourceRetriever.getResourceAsFile(anyString(), isNull(String.class), eq(mavenProject)))
      .thenReturn(Optional.<File>absent());
  }

  @Test
  public void rethrowsMojoFailureExceptions() throws Exception {
    MojoFailureException mojoException = new MojoFailureException("mock exception");
    this.subject.setExceptionToThrow(mojoException);
    this.expectedException.expect(equalTo(mojoException));
    this.subject.execute();
  }

  @Test
  public void wrapsNonMojoFailureExceptions() throws Exception {
    IOException ioException = new IOException("mock exception");
    this.subject.setExceptionToThrow(ioException);
    this.expectedException.expect(MojoExecutionException.class);
    this.expectedException.expectMessage("The jasmine-maven-plugin encountered an exception:");
    this.expectedException.expectCause(equalTo(ioException));
    this.subject.execute();
  }

  @Test
  public void setsSourceIncludes() throws Exception {
    this.subject.execute();
    assertThat(getJasmineConfig().getSources().getIncludes()).contains("**" + File.separator + "*.js");
  }

  @Test
  public void setsSourceExcludes() throws Exception {
    this.subject.execute();
    assertThat(getJasmineConfig().getSources().getExcludes()).isEmpty();
  }

  @Test
  public void setsSpecIncludes() throws Exception {
    this.subject.execute();
    assertThat(getJasmineConfig().getSpecs().getIncludes()).contains("**" + File.separator + "*.js");
  }

  @Test
  public void setsSpecExcludes() throws Exception {
    this.subject.execute();
    assertThat(getJasmineConfig().getSpecs().getExcludes()).isEmpty();
  }

  @Test
  public void testGetSourceEncoding() throws MojoFailureException, MojoExecutionException {
    this.subject.execute();
    assertThat(getJasmineConfig().getSourceEncoding()).isEqualTo(ENCODING);
  }

  @Test
  public void testGetCustomRunnerConfiguration() throws
                                                 ResourceNotFoundException,
                                                 MojoExecutionException,
                                                 MojoFailureException,
                                                 FileResourceCreationException {
    File configFile = mock(File.class);

    Whitebox.setInternalState(this.subject, CUSTOM_RUNNER_CONFIGURATION_PARAM, CUSTOM_RUNNER_CONFIGURATION);

    when(resourceRetriever.getResourceAsFile(
      CUSTOM_RUNNER_CONFIGURATION_PARAM,
      CUSTOM_RUNNER_CONFIGURATION,
      mavenProject
    )).thenReturn(Optional.of(configFile));

    subject.execute();

    assertThat(getJasmineConfig().getCustomRunnerConfiguration())
      .isPresent()
      .contains(configFile);
  }

  @Test
  public void testGetCustomRunnerTemplate() throws Exception {
    File templateFile = mock(File.class);

    Whitebox.setInternalState(this.subject, CUSTOM_RUNNER_TEMPLATE_PARAM, CUSTOM_RUNNER_TEMPLATE);

    when(resourceRetriever.getResourceAsFile(CUSTOM_RUNNER_TEMPLATE_PARAM, CUSTOM_RUNNER_TEMPLATE, mavenProject))
      .thenReturn(Optional.of(templateFile));

    subject.execute();

    assertThat(getJasmineConfig().getCustomRunnerTemplate())
      .isPresent()
      .contains(templateFile);
  }

  @Test
  public void testGetReporters() throws
                                 ResourceNotFoundException,
                                 MojoExecutionException,
                                 MojoFailureException,
                                 FileResourceCreationException {
    List<Reporter> reporters = Collections.singletonList(mock(Reporter.class));
    Whitebox.setInternalState(subject, "reporters", reporters);
    when(reporterRetriever.retrieveReporters(reporters, mavenProject)).thenReturn(reporters);

    subject.execute();

    assertThat(getJasmineConfig().getReporters()).isEqualTo(reporters);
  }

  @Test
  public void testGetFileSystemReporters() throws
                                           ResourceNotFoundException,
                                           MojoExecutionException,
                                           MojoFailureException,
                                           FileResourceCreationException {
    List<FileSystemReporter> fsReporters = Collections.singletonList(mock(FileSystemReporter.class));
    Whitebox.setInternalState(subject, "fileSystemReporters", fsReporters);
    when(reporterRetriever.retrieveFileSystemReporters(fsReporters, targetDir, mavenProject)).thenReturn(fsReporters);

    subject.execute();

    assertThat(getJasmineConfig().getFileSystemReporters()).isEqualTo(fsReporters);
  }

  @Test
  public void testGetBaseDir() throws MojoFailureException, MojoExecutionException {
    when(this.mavenProject.getBasedir()).thenReturn(this.baseDir);
    subject.execute();
    assertThat(getJasmineConfig().getBasedir()).isEqualTo(this.baseDir);
  }

  @Test
  public void testGetMavenProject() {
    Assertions.assertThat(this.subject.getProject()).isEqualTo(this.mavenProject);
  }

  @Test
  public void testGetAutoRefreshInterval() throws MojoFailureException, MojoExecutionException {
    Whitebox.setInternalState(this.subject, "autoRefreshInterval", 5);
    subject.execute();
    Assertions.assertThat(getJasmineConfig().getAutoRefreshInterval()).isEqualTo(5);
  }

  private JasmineConfiguration getJasmineConfig() {
    return this.subject.getJasmineConfiguration();
  }

  static class MockJasmineMojo extends AbstractJasmineMojo {

    private ServerConfiguration serverConfiguration;
    private JasmineConfiguration jasmineConfiguration;
    private Exception exceptionToThrow;

    protected MockJasmineMojo(MavenProject mavenProject,
                              ResourceRetriever resourceRetriever,
                              ReporterRetriever reporterRetriever) {
      super(mavenProject, ReporterType.HtmlReporter, resourceRetriever, reporterRetriever);
    }

    @Override
    public void run(ServerConfiguration serverConfiguration,
                    JasmineConfiguration jasmineConfiguration) throws Exception {
      this.serverConfiguration = serverConfiguration;
      this.jasmineConfiguration = jasmineConfiguration;
      if (this.exceptionToThrow != null) {
        throw this.exceptionToThrow;
      }
    }

    public MavenProject getProject() {
      return this.getMavenProject();
    }

    public ServerConfiguration getServerConfiguration() {
      return serverConfiguration;
    }

    public JasmineConfiguration getJasmineConfiguration() {
      return this.jasmineConfiguration;
    }

    public void setExceptionToThrow(Exception exceptionToThrow) {
      this.exceptionToThrow = exceptionToThrow;
    }
  }
}
