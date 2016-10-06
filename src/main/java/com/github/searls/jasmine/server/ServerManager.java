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

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

public class ServerManager {

  private static final int ANY_PORT = 0;

  private final ServerConnector connector;

  protected ServerManager(ServerConnector connector) {
    this.connector = connector;
  }

  public int start(Handler handler) throws Exception {
    return this.start(ANY_PORT, handler);
  }

  public int start(int port, Handler handler) throws Exception {
    connector.setPort(port);
    connector.getServer().setHandler(handler);
    connector.getServer().start();

    return connector.getLocalPort();
  }

  public void stop() throws Exception {
    connector.getServer().stop();
  }

  public void join() throws Exception {
    connector.getServer().join();
  }

  public static ServerManager newInstance() {
    Server server = new Server();
    ServerConnector connector = new ServerConnector(server);
    server.addConnector(connector);
    return new ServerManager(connector);
  }
}
