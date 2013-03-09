package com.github.searls.jasmine;

import java.io.IOException;

import org.apache.maven.plugins.annotations.Mojo;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;

import com.github.searls.jasmine.runner.ReporterType;

/**
 * Execute specs in a web browser. Monitors your sources/specs for changes as you develop.
 */
@Mojo(name="bdd",requiresDirectInvocation=true)
public class ServerMojo extends AbstractServerMojo {

  public static final String INSTRUCTION_FORMAT =
      "\n\n" +
          "Server started--it's time to spec some JavaScript! You can run your specs as you develop by visiting this URL in a web browser: \n\n" +
          "  http://localhost:%s"+
          "\n\n" +
          "The server will monitor these two directories for scripts that you add, remove, and change:\n\n" +
          "  source directory: %s\n\n"+
          "  spec directory: %s"+
          "\n\n"+
          "Just leave this process running as you test-drive your code, refreshing your browser window to re-run your specs. You can kill the server with Ctrl-C when you're done.";


  @Override
  protected void configure(Connector connector) {
    connector.setPort(this.serverPort);
  }

  private String buildServerInstructions() throws IOException {
    return String.format(
        INSTRUCTION_FORMAT,
        this.serverPort,
        this.getRelativePath(this.sources.getDirectory()),
        this.getRelativePath(this.specs.getDirectory()));
  }

  @Override
  protected void executeJasmine(Server server) throws Exception {
    server.start();
    this.getLog().info(this.buildServerInstructions());
    server.join();
  }

  @Override
  protected ReporterType getReporterType() {
    return ReporterType.HtmlReporter;
  }

  @Override
  protected String getSpecRunnerFilename() {
    return this.manualSpecRunnerHtmlFileName;
  }
}
