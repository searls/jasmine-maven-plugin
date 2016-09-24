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

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.IOException;

@Named
public class ResourceHandlerConfigurator {

  private final RelativizesFilePaths relativizesFilePaths;
  private final CreatesRunner createsRunner;

  @Inject
  public ResourceHandlerConfigurator(RelativizesFilePaths relativizesFilePaths,
                                     CreatesRunner createsRunner) {
    this.relativizesFilePaths = relativizesFilePaths;
    this.createsRunner = createsRunner;
  }

  public Handler createHandler(JasmineConfiguration configuration) throws IOException {
    ContextHandlerCollection contexts = new ContextHandlerCollection();

    for (Context context : configuration.getContexts()) {
      String contextRoot = StringUtils.prependIfMissing(context.getContextRoot(), "/");
      addContext(
        contexts,
        contextRoot,
        this.createResourceHandler(configuration, true, context.getDirectory().getCanonicalPath())
      );
    }

    addContext(contexts, "/", this.createResourceHandler(
      configuration,
      false,
      configuration.getBasedir().getCanonicalPath(),
      this.getWelcomeFilePath(configuration)
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

  private ResourceHandler createResourceHandler(JasmineConfiguration configuration,
                                                boolean directory,
                                                String absolutePath,
                                                String... welcomeFiles) {
    ResourceHandler resourceHandler = new JasmineResourceHandler(this.createsRunner, configuration);
    resourceHandler.setDirectoriesListed(directory);
    if (welcomeFiles.length > 0) {
      resourceHandler.setWelcomeFiles(welcomeFiles);
    }
    resourceHandler.setResourceBase(absolutePath);
    return resourceHandler;
  }

  private String getWelcomeFilePath(JasmineConfiguration configuration) throws IOException {
    return this.relativizesFilePaths.relativize(
      configuration.getBasedir(),
      configuration.getJasmineTargetDir()
    ) + File.separator + configuration.getSpecRunnerHtmlFileName();
  }
}
