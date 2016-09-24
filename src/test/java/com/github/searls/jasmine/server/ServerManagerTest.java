package com.github.searls.jasmine.server;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
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

  @Before
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
