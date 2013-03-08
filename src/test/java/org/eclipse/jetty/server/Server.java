package org.eclipse.jetty.server;

import org.eclipse.jetty.util.component.Container;

//Link Seam for jetty server.
public class Server {

  public void addConnector(Connector connector) {}

  public void start() throws Exception {}

  public void stop() throws Exception {}

  public void join() throws Exception {}

  public void setHandler(Handler handler) {}

  public Container getContainer() {
    return null;
  }

  public interface Graceful extends Handler {
    public void setShutdown(boolean shutdown);
  }
}
