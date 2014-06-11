package com.github.searls.jasmine.mojo;

import static org.mockito.Mockito.verify;
//import static org.junit.Assert.assertTrue;

import com.sun.jna.Platform;
import java.util.Properties;

import org.apache.maven.plugin.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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
  
  @Test(timeout = 10000)
  public void testCleanupChildProcessesInShutdownHook() throws Exception {
    ProcessBuilder builder;
    Process process;
    if ( Platform.isWindows() ) {
      builder = new ProcessBuilder( "timeout", "60" );
    } else {
      builder = new ProcessBuilder( "sleep", "60" );
    }
    process = builder.start();
    Thread.sleep( 1000 );
    this.mojo.attachShutDownHook();
    verify(log).debug("Attaching ShutdownHook");
    this.mojo.shutdownHook();
    verify(log).debug("Cleanup Child Processes");
    process.waitFor();
  }
  
}
