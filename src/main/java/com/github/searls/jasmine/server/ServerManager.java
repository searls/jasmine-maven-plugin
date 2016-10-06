package com.github.searls.jasmine.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

public class ServerManager {

  private static final int ANY_PORT = 0;

  private final ServerConnector connector;
  private final ResourceHandlerConfigurator configurator;

  protected ServerManager(ServerConnector connector,
                          ResourceHandlerConfigurator configurator) {
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
    connector.getServer().setHandler(this.configurator.createHandler());
    connector.getServer().start();

    return connector.getLocalPort();
  }

  public void stop() throws Exception {
    connector.getServer().stop();
  }

  public void join() throws Exception {
    connector.getServer().join();
  }

  public static ServerManager newInstance(ResourceHandlerConfigurator configurator) {
    Server server = new Server();
    ServerConnector connector = new ServerConnector(server);
    server.addConnector(connector);
    return new ServerManager(connector, configurator);
  }
}
