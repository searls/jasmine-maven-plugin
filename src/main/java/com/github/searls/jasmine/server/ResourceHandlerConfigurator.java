package com.github.searls.jasmine.server;

import java.io.File;
import java.io.IOException;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;

import com.github.searls.jasmine.config.JasmineConfiguration;
import com.github.searls.jasmine.io.RelativizesFilePaths;
import com.github.searls.jasmine.runner.CreatesRunner;

public class ResourceHandlerConfigurator {

  private final JasmineConfiguration configuration;
  private final RelativizesFilePaths relativizesFilePaths;
  private final CreatesRunner createsRunner;

  public ResourceHandlerConfigurator(JasmineConfiguration configuration, RelativizesFilePaths relativizesFilePaths, CreatesRunner createsRunner) {
    this.configuration = configuration;
    this.relativizesFilePaths = relativizesFilePaths;
    this.createsRunner = createsRunner;
  }

  public Handler createHandler() throws IOException  {
    ContextHandlerCollection contexts = new ContextHandlerCollection();

    ContextHandler srcDirContextHandler = contexts.addContext("/" + this.configuration.getSrcDirectoryName(), "");
    srcDirContextHandler.setAliases(true);
    srcDirContextHandler.setHandler(this.createResourceHandler(true, this.configuration.getSources().getDirectory().getAbsolutePath(), null));

    ContextHandler specDirContextHandler = contexts.addContext("/" + this.configuration.getSpecDirectoryName(), "");
    specDirContextHandler.setAliases(true);
    specDirContextHandler.setHandler(this.createResourceHandler(true, this.configuration.getSpecs().getDirectory().getAbsolutePath(), null));

    ContextHandler rootContextHandler = contexts.addContext("/", "");
    rootContextHandler.setAliases(true);
    rootContextHandler.setHandler(this.createResourceHandler(false, this.configuration.getBasedir().getAbsolutePath(), new String[]{this.getWelcomeFilePath()}));

    return contexts;
  }

  private ResourceHandler createResourceHandler(boolean directory, String absolutePath, String[] welcomeFiles) {
    ResourceHandler resourceHandler = new JasmineResourceHandler(this.createsRunner, this.configuration);
    resourceHandler.setDirectoriesListed(directory);
    if (welcomeFiles != null) {
      resourceHandler.setWelcomeFiles(welcomeFiles);
    }
    resourceHandler.setResourceBase(absolutePath);
    return resourceHandler;
  }

  private String getWelcomeFilePath() throws IOException {
    return this.relativizesFilePaths.relativize(this.configuration.getBasedir(), this.configuration.getJasmineTargetDir()) + File.separator + createsRunner.getRunnerFile();
  }
}
