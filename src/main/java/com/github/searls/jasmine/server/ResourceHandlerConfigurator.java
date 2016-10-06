/*-
 * #%L
 * jasmine-maven-plugin
 * %%
 * Copyright (C) 2010 - 2017 Justin Searls
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
    ResourceHandler resourceHandler = new JasmineResourceHandler(this.createsRunner, this.configuration);
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
