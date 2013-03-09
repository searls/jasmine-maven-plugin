package com.github.searls.jasmine.server;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;

public class ServerManager {

  private static final int ANY_PORT = 0;

  private final Server server;
  private final ResourceHandlerConfigurator configurator;

  public ServerManager(Server server, ResourceHandlerConfigurator configurator) {
    this.configurator = configurator;
    this.server = server;
  }

  public ServerManager(ResourceHandlerConfigurator configurator) {
    this(new Server(),configurator);
  }

  public int start() throws Exception {
    return this.startServer(ANY_PORT);
  }

  public void start(int port) throws Exception {
    this.startServer(port);
  }

  private int startServer(int port) throws Exception {
    Connector connector = new SelectChannelConnector();
    connector.setPort(port);

    this.server.setHandler(this.configurator.createHandler());
    this.server.addConnector(connector);

    this.server.start();

    return connector.getLocalPort();
  }

  public void stop() throws Exception {
    this.server.stop();
  }

  public void join() throws Exception {
    this.server.join();
  }
}
