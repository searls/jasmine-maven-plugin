package com.github.searls.jasmine;

import java.io.File;
import java.io.IOException;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;

import com.github.searls.jasmine.io.RelativizesFilePaths;
import com.github.searls.jasmine.runner.ReporterType;

public abstract class AbstractServerMojo extends AbstractJasmineMojo {

	private final Server server;
	private final RelativizesFilePaths relativizesFilePaths;

	private Connector connector;

	protected AbstractServerMojo() {
		this.server = new Server();
		this.relativizesFilePaths = new RelativizesFilePaths();
		this.connector = null;
	}

	@Override
	public void run() throws Exception {
		this.addConnectorToServer();
		this.addHandlersToServer();
		this.executeJasmine(this.server);
	}

	private void addHandlersToServer() throws IOException {
		this.server.setHandler(
				new ResourceHandlerConfigurator(
						this, this.relativizesFilePaths,
						this.getSpecRunnerFilename(),
						this.getReporterType()).createHandler());
	}

	private void addConnectorToServer() {
		this.connector = new SelectChannelConnector();
		this.configure(this.connector);
		this.server.addConnector(this.connector);
	}

	protected void configure(Connector connector) {

	}

	protected abstract void executeJasmine(Server server) throws Exception;
	protected abstract ReporterType getReporterType();
	protected abstract String getSpecRunnerFilename();

	protected String getRelativePath(File absolutePath) throws IOException {
		return this.relativizesFilePaths.relativize(this.mavenProject.getBasedir(), absolutePath);
	}

	protected int getPort() {
		return this.connector.getLocalPort();
	}
}
