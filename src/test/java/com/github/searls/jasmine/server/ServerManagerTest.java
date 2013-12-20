package com.github.searls.jasmine.server;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ServerManagerTest {

  @Mock
  private ResourceHandlerConfigurator configurator;

  @Mock
  private Server server;

  @Mock
  private Connector connector;

  private ServerManager serverManager;

  @Captor
  private ArgumentCaptor<Connector> connectorCaptor;

  @Before
  public void before() {
    this.serverManager = new ServerManager(server,connector,configurator);
  }

  @Test
  public void testStartAnyPort() throws Exception {
    this.serverManager.start();

    verify(this.server).addConnector(this.connector);
    verify(this.connector).setPort(0);
  }

  @Test
  public void testStartOnSpecificPort() throws Exception {
    this.serverManager.start(1234);

    verify(this.server).addConnector(connectorCaptor.capture());
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
