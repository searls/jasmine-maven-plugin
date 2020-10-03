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
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ServerManagerTest {

  @Mock
  private Server server;

  @Mock
  private ServerConnector connector;

  @Mock
  private Handler handler;

  @Captor
  private ArgumentCaptor<Connector> connectorCaptor;

  @InjectMocks
  private ServerManager serverManager;

  @BeforeEach
  public void before() {
    when(connector.getServer()).thenReturn(server);
  }

  @Test
  public void testStartAnyPort() throws Exception {
    this.serverManager.start(handler);
    verify(this.connector).setPort(0);
  }

  @Test
  public void testStartOnSpecificPort() throws Exception {
    this.serverManager.start(1234, handler);
    verify(this.connector).setPort(1234);
  }

  @Test
  public void testStop() throws Exception {
    this.serverManager.stop();
    verify(this.server).stop();
  }

  @Test
  public void testJoin() throws Exception {
    this.serverManager.join();
    verify(this.server).join();
  }

}
