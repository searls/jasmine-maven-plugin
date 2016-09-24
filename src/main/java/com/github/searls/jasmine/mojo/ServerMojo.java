package com.github.searls.jasmine.mojo;

import com.github.searls.jasmine.config.JasmineConfiguration;
import com.github.searls.jasmine.config.ServerConfiguration;
import com.github.searls.jasmine.io.RelativizesFilePaths;
import com.github.searls.jasmine.runner.ReporterType;
import com.github.searls.jasmine.server.ResourceHandlerConfigurator;
import com.github.searls.jasmine.server.ServerManager;
import com.github.searls.jasmine.server.ServerManagerFactory;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

/**
 * Execute specs in a web browser. Monitors your sources/specs for changes as you develop.
 */
@Mojo(name = "bdd", requiresDirectInvocation = true, requiresDependencyResolution = ResolutionScope.TEST)
public class ServerMojo extends AbstractJasmineMojo {

  private static final Logger LOGGER = LoggerFactory.getLogger(ServerMojo.class);

  private static final String INSTRUCTION_FORMAT = getInstructionsTemplate();

  private final RelativizesFilePaths relativizesFilePaths;
  private final ResourceHandlerConfigurator resourceHandlerConfigurator;
  private final ServerManagerFactory serverManagerFactory;

  @Inject
  public ServerMojo(MavenProject mavenProject,
                    ResourceRetriever resourceRetriever,
                    ReporterRetriever reporterRetriever,
                    RelativizesFilePaths relativizesFilePaths,
                    ResourceHandlerConfigurator resourceHandlerConfigurator,
                    ServerManagerFactory serverManagerFactory) {
    super(mavenProject, ReporterType.HtmlReporter, resourceRetriever, reporterRetriever);
    this.relativizesFilePaths = relativizesFilePaths;
    this.resourceHandlerConfigurator = resourceHandlerConfigurator;
    this.serverManagerFactory = serverManagerFactory;
  }

  @Override
  public void run(ServerConfiguration serverConfiguration,
                  JasmineConfiguration jasmineConfiguration) throws Exception {
    ServerManager serverManager = serverManagerFactory.create();
    serverManager.start(serverConfiguration.getServerPort(), resourceHandlerConfigurator.createHandler(jasmineConfiguration));
    LOGGER.info(this.buildServerInstructions(serverConfiguration, jasmineConfiguration));
    serverManager.join();
  }

  private String buildServerInstructions(ServerConfiguration serverConfiguration,
                                         JasmineConfiguration jasmineConfiguration) throws IOException {
    return String.format(
      INSTRUCTION_FORMAT,
      serverConfiguration.getServerURL(),
      getSourcePath(jasmineConfiguration),
      getSpecPath(jasmineConfiguration)
    );
  }

  private String getSourcePath(JasmineConfiguration config) throws IOException {
    return this.getRelativePath(config.getBasedir(), config.getSources().getDirectory());
  }

  private String getSpecPath(JasmineConfiguration config) throws IOException {
    return this.getRelativePath(config.getBasedir(), config.getSpecs().getDirectory());
  }

  private String getRelativePath(File basedir, File absolutePath) throws IOException {
    return this.relativizesFilePaths.relativize(basedir, absolutePath);
  }

  private static String getInstructionsTemplate() {
    String template;
    try {
      template = IOUtils.toString(ServerMojo.class.getResourceAsStream("/instructions.template"));
    } catch (IOException e) {
      template = "";
      LOGGER.error("Unable to read instructions template: ", e);
    }
    return template;
  }
}
