package com.github.searls.jasmine.server;

import com.github.searls.jasmine.config.JasmineConfiguration;
import com.github.searls.jasmine.io.RelativizesFilePaths;
import com.github.searls.jasmine.mojo.Context;
import com.github.searls.jasmine.runner.CreatesRunner;
import com.github.searls.jasmine.thirdpartylibs.ClassPathResourceHandler;
import com.github.searls.jasmine.thirdpartylibs.WebJarResourceHandler;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;

import java.io.File;
import java.io.IOException;

public class ResourceHandlerConfigurator {

  private final JasmineConfiguration configuration;
  private final RelativizesFilePaths relativizesFilePaths;
  private final CreatesRunner createsRunner;

  public ResourceHandlerConfigurator(JasmineConfiguration configuration,
                                     RelativizesFilePaths relativizesFilePaths,
                                     CreatesRunner createsRunner) {
    this.configuration = configuration;
    this.relativizesFilePaths = relativizesFilePaths;
    this.createsRunner = createsRunner;
  }

  public Handler createHandler() throws IOException  {
    ContextHandlerCollection contexts = new ContextHandlerCollection();

    for (Context context : this.configuration.getContexts()) {
      String contextRoot = StringUtils.prependIfMissing(context.getContextRoot(),"/");
      ContextHandler handler = contexts.addContext(contextRoot, "");
      handler.setAliases(true);
      handler.setHandler(this.createResourceHandler(true, context.getDirectory().getCanonicalPath(), null));
    }

    ContextHandler rootContextHandler = contexts.addContext("/", "");
    rootContextHandler.setHandler(this.createResourceHandler(false, this.configuration.getBasedir().getCanonicalPath(), new String[]{this.getWelcomeFilePath()}));
    rootContextHandler.setAliases(true);

    ContextHandler classPathContextHandler = contexts.addContext("/classpath", "");
    classPathContextHandler.setHandler(new ClassPathResourceHandler(configuration.getProjectClassLoader()));
    classPathContextHandler.setAliases(true);

    ContextHandler webJarsContextHandler = contexts.addContext("/webjars", "");
    webJarsContextHandler.setHandler(new WebJarResourceHandler(configuration.getProjectClassLoader()));
    webJarsContextHandler.setAliases(true);

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
