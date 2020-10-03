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

import com.github.searls.jasmine.config.JasmineConfiguration;
import com.github.searls.jasmine.config.ServerConfiguration;
import com.github.searls.jasmine.model.FileSystemReporter;
import com.github.searls.jasmine.model.Reporter;
import com.github.searls.jasmine.runner.ReporterType;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.github.searls.jasmine.mojo.AbstractJasmineMojo.CUSTOM_RUNNER_CONFIGURATION_PARAM;
import static com.github.searls.jasmine.mojo.AbstractJasmineMojo.CUSTOM_RUNNER_TEMPLATE_PARAM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractJasmineMojoTest {

  private static final String ENCODING = "UTF-8";
  private static final String CUSTOM_RUNNER_TEMPLATE = "/my/super/sweet/template";
  private static final String CUSTOM_RUNNER_CONFIGURATION = "/my/fancy/pants/config";

  @Mock
  private File baseDir;

  @Mock
  private File targetDir;

  @Mock
  private File sourceDirectory;

  @Mock
  private File specDirectory;

  @Mock
  private MavenProject mavenProject;

  @Mock
  private ResourceRetriever resourceRetriever;

  @Mock
  private ReporterRetriever reporterRetriever;

  @InjectMocks
  private MockJasmineMojo mojo;

  @Before
  public void before() throws MojoExecutionException {
    mojo.setJsSrcDir(sourceDirectory);
    mojo.setJsTestSrcDir(specDirectory);
    mojo.setJasmineTargetDir(targetDir);
    mojo.setSourceEncoding(ENCODING);

    when(mavenProject.getBasedir()).thenReturn(baseDir);

    when(resourceRetriever.getResourceAsFile(anyString(), isNull(), eq(mavenProject)))
      .thenReturn(Optional.empty());
  }

  @Test
  public void rethrowsMojoFailureExceptions() {
    MojoFailureException mojoException = new MojoFailureException("mock exception");
    mojo.setExceptionToThrow(mojoException);
    assertThatExceptionOfType(MojoFailureException.class)
      .isThrownBy(() -> mojo.execute())
      .isSameAs(mojoException);
  }

  @Test
  public void wrapsNonMojoFailureExceptions() {
    IOException ioException = new IOException("mock exception");
    mojo.setExceptionToThrow(ioException);
    assertThatExceptionOfType(MojoExecutionException.class)
      .isThrownBy(() -> mojo.execute())
      .withMessage("The jasmine-maven-plugin encountered an exception:")
      .withCause(ioException);
  }

  @Test
  public void setsSourceIncludes() throws Exception {
    mojo.execute();
    assertThat(getJasmineConfig().getSources().getIncludes()).contains("**" + File.separator + "*.js");
  }

  @Test
  public void setsSourceExcludes() throws Exception {
    mojo.execute();
    assertThat(getJasmineConfig().getSources().getExcludes()).isEmpty();
  }

  @Test
  public void setsSpecIncludes() throws Exception {
    mojo.execute();
    assertThat(getJasmineConfig().getSpecs().getIncludes()).contains("**" + File.separator + "*.js");
  }

  @Test
  public void setsSpecExcludes() throws Exception {
    mojo.execute();
    assertThat(getJasmineConfig().getSpecs().getExcludes()).isEmpty();
  }

  @Test
  public void testGetSourceEncoding() throws MojoFailureException, MojoExecutionException {
    mojo.execute();
    assertThat(getJasmineConfig().getSourceEncoding()).isEqualTo(ENCODING);
  }

  @Test
  public void testGetCustomRunnerConfiguration() throws MojoExecutionException, MojoFailureException {
    File configFile = mock(File.class);

    mojo.setCustomRunnerConfiguration(CUSTOM_RUNNER_CONFIGURATION);

    when(resourceRetriever.getResourceAsFile(
      CUSTOM_RUNNER_CONFIGURATION_PARAM,
      CUSTOM_RUNNER_CONFIGURATION,
      mavenProject
    )).thenReturn(Optional.of(configFile));

    mojo.execute();

    assertThat(getJasmineConfig().getCustomRunnerConfiguration())
      .isPresent()
      .contains(configFile);
  }

  @Test
  public void testGetCustomRunnerTemplate() throws Exception {
    File templateFile = mock(File.class);

    mojo.setCustomRunnerTemplate(CUSTOM_RUNNER_TEMPLATE);

    when(resourceRetriever.getResourceAsFile(CUSTOM_RUNNER_TEMPLATE_PARAM, CUSTOM_RUNNER_TEMPLATE, mavenProject))
      .thenReturn(Optional.of(templateFile));

    mojo.execute();

    assertThat(getJasmineConfig().getCustomRunnerTemplate())
      .isPresent()
      .contains(templateFile);
  }

  @Test
  public void testGetReporters() throws MojoExecutionException, MojoFailureException {
    List<Reporter> reporters = Collections.singletonList(mock(Reporter.class));
    mojo.setReporters(reporters);
    when(reporterRetriever.retrieveReporters(reporters, mavenProject)).thenReturn(reporters);

    mojo.execute();

    assertThat(getJasmineConfig().getReporters()).isEqualTo(reporters);
  }

  @Test
  public void testGetFileSystemReporters() throws MojoExecutionException, MojoFailureException {
    List<FileSystemReporter> fsReporters = Collections.singletonList(mock(FileSystemReporter.class));
    mojo.setFileSystemReporters(fsReporters);

    when(reporterRetriever.retrieveFileSystemReporters(fsReporters, targetDir, mavenProject)).thenReturn(fsReporters);

    mojo.execute();

    assertThat(getJasmineConfig().getFileSystemReporters()).isEqualTo(fsReporters);
  }

  @Test
  public void testGetBaseDir() throws MojoFailureException, MojoExecutionException {
    when(this.mavenProject.getBasedir()).thenReturn(this.baseDir);
    mojo.execute();
    assertThat(getJasmineConfig().getBasedir()).isEqualTo(this.baseDir);
  }

  @Test
  public void testGetMavenProject() {
    Assertions.assertThat(mojo.getProject()).isEqualTo(this.mavenProject);
  }

  @Test
  public void testGetAutoRefreshInterval() throws MojoFailureException, MojoExecutionException {
    mojo.setAutoRefreshInterval(5);
    mojo.execute();
    Assertions.assertThat(getJasmineConfig().getAutoRefreshInterval()).isEqualTo(5);
  }

  private JasmineConfiguration getJasmineConfig() {
    return mojo.getJasmineConfiguration();
  }

  static class MockJasmineMojo extends AbstractJasmineMojo {

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
      this.jasmineConfiguration = jasmineConfiguration;
      if (this.exceptionToThrow != null) {
        throw this.exceptionToThrow;
      }
    }

    public MavenProject getProject() {
      return this.getMavenProject();
    }

    public JasmineConfiguration getJasmineConfiguration() {
      return this.jasmineConfiguration;
    }

    public void setExceptionToThrow(Exception exceptionToThrow) {
      this.exceptionToThrow = exceptionToThrow;
    }
  }
}
