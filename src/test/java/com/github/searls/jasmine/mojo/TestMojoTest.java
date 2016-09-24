package com.github.searls.jasmine.mojo;

import com.github.searls.jasmine.driver.WebDriverFactory;
import com.github.searls.jasmine.format.JasmineResultLogger;
import com.github.searls.jasmine.io.RelativizesFilePaths;
import com.github.searls.jasmine.runner.CreatesRunner;
import com.github.searls.jasmine.runner.SpecRunnerExecutor;
import com.github.searls.jasmine.server.ResourceHandlerConfigurator;
import org.eclipse.aether.RepositorySystem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Properties;

@RunWith(PowerMockRunner.class)
public class TestMojoTest {

  @Mock
  private Properties properties;

  @Mock
  private RepositorySystem repositorySystem;

  @Mock
  private WebDriverFactory webDriverFactory;

  @Mock
  private RelativizesFilePaths relativizesFilePaths;

  @Mock
  private CreatesRunner createsRunner;

  @Mock
  private SpecRunnerExecutor specRunnerExecutor;

  @Mock
  private JasmineResultLogger jasmineResultLogger;

  @Mock
  private ResourceHandlerConfigurator resourceHandlerConfigurator;

  @InjectMocks
  private TestMojo mojo;

  @Test
  public void testExecuteIfSkipIsTrue() throws Exception {
    Whitebox.setInternalState(this.mojo, "skipTests", true);
    this.mojo.execute();
  }
}
