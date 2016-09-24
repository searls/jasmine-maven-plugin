package com.github.searls.jasmine.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

public class ServerManagerFactory {

  public ServerManager create() {
    return create(new Server());
  }

  public ServerManager create(Server server) {
    ServerConnector connector = new ServerConnector(server);
    server.addConnector(connector);
    return new ServerManager(connector);
  }

}
