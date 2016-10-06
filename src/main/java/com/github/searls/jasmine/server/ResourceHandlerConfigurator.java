package com.github.searls.jasmine.server;

import com.github.searls.jasmine.config.JasmineConfiguration;
import com.github.searls.jasmine.io.RelativizesFilePaths;
import com.github.searls.jasmine.mojo.Context;
import com.github.searls.jasmine.runner.CreatesRunner;
import com.github.searls.jasmine.thirdpartylibs.ClassPathResourceHandler;
import com.github.searls.jasmine.thirdpartylibs.WebJarResourceHandler;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.AllowSymLinkAliasChecker;
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

  public Handler createHandler() throws IOException {
    ContextHandlerCollection contexts = new ContextHandlerCollection();

    for (Context context : this.configuration.getContexts()) {
      String contextRoot = StringUtils.prependIfMissing(context.getContextRoot(), "/");
      addContext(contexts, contextRoot, this.createResourceHandler(true, context.getDirectory().getCanonicalPath()));
    }

    addContext(contexts, "/", this.createResourceHandler(
      false,
      this.configuration.getBasedir().getCanonicalPath(),
      this.getWelcomeFilePath()
    ));
    addContext(contexts, "/classpath", new ClassPathResourceHandler(configuration.getProjectClassLoader()));
    addContext(contexts, "/webjars", new WebJarResourceHandler(configuration.getProjectClassLoader()));
    return contexts;
  }

  private void addContext(ContextHandlerCollection contexts, String contextPath, Handler handler) {
    ContextHandler contextHandler = contexts.addContext(contextPath, "");
    contextHandler.setHandler(handler);
    contextHandler.addAliasCheck(new AllowSymLinkAliasChecker());
  }

  private ResourceHandler createResourceHandler(boolean directory, String absolutePath, String ... welcomeFiles) {
    ResourceHandler resourceHandler = new JasmineResourceHandler(this.createsRunner);
    resourceHandler.setDirectoriesListed(directory);
    if (welcomeFiles.length > 0) {
      resourceHandler.setWelcomeFiles(welcomeFiles);
    }
    resourceHandler.setResourceBase(absolutePath);
    return resourceHandler;
  }

  private String getWelcomeFilePath() throws IOException {
    return this.relativizesFilePaths.relativize(this.configuration.getBasedir(), this.configuration.getJasmineTargetDir()) + File.separator + createsRunner.getRunnerFile();
  }
}
