package org.eclipse.jetty.server;

import org.eclipse.jetty.util.component.Container;

//Link Seam for jetty server.
public class Server {

  public void addConnector(Connector connector) {}

  public void start() {}

  public void join() {}

  public void setHandler(Handler handler) {}

  public Container getContainer() { return null; }
}
