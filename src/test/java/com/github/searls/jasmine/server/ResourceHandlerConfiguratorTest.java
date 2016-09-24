package com.github.searls.jasmine.server;

import com.github.searls.jasmine.config.JasmineConfiguration;
import com.github.searls.jasmine.io.RelativizesFilePaths;
import com.github.searls.jasmine.model.ScriptSearch;
import com.github.searls.jasmine.mojo.Context;
import com.github.searls.jasmine.runner.CreatesRunner;
import com.github.searls.jasmine.thirdpartylibs.ProjectClassLoaderFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ResourceHandlerConfiguratorTest {

  private static final String WELCOME_FILE = "welcomefile";
  private static final String SOURCE_DIRECTORY = "sourcedir";
  private static final String SPEC_DIRECTORY = "specdir";
  private static final String BASE_DIRECTORY = "basedir";
  private static final String SOURCE_CONTEXT_ROOT = "sourceroot";
  private static final String SPEC_CONTEXT_ROOT = "specroot";

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

  @Mock
  private CreatesRunner createsRunner;

  @Mock
  private Context contextA;

  @Mock
  private Context contextB;

  private List<Context> contexts;

  @InjectMocks
  private ResourceHandlerConfigurator configurator;

  @Before
  public void before() {
    contexts = Arrays.asList(contextA, contextB);
  }

  @Test
  public void testCreateHandler() throws IOException {

    when(sourceDirectory.getCanonicalPath()).thenReturn(SOURCE_DIRECTORY);
    when(specDirectory.getCanonicalPath()).thenReturn(SPEC_DIRECTORY);
    when(baseDirectory.getCanonicalPath()).thenReturn(BASE_DIRECTORY);

    when(configuration.getBasedir()).thenReturn(baseDirectory);
    when(configuration.getProjectClassLoader()).thenReturn(new ProjectClassLoaderFactory().create());
    when(configuration.getSpecRunnerHtmlFileName()).thenReturn(WELCOME_FILE);
    when(configuration.getContexts()).thenReturn(contexts);

    when(contextA.getContextRoot()).thenReturn(SOURCE_CONTEXT_ROOT);
    when(contextA.getDirectory()).thenReturn(sourceDirectory);

    when(contextB.getContextRoot()).thenReturn(SPEC_CONTEXT_ROOT);
    when(contextB.getDirectory()).thenReturn(specDirectory);

    this.configurator.createHandler(configuration);
  }

}
