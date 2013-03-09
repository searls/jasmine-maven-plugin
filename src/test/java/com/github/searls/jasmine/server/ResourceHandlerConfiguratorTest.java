package com.github.searls.jasmine.server;

import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.searls.jasmine.config.JasmineConfiguration;
import com.github.searls.jasmine.io.RelativizesFilePaths;
import com.github.searls.jasmine.model.ScriptSearch;
import com.github.searls.jasmine.runner.ReporterType;

@RunWith(MockitoJUnitRunner.class)
public class ResourceHandlerConfiguratorTest {

  private static final String WELCOME_FILE = "welcomefile";
  private static final String SOURCE_DIRECTORY = "sourcedir";
  private static final String SPEC_DIRECTORY = "specdir";
  private static final String BASE_DIRECTORY = "basedir";

  private ResourceHandlerConfigurator configurator;

  @Mock
  private JasmineConfiguration configuration;

  @Mock
  private RelativizesFilePaths relativizesFilePaths;

  @Mock
  private File sourceDirectory;

  @Mock
  private File specDirectory;

  @Mock
  private File baseDirectory;

  @Mock
  private ScriptSearch sources;

  @Mock
  private ScriptSearch specs;

  @Before
  public void before() {
    this.configurator = new ResourceHandlerConfigurator(
        configuration,
        relativizesFilePaths,
        WELCOME_FILE,
        ReporterType.HtmlReporter);
  }

  @Test
  public void testCreateHandler() throws IOException {
    when(sourceDirectory.getAbsolutePath()).thenReturn(SOURCE_DIRECTORY);
    when(specDirectory.getAbsolutePath()).thenReturn(SPEC_DIRECTORY);
    when(baseDirectory.getAbsolutePath()).thenReturn(BASE_DIRECTORY);

    when(sources.getDirectory()).thenReturn(sourceDirectory);
    when(specs.getDirectory()).thenReturn(specDirectory);

    when(configuration.getSpecs()).thenReturn(specs);
    when(configuration.getSources()).thenReturn(sources);
    when(configuration.getBasedir()).thenReturn(baseDirectory);

    this.configurator.createHandler();
  }

}
