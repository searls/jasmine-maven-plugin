package com.github.searls.jasmine;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;

import org.apache.maven.project.MavenProject;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
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

@RunWith(MockitoJUnitRunner.class)
public class ServerMojoTest {

	private static final int PORT = 8923;
	private static final String RELATIVE_TARGET_DIR = "some dir";
	private static final String MANUAL_SPEC_RUNNER_NAME = "nacho specs";
	private static final String BASE_DIR = "my-base-dir";
	
	@InjectMocks private ServerMojo subject = new ServerMojo();
	
	@Mock private MavenProject mavenProject;
	@Mock private Server server = new Server();
	@Mock private RelativizesFilePaths relativizesFilePaths;
	@Mock private File baseDir;
	@Mock private File targetDir;
	
	@Captor private ArgumentCaptor<SelectChannelConnector> connectorCaptor;
	@Captor private ArgumentCaptor<HandlerList> handlerListCaptor;
	
	@Before
	public void arrangeAndAct() throws Exception {
		subject.serverPort = PORT;
		subject.jasmineTargetDir = targetDir;
		subject.manualSpecRunnerHtmlFileName = MANUAL_SPEC_RUNNER_NAME;
		when(baseDir.getAbsolutePath()).thenReturn(BASE_DIR);
		when(mavenProject.getBasedir()).thenReturn(baseDir);
		when(relativizesFilePaths.relativize(baseDir,targetDir)).thenReturn(RELATIVE_TARGET_DIR);
		
		subject.run();
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
		ResourceHandler handler = (ResourceHandler) handlerListCaptor.getValue().getHandlers()[0];
		
		assertThat(handler.isDirectoriesListed(),is(true));
		assertThat(handler.getWelcomeFiles()[0],is(RELATIVE_TARGET_DIR+File.separator+MANUAL_SPEC_RUNNER_NAME));
		assertThat(handler.getResourceBase(),is(Resource.newResource(BASE_DIR).toString()));
	}
	
	@Test
	public void addsDefaultHandler() throws Exception {
		verify(server).setHandler(handlerListCaptor.capture());
		assertThat(handlerListCaptor.getValue().getHandlers()[1],is(DefaultHandler.class));
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
