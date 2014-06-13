package com.github.searls.jasmine.mojo;

import static org.mockito.Mockito.verify;

import java.util.Properties;

import org.apache.maven.plugin.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openqa.selenium.WebDriver;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class TestMojoTest {

  @Mock
  private Log log;

  private TestMojo mojo;

  @Mock
  private Properties properties;

  @Mock
  private WebDriver driver;
  
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
  public void testCleanupChildProcessesInShutdownHook() throws Exception {
    this.mojo.attachShutDownHook(driver);
    verify(log).debug("Attaching ShutdownHook");
    this.mojo.shutdownHook(driver);
    verify(log).debug("Cleanup Child Processes");
	verify(driver).quit();
  }
  
}
