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
package com.github.searls.jasmine.mojo;

import com.github.searls.jasmine.NullLog;
import com.github.searls.jasmine.io.RelativizesFilePaths;
import com.github.searls.jasmine.runner.CreatesRunner;
import com.github.searls.jasmine.runner.ReporterType;
import com.github.searls.jasmine.server.ResourceHandlerConfigurator;
import com.github.searls.jasmine.server.ServerManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.io.IOException;

/**
 * Execute specs in a web browser. Monitors your sources/specs for changes as you develop.
 */
@Mojo(name = "bdd", requiresDirectInvocation = true, requiresDependencyResolution = ResolutionScope.TEST)
public class ServerMojo extends AbstractJasmineMojo {

  public static final String INSTRUCTION_FORMAT =
    "\n\n" +
      "Server started--it's time to spec some JavaScript! You can run your specs as you develop by visiting this URL in a web browser: \n\n" +
      " %s://localhost:%s" +
      "\n\n" +
      "The server will monitor these two directories for scripts that you add, remove, and change:\n\n" +
      "  source directory: %s\n\n" +
      "  spec directory: %s" +
      "\n\n" +
      "Just leave this process running as you test-drive your code, refreshing your browser window to re-run your specs. You can kill the server with Ctrl-C when you're done.";

  private final RelativizesFilePaths relativizesFilePaths;

  public ServerMojo() {
    this(new RelativizesFilePaths());
  }

  public ServerMojo(RelativizesFilePaths relativizesFilePaths) {
    this.relativizesFilePaths = relativizesFilePaths;
  }

  private String buildServerInstructions() throws IOException {
    return String.format(
      INSTRUCTION_FORMAT,
      this.uriScheme,
      this.serverPort,
      this.getRelativePath(this.sources.getDirectory()),
      this.getRelativePath(this.specs.getDirectory()));
  }

  @Override
  public void run() throws Exception {
    ServerManager serverManager = this.getServerManager();

    serverManager.start(this.serverPort);
    this.getLog().info(this.buildServerInstructions());
    serverManager.join();
  }

  private ServerManager getServerManager() throws MojoExecutionException {
    Log log = this.debug ? this.getLog() : new NullLog();

    CreatesRunner createsRunner = new CreatesRunner(
      this,
      log,
      this.manualSpecRunnerHtmlFileName,
      ReporterType.HtmlReporter);

    ResourceHandlerConfigurator configurator = new ResourceHandlerConfigurator(
      this,
      this.relativizesFilePaths,
      createsRunner);

    return ServerManager.newInstance(configurator);
  }

  private String getRelativePath(File absolutePath) throws IOException {
    return this.relativizesFilePaths.relativize(this.mavenProject.getBasedir(), absolutePath);
  }
}
