package com.github.searls.jasmine.mojo;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.searls.jasmine.io.RelativizesFilePaths;
import com.github.searls.jasmine.model.ScriptSearch;
import com.github.searls.jasmine.runner.CreatesRunner;
import com.github.searls.jasmine.runner.ReporterType;
import com.github.searls.jasmine.runner.SpecRunnerTemplate;
import com.github.searls.jasmine.server.ResourceHandlerConfigurator;
import com.github.searls.jasmine.server.ServerManager;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ServerMojo.class)
public class ServerMojoTest {

  private static final String SPECS_DIR = "spec dir";
  private static final String SOURCE_DIR = "source dir";
  private static final int PORT = 8923;
  private static final String RELATIVE_TARGET_DIR = "some dir";
  private static final String MANUAL_SPEC_RUNNER_NAME = "nacho specs";
  private static final String BASE_DIR = "my-base-dir";

  @InjectMocks private final ServerMojo subject = new ServerMojo();

  @Mock private Log log;
  @Mock private MavenProject mavenProject;
  @Mock private RelativizesFilePaths relativizesFilePaths;
  @Mock private File baseDir;
  @Mock private File targetDir;
  @Mock private File sourceDir;
  @Mock private File specDir;
  @Mock private ScriptSearch sources;
  @Mock private ScriptSearch specs;
  @Mock private CreatesRunner createsRunner;
  @Mock private ResourceHandlerConfigurator configurator;
  @Mock private ServerManager serverManager;

  @Before
  public void arrangeAndAct() throws Exception {
    this.subject.sources = this.sources;
    this.subject.specs = this.specs;
    this.subject.setLog(this.log);
    this.subject.serverPort = PORT;
    this.subject.jasmineTargetDir = this.targetDir;
    this.subject.manualSpecRunnerHtmlFileName = MANUAL_SPEC_RUNNER_NAME;
    this.subject.specRunnerTemplate = SpecRunnerTemplate.DEFAULT;
    this.subject.debug = true;
    when(this.sourceDir.getAbsolutePath()).thenReturn(SOURCE_DIR);
    when(this.specDir.getAbsolutePath()).thenReturn(SPECS_DIR);
    when(this.sources.getDirectory()).thenReturn(this.sourceDir);
    when(this.specs.getDirectory()).thenReturn(this.specDir);
    when(this.baseDir.getAbsolutePath()).thenReturn(BASE_DIR);
    when(this.mavenProject.getBasedir()).thenReturn(this.baseDir);
    when(this.relativizesFilePaths.relativize(this.baseDir,this.targetDir)).thenReturn(RELATIVE_TARGET_DIR);
    when(this.relativizesFilePaths.relativize(this.baseDir,this.sources.getDirectory())).thenReturn(SOURCE_DIR);
    when(this.relativizesFilePaths.relativize(this.baseDir,this.specs.getDirectory())).thenReturn(SPECS_DIR);

    whenNew(CreatesRunner.class).withArguments(
        this.subject,
        this.log,
        MANUAL_SPEC_RUNNER_NAME,
        ReporterType.HtmlReporter).thenReturn(createsRunner);

    whenNew(ResourceHandlerConfigurator.class).withArguments(
        this.subject,
        this.relativizesFilePaths,
        createsRunner).thenReturn(configurator);

    whenNew(ServerManager.class).withArguments(configurator).thenReturn(serverManager);

    this.subject.run();
  }

  @Test
  public void logsInstructions() {
    verify(this.log).info(String.format(ServerMojo.INSTRUCTION_FORMAT, PORT, SOURCE_DIR, SPECS_DIR));
  }

  @Test
  public void startsTheServer() throws Exception {
    verify(this.serverManager).start(PORT);
  }

  @Test
  public void joinsTheServer() throws Exception {
    verify(this.serverManager).join();
  }
}
