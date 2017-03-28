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
