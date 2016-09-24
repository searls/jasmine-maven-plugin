package com.github.searls.jasmine.mojo;

import com.github.searls.jasmine.config.JasmineConfiguration;
import com.github.searls.jasmine.config.ServerConfiguration;
import com.github.searls.jasmine.io.RelativizesFilePaths;
import com.github.searls.jasmine.model.ScriptSearch;
import com.github.searls.jasmine.runner.SpecRunnerTemplate;
import com.github.searls.jasmine.server.ResourceHandlerConfigurator;
import com.github.searls.jasmine.server.ServerManager;
import com.github.searls.jasmine.server.ServerManagerFactory;
import org.apache.maven.project.MavenProject;
import org.eclipse.jetty.server.Handler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ServerMojo.class})
public class ServerMojoTest {

  private static final String SPECS_DIR = "spec dir";
  private static final String SOURCE_DIR = "source dir";
  private static final String SCHEME = "http";
  private static final int PORT = 8923;
  private static final String RELATIVE_TARGET_DIR = "some dir";
  private static final String MANUAL_SPEC_RUNNER_NAME = "nacho specs";
  private static final String BASE_DIR = "my-base-dir";

  @Mock
  private MavenProject mavenProject;

  @Mock
  private ServerConfiguration serverConfiguration;

  @Mock
  private JasmineConfiguration jasmineConfiguration;

  @Mock
  private RelativizesFilePaths relativizesFilePaths;

  @Mock
  private File baseDir;

  @Mock
  private File targetDir;

  @Mock
  private File sourceDir;

  @Mock
  private File specDir;

  @Mock
  private ScriptSearch sources;

  @Mock
  private ScriptSearch specs;

  @Mock
  private ResourceHandlerConfigurator configurator;

  @Mock
  private ServerManager serverManager;

  @Mock
  private ServerManagerFactory serverManagerFactory;

  @Mock
  private Handler handler;

  @InjectMocks
  private ServerMojo subject;

  @Before
  public void arrangeAndAct() throws Exception {
    when(serverConfiguration.getUriScheme()).thenReturn(SCHEME);
    when(serverConfiguration.getServerPort()).thenReturn(PORT);

    when(jasmineConfiguration.getSources()).thenReturn(sources);
    when(jasmineConfiguration.getSpecs()).thenReturn(specs);
    when(jasmineConfiguration.getJasmineTargetDir()).thenReturn(targetDir);
    when(jasmineConfiguration.getSpecRunnerTemplate()).thenReturn(SpecRunnerTemplate.DEFAULT);

    when(sourceDir.getAbsolutePath()).thenReturn(SOURCE_DIR);
    when(specDir.getAbsolutePath()).thenReturn(SPECS_DIR);
    when(sources.getDirectory()).thenReturn(sourceDir);
    when(specs.getDirectory()).thenReturn(specDir);
    when(baseDir.getAbsolutePath()).thenReturn(BASE_DIR);
    when(mavenProject.getBasedir()).thenReturn(baseDir);
    when(relativizesFilePaths.relativize(baseDir, targetDir)).thenReturn(RELATIVE_TARGET_DIR);
    when(relativizesFilePaths.relativize(baseDir, sources.getDirectory())).thenReturn(SOURCE_DIR);
    when(relativizesFilePaths.relativize(baseDir, specs.getDirectory())).thenReturn(SPECS_DIR);
    when(serverManagerFactory.create()).thenReturn(serverManager);
    when(configurator.createHandler(jasmineConfiguration)).thenReturn(handler);


    subject.run(serverConfiguration, jasmineConfiguration);
  }

  @Test
  public void startsTheServer() throws Exception {
    verify(serverManager).start(PORT, handler);
  }

  @Test
  public void joinsTheServer() throws Exception {
    verify(serverManager).join();
  }
}
