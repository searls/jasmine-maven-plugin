package com.github.searls.jasmine;

import static org.hamcrest.Matchers.instanceOf;
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
		this.subject.sources = this.sources;
		this.subject.specs = this.specs;
		this.subject.setLog(this.log);
		this.subject.serverPort = PORT;
		this.subject.jasmineTargetDir = this.targetDir;
		this.subject.manualSpecRunnerHtmlFileName = MANUAL_SPEC_RUNNER_NAME;
		this.subject.specRunnerTemplate = SpecRunnerTemplate.DEFAULT;
		when(this.sourceDir.getAbsolutePath()).thenReturn(SOURCE_DIR);
		when(this.specDir.getAbsolutePath()).thenReturn(SPECS_DIR);
		when(this.sources.getDirectory()).thenReturn(this.sourceDir);
		when(this.specs.getDirectory()).thenReturn(this.specDir);
		when(this.baseDir.getAbsolutePath()).thenReturn(BASE_DIR);
		when(this.mavenProject.getBasedir()).thenReturn(this.baseDir);
		when(this.relativizesFilePaths.relativize(this.baseDir,this.targetDir)).thenReturn(RELATIVE_TARGET_DIR);
		when(this.relativizesFilePaths.relativize(this.baseDir,this.sources.getDirectory())).thenReturn(SOURCE_DIR);
		when(this.relativizesFilePaths.relativize(this.baseDir,this.specs.getDirectory())).thenReturn(SPECS_DIR);

		this.subject.run();
	}

	@Test
	public void logsInstructions() {
		verify(this.log).info(String.format(ServerMojo.INSTRUCTION_FORMAT, PORT, SOURCE_DIR, SPECS_DIR));
	}

	@Test
	public void addsConnector() throws Exception {
		verify(this.server).addConnector(this.connectorCaptor.capture());
		assertThat(this.connectorCaptor.getValue(),is(instanceOf(SelectChannelConnector.class)));
		assertThat(this.connectorCaptor.getValue().getPort(),is(PORT));
	}

	@Test
	public void addsResourceHandler() throws Exception {
		verify(this.server).setHandler(this.handlerListCaptor.capture());

		Handler[] handlers = this.handlerListCaptor.getValue().getHandlers();
		assertThat(handlers.length,is(3));

		this.checkContextResourceHandler((ContextHandler) handlers[0], SOURCE_DIR, true, null);
		this.checkContextResourceHandler((ContextHandler) handlers[1], SPECS_DIR, true, null);
		this.checkContextResourceHandler((ContextHandler) handlers[2], BASE_DIR, false, RELATIVE_TARGET_DIR+File.separator+MANUAL_SPEC_RUNNER_NAME);
	}

	private void checkContextResourceHandler(ContextHandler ctxHandler,
	                                         String expectedBase,
	                                         boolean expectDirectoriesListed,
	                                         String expectedWelcomeFiles) throws MalformedURLException, IOException {
		ResourceHandler handler = (ResourceHandler) ctxHandler.getHandlers()[0];

		assertThat(handler,is(instanceOf(JasmineResourceHandler.class)));
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
		verify(this.server).setHandler(this.handlerListCaptor.capture());
		Handler[] handlers = this.handlerListCaptor.getValue().getHandlers();
		assertThat(handlers.length,is(3));
		assertThat(handlers[0],is(instanceOf(ContextHandler.class)));
		assertThat(handlers[1],is(instanceOf(ContextHandler.class)));
		assertThat(handlers[2],is(instanceOf(ContextHandler.class)));
	}

	@Test
	public void startsTheServer() throws Exception {
		verify(this.server).start();
	}

	@Test
	public void joinsTheServer() throws Exception {
		verify(this.server).join();
	}




}
