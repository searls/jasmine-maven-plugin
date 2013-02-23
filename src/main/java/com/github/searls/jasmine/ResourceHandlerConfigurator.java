package com.github.searls.jasmine;

import java.io.File;
import java.io.IOException;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;

import com.github.searls.jasmine.io.RelativizesFilePaths;
import com.github.searls.jasmine.runner.ReporterType;
import com.github.searls.jasmine.server.JasmineResourceHandler;

public class ResourceHandlerConfigurator {

	private final AbstractJasmineMojo configuration;
	private final RelativizesFilePaths relativizesFilePaths;
	private final String welcome;
	private final ReporterType reporterType;

	public ResourceHandlerConfigurator(AbstractJasmineMojo configuration, RelativizesFilePaths relativizesFilePaths, String welcome, ReporterType reporterType) {
		this.configuration = configuration;
		this.relativizesFilePaths = relativizesFilePaths;
		this.welcome = welcome;
		this.reporterType = reporterType;
	}

	public Handler createHandler() throws IOException {
		ContextHandlerCollection contexts = new ContextHandlerCollection();

		ContextHandler srcDirContextHandler = contexts.addContext("/" + this.configuration.srcDirectoryName, "");
		srcDirContextHandler.setHandler(this.createResourceHandler(true, this.configuration.sources.getDirectory().getAbsolutePath(), null));

		ContextHandler specDirContextHandler = contexts.addContext("/" + this.configuration.specDirectoryName, "");
		specDirContextHandler.setHandler(this.createResourceHandler(true, this.configuration.specs.getDirectory().getAbsolutePath(), null));

		ContextHandler rootContextHandler = contexts.addContext("/", "");
		rootContextHandler.setHandler(this.createResourceHandler(false, this.configuration.mavenProject.getBasedir().getAbsolutePath(), new String[]{this.getWelcomeFilePath()}));

		return contexts;
	}

	private ResourceHandler createResourceHandler(boolean directory, String absolutePath, String[] welcomeFiles) throws IOException {
		ResourceHandler resourceHandler = new JasmineResourceHandler(this.configuration, this.welcome,this.reporterType);
		resourceHandler.setDirectoriesListed(directory);
		if (null != welcomeFiles) {
			resourceHandler.setWelcomeFiles(welcomeFiles);
		}
		resourceHandler.setResourceBase(absolutePath);
		return resourceHandler;
	}

	private String getWelcomeFilePath() throws IOException {
		return this.relativizesFilePaths.relativize(this.configuration.mavenProject.getBasedir(), this.configuration.jasmineTargetDir) + File.separator + this.welcome;
	}
}
