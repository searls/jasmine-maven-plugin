package com.github.searls.jasmine;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.resource.Resource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.searls.jasmine.io.RelativizesFilePaths;
import com.github.searls.jasmine.model.ScriptSearch;
import com.github.searls.jasmine.runner.SpecRunnerTemplate;
import com.github.searls.jasmine.server.JasmineResourceHandler;

@RunWith(MockitoJUnitRunner.class)
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
  @Mock private final Server server = new Server();
  @Mock private RelativizesFilePaths relativizesFilePaths;
  @Mock private File baseDir;
  @Mock private File targetDir;
  @Mock private File sourceDir;
  @Mock private File specDir;
  @Mock private ScriptSearch sources;
  @Mock private ScriptSearch specs;



  @Captor private ArgumentCaptor<SelectChannelConnector> connectorCaptor;
  @Captor private ArgumentCaptor<ContextHandlerCollection> handlerListCaptor;

  @Before
  public void arrangeAndAct() throws Exception {
    subject.sources = sources;
    subject.specs = specs;
    subject.setLog(log);
    subject.serverPort = PORT;
    subject.jasmineTargetDir = targetDir;
    subject.manualSpecRunnerHtmlFileName = MANUAL_SPEC_RUNNER_NAME;
    subject.specRunnerTemplate = SpecRunnerTemplate.DEFAULT;
    when(sourceDir.getAbsolutePath()).thenReturn(SOURCE_DIR);
    when(specDir.getAbsolutePath()).thenReturn(SPECS_DIR);
    when(sources.getDirectory()).thenReturn(sourceDir);
    when(specs.getDirectory()).thenReturn(specDir);
    when(baseDir.getAbsolutePath()).thenReturn(BASE_DIR);
    when(mavenProject.getBasedir()).thenReturn(baseDir);
    when(relativizesFilePaths.relativize(baseDir,targetDir)).thenReturn(RELATIVE_TARGET_DIR);
    when(relativizesFilePaths.relativize(baseDir,sources.getDirectory())).thenReturn(SOURCE_DIR);
    when(relativizesFilePaths.relativize(baseDir,specs.getDirectory())).thenReturn(SPECS_DIR);

    subject.run();
  }

  @Test
  public void logsInstructions() {
    verify(log).info(String.format(ServerMojo.INSTRUCTION_FORMAT, PORT, SOURCE_DIR, SPECS_DIR));
  }

  @Test
  public void addsConnector() throws Exception {
    verify(server).addConnector(connectorCaptor.capture());
    assertThat(connectorCaptor.getValue(),is(SelectChannelConnector.class));
    assertThat(connectorCaptor.getValue().getPort(),is(PORT));
  }

  @Test
  public void addsResourceHandler() throws Exception {
    verify(server).setHandler(handlerListCaptor.capture());
    
    Handler[] handlers = handlerListCaptor.getValue().getHandlers();
    assertThat(handlers.length,is(3));
    
    checkContextResourceHandler((ContextHandler) handlers[0], SOURCE_DIR, true, null);
    checkContextResourceHandler((ContextHandler) handlers[1], SPECS_DIR, true, null);
    checkContextResourceHandler((ContextHandler) handlers[2], BASE_DIR, false, RELATIVE_TARGET_DIR+File.separator+MANUAL_SPEC_RUNNER_NAME);
  }
  
  private void checkContextResourceHandler(ContextHandler ctxHandler,
  																				 String expectedBase,
  																				 boolean expectDirectoriesListed,
  																				 String expectedWelcomeFiles) throws MalformedURLException, IOException {
  	ResourceHandler handler = (ResourceHandler) ctxHandler.getHandlers()[0];

  	assertThat(handler,is(JasmineResourceHandler.class));
    assertThat(handler.isDirectoriesListed(),is(expectDirectoriesListed));
    assertThat(handler.getResourceBase(),is(Resource.newResource(expectedBase).toString()));
    if (expectedWelcomeFiles != null) {
    	assertThat(handler.getWelcomeFiles()[0],is(expectedWelcomeFiles));
    } else {
    	assertThat(handler.getWelcomeFiles()[0],is("index.html"));
    }
  }

  @Test
  public void addsContextHandlers() throws Exception {
    verify(server).setHandler(handlerListCaptor.capture());
    Handler[] handlers = handlerListCaptor.getValue().getHandlers();
    assertThat(handlers.length,is(3));
    assertThat(handlers[0],is(ContextHandler.class));
    assertThat(handlers[1],is(ContextHandler.class));
    assertThat(handlers[2],is(ContextHandler.class));
  }

  @Test
  public void startsTheServer() throws Exception {
    verify(server).start();
  }

  @Test
  public void joinsTheServer() throws Exception {
    verify(server).join();
  }




}
