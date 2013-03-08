package com.github.searls.jasmine.server;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

@RunWith(MockitoJUnitRunner.class)
public class ServerManagerTest {

  @Mock
  private ResourceHandlerConfigurator configurator;

  @Mock
  private Server server;

  private ServerManager serverManager;

  @Captor
  private ArgumentCaptor<Connector> connectorCaptor;

  @Before
  public void before() {
    this.serverManager = new ServerManager(server,configurator);
  }

  @Test
  public void testStartAnyPort() throws Exception {
    this.serverManager.start();

    verify(this.server).addConnector(connectorCaptor.capture());
    assertThat(this.connectorCaptor.getValue(),is(instanceOf(SelectChannelConnector.class)));
    assertThat(this.connectorCaptor.getValue().getPort(),is(0));
  }

  @Test
  public void testStartOnSpecificPort() throws Exception {
    this.serverManager.start(1234);

    verify(this.server).addConnector(connectorCaptor.capture());
    assertThat(this.connectorCaptor.getValue(),is(instanceOf(SelectChannelConnector.class)));
    assertThat(this.connectorCaptor.getValue().getPort(),is(1234));
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

  @Test
  public void testConstructWithoutServer() throws Exception {
    ServerManager manager = new ServerManager(this.configurator);
    Server newServer = Whitebox.getInternalState(manager, Server.class);
    assertNotNull(newServer);
  }
}
