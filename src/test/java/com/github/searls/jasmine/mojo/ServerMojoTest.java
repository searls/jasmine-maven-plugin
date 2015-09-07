package com.github.searls.jasmine.mojo;

import com.github.searls.jasmine.io.RelativizesFilePaths;
import com.github.searls.jasmine.model.ScriptSearch;
import com.github.searls.jasmine.runner.CreatesRunner;
import com.github.searls.jasmine.runner.ReporterType;
import com.github.searls.jasmine.runner.SpecRunnerTemplate;
import com.github.searls.jasmine.server.ResourceHandlerConfigurator;
import com.github.searls.jasmine.server.ServerManager;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.net.BindException;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ServerMojo.class)
public class ServerMojoTest {

  private static final String SPECS_DIR = "spec dir";
  private static final String SOURCE_DIR = "source dir";
  private static final String SCHEME = "http";
  private static final int PORT = 8923;
  private static final String RELATIVE_TARGET_DIR = "some dir";
  private static final String MANUAL_SPEC_RUNNER_NAME = "nacho specs";
  private static final String BASE_DIR = "my-base-dir";

  private static final String connectorClassString = "org.eclipse.jetty.server.nio.SelectChannelConnector";
  private static final Class<? extends Connector> connectorClass = org.eclipse.jetty.server.nio.SelectChannelConnector.class;

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

  private ServerMojo subject;

  @Before
  public void arrangeAndAct() throws Exception {
    this.subject = new ServerMojo(relativizesFilePaths);
    this.subject.mavenProject = mavenProject;
    this.subject.sources = this.sources;
    this.subject.specs = this.specs;
    this.subject.setLog(this.log);
    this.subject.uriScheme = SCHEME;
    this.subject.serverPort = PORT;
    this.subject.jasmineTargetDir = this.targetDir;
    this.subject.manualSpecRunnerHtmlFileName = MANUAL_SPEC_RUNNER_NAME;
    this.subject.specRunnerTemplate = SpecRunnerTemplate.DEFAULT;
    this.subject.debug = true;
    this.subject.connectorClass = connectorClassString;
    when(this.sourceDir.getAbsolutePath()).thenReturn(SOURCE_DIR);
    when(this.specDir.getAbsolutePath()).thenReturn(SPECS_DIR);
    when(this.sources.getDirectory()).thenReturn(this.sourceDir);
    when(this.specs.getDirectory()).thenReturn(this.specDir);
    when(this.baseDir.getAbsolutePath()).thenReturn(BASE_DIR);
    when(this.mavenProject.getBasedir()).thenReturn(this.baseDir);
    when(this.relativizesFilePaths.relativize(this.baseDir,this.targetDir)).thenReturn(RELATIVE_TARGET_DIR);
    when(this.relativizesFilePaths.relativize(this.baseDir,this.sources.getDirectory())).thenReturn(SOURCE_DIR);
    when(this.relativizesFilePaths.relativize(this.baseDir, this.specs.getDirectory())).thenReturn(SPECS_DIR);

    whenNew(CreatesRunner.class).withArguments(
        this.subject,
        this.log,
        MANUAL_SPEC_RUNNER_NAME,
        ReporterType.HtmlReporter).thenReturn(createsRunner);

    whenNew(ResourceHandlerConfigurator.class).withArguments(
        this.subject,
        this.relativizesFilePaths,
        createsRunner).thenReturn(configurator);

    whenNew(ServerManager.class).withArguments(isA(Server.class), isA(connectorClass), eq(configurator)).thenReturn(serverManager);
  }

  @Test
  public void logsInstructions() throws Exception {
    subject.run();
    verify(this.log).info(String.format(ServerMojo.INSTRUCTION_FORMAT, SCHEME, PORT, SOURCE_DIR, SPECS_DIR));
  }

  @Test
  public void startsTheServer() throws Exception {
    subject.run();
    verify(this.serverManager).start(PORT);
  }

  @Test
  public void startServerOnFreePort() throws Exception {
    final int freePort = PORT+3;
    doAnswer(new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
          if ((Integer) invocationOnMock.getArguments()[0] < freePort) {
              throw new BindException();
          }
          return null;
      }
    }).when(serverManager).start(anyInt());

    subject.run();
    verify(serverManager, atLeast(3)).start(anyInt());
    verify(serverManager).start(freePort);
  }

  @Test
  public void joinsTheServer() throws Exception {
    subject.run();
    verify(this.serverManager).join();
  }
}
