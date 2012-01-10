package com.github.searls.jasmine;

import com.github.searls.jasmine.io.RelativizesFilePaths;
import com.github.searls.jasmine.server.JasmineResourceHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.*;

import java.io.File;
import java.io.IOException;

public class ResourceHandlerConfigurator {

	private AbstractJasmineMojo configuration;
	private RelativizesFilePaths relativizesFilePaths;

	public ResourceHandlerConfigurator(AbstractJasmineMojo configuration, RelativizesFilePaths relativizesFilePaths) {
		this.configuration = configuration;
		this.relativizesFilePaths = relativizesFilePaths;
	}

	public Handler createHandler(String specRunnerTemplate) throws IOException {
		Handler handler;
		if ("DEFAULT".equals(specRunnerTemplate)) {
			handler = createDefaultResourceHandler();
		} else if ("REQUIRE_JS".equals(specRunnerTemplate)) {
			handler = createContextualizedResourceHandler();
		} else {
			throw new UnsupportedOperationException("Unable to create handler for " + specRunnerTemplate + " profile!");
		}
		return handler;
	}

	private Handler createContextualizedResourceHandler() throws IOException {
		ContextHandlerCollection contexts = new ContextHandlerCollection();

		ContextHandler srcDirContextHandler = contexts.addContext("/" + configuration.srcDirectoryName, "");
		srcDirContextHandler.setHandler(createResourceHandler(true, configuration.sources.getDirectory().getAbsolutePath(), null));

		ContextHandler specDirContextHandler = contexts.addContext("/" + configuration.specDirectoryName, "");
		specDirContextHandler.setHandler(createResourceHandler(true, configuration.specs.getDirectory().getAbsolutePath(), null));

		ContextHandler rootContextHandler = contexts.addContext("/", "");
		rootContextHandler.setHandler(createResourceHandler(false, configuration.mavenProject.getBasedir().getAbsolutePath(), new String[]{manualSpecRunnerPath()}));

		return contexts;
	}

	private Handler createDefaultResourceHandler() throws IOException {
		ResourceHandler resourceHandler = createResourceHandler(true, configuration.mavenProject.getBasedir().getAbsolutePath(), new String[]{manualSpecRunnerPath()});

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[]{resourceHandler, new DefaultHandler()});
		return handlers;
	}

	private ResourceHandler createResourceHandler(boolean directory, String absolutePath, String[] welcomeFiles) throws IOException {
		ResourceHandler resourceHandler = new JasmineResourceHandler(configuration);
		resourceHandler.setDirectoriesListed(directory);
		if (null != welcomeFiles) {
			resourceHandler.setWelcomeFiles(welcomeFiles);
		}
		resourceHandler.setResourceBase(absolutePath);
		return resourceHandler;
	}

	private String manualSpecRunnerPath() throws IOException {
		return relativizesFilePaths.relativize(configuration.mavenProject.getBasedir(), configuration.jasmineTargetDir) + File.separator + configuration.manualSpecRunnerHtmlFileName;
	}
}
