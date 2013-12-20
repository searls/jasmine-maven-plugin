package com.github.searls.jasmine.server;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;

public class ServerManager {

  private static final int ANY_PORT = 0;

  private final Server server;
  private final Connector connector;
  private final ResourceHandlerConfigurator configurator;

  public ServerManager(Server server,
                       Connector connector,
                       ResourceHandlerConfigurator configurator) {
    this.server = server;
    this.connector = connector;
    this.configurator = configurator;
  }

  public int start() throws Exception {
    return this.startServer(ANY_PORT);
  }

  public void start(int port) throws Exception {
    this.startServer(port);
  }

  private int startServer(int port) throws Exception {
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
