package com.github.searls.jasmine;

import java.io.File;
import java.io.IOException;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;

import com.github.searls.jasmine.io.RelativizesFilePaths;

/**
 * @goal server
 */
public class ServerMojo extends AbstractJasmineMojo {

	private Server server = new Server();
	
	private RelativizesFilePaths relativizesFilePaths = new RelativizesFilePaths();
	
	@Override
	public void run() throws Exception {
		SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(serverPort);
        server.addConnector(connector);
 
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);       
        resourceHandler.setWelcomeFiles(new String[]{ manualSpecRunnerPath() });
        resourceHandler.setResourceBase(mavenProject.getBasedir().getAbsolutePath());

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resourceHandler, new DefaultHandler() });
        server.setHandler(handlers);

        server.start();
		server.join();
	}

	private String manualSpecRunnerPath() throws IOException {
		return relativizesFilePaths.relativize(mavenProject.getBasedir(), jasmineTargetDir) + File.separator +manualSpecRunnerHtmlFileName;
	}

}
