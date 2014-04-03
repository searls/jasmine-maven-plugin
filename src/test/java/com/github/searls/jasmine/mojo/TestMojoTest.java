package com.github.searls.jasmine.mojo;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.maven.plugin.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class TestMojoTest {

  @Mock
  private Log log;

  private TestMojo mojo;

  @Mock
  private Properties properties;

  @Before
  public void before() {
    this.mojo = new TestMojo();
    this.mojo.setLog(log);
  }

  @Test
  public void testExecuteIfSkipIsTrue() throws Exception {
    this.mojo.skipTests = true;
    this.mojo.execute();
    verify(log).info("Skipping Jasmine Specs");
  }
  
  @Test
  public void getHostName() {
    
    String hostName = mojo.getHostName();
    
    System.out.println("Hostname = " + hostName);
  }
  
  @Test
  public void getIP() {
    
    String ip = mojo.getIP();
    
    System.out.println("IP = " + ip);
  }
  
  @Test
  public void processServerHostName() {
    
    String remoteWebDriverUrl = "remoteWebDriverUrl";
    
    final String fakeIP       = "1.1.1.1";
    final String fakeHostname = "fakeHostname";
    
    TestMojo mojo = spy(new TestMojo());
    
    when(mojo.getIP()).thenReturn(fakeIP);
    when(mojo.getHostName()).thenReturn(fakeHostname);
    
    // With remoteWebDriverUrl
    assertEquals("serverHostname", mojo.processServerHostName("serverHostname", remoteWebDriverUrl));
    assertEquals(fakeHostname,     mojo.processServerHostName("HOSTNAME",       remoteWebDriverUrl));
    assertEquals(fakeIP,           mojo.processServerHostName("IP",             remoteWebDriverUrl));
    assertEquals(fakeIP,           mojo.processServerHostName(null,             remoteWebDriverUrl));

    // Without remoteWebDriverUrl
    assertEquals("serverHostname", mojo.processServerHostName("serverHostname", null));
    assertEquals("localhost",      mojo.processServerHostName(null,             null));
  }
}
