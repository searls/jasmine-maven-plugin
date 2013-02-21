package com.github.searls.jasmine;

import java.io.File;
import java.io.IOException;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;

import com.github.searls.jasmine.io.RelativizesFilePaths;
import com.github.searls.jasmine.server.JasmineResourceHandler;

public class ResourceHandlerConfigurator {

  private final AbstractJasmineMojo configuration;
  private final RelativizesFilePaths relativizesFilePaths;

  public ResourceHandlerConfigurator(AbstractJasmineMojo configuration, RelativizesFilePaths relativizesFilePaths) {
    this.configuration = configuration;
    this.relativizesFilePaths = relativizesFilePaths;
  }

  public Handler createHandler() throws IOException {
    ContextHandlerCollection contexts = new ContextHandlerCollection();

    ContextHandler srcDirContextHandler = contexts.addContext("/" + configuration.srcDirectoryName, "");
    srcDirContextHandler.setAliases(true);
    srcDirContextHandler.setHandler(createResourceHandler(true, configuration.sources.getDirectory().getAbsolutePath(), null));

    ContextHandler specDirContextHandler = contexts.addContext("/" + configuration.specDirectoryName, "");
    specDirContextHandler.setAliases(true);
    specDirContextHandler.setHandler(createResourceHandler(true, configuration.specs.getDirectory().getAbsolutePath(), null));

    ContextHandler rootContextHandler = contexts.addContext("/", "");
    rootContextHandler.setAliases(true);
    rootContextHandler.setHandler(createResourceHandler(false, configuration.mavenProject.getBasedir().getAbsolutePath(), new String[]{manualSpecRunnerPath()}));

    return contexts;
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
