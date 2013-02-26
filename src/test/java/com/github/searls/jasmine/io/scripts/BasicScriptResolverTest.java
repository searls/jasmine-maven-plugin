package com.github.searls.jasmine.io.scripts;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.searls.jasmine.model.ScriptSearch;

@RunWith(MockitoJUnitRunner.class)
public class BasicScriptResolverTest {

  private static final String SOURCE_DIRECTORY = "/project/source/directory";
  private static final String SPEC_DIRECTORY = "/project/spec/directory";

  @Mock
  private ScriptSearch sourceScriptSearch;

  @Mock
  private ScriptSearch specScriptSearch;

  @Mock
  private File sourceDirectory;

  @Mock
  private File specDirectory;

  private URI sourceURI;

  private URI specURI;

  private List<String> preloadList;

  private BasicScriptResolver resolver;

  @Before
  public void before() throws URISyntaxException {
    this.sourceURI = new URI(SOURCE_DIRECTORY);
    this.specURI = new URI(SPEC_DIRECTORY);
    this.preloadList = new ArrayList<String>();
  }

  @Test
  public void testGetSourceDirectory() {
    when(sourceDirectory.toURI()).thenReturn(this.sourceURI);

    when(sourceDirectory.canRead()).thenReturn(true);
    when(specDirectory.canRead()).thenReturn(true);

    when(this.sourceScriptSearch.getDirectory()).thenReturn(sourceDirectory);
    when(this.specScriptSearch.getDirectory()).thenReturn(specDirectory);

    this.resolver = new BasicScriptResolver(sourceScriptSearch, specScriptSearch, preloadList);

    assertEquals(SOURCE_DIRECTORY,this.resolver.getSourceDirectory());
  }

  @Test
  public void testGetSpecDirectory() {
    when(specDirectory.toURI()).thenReturn(this.specURI);

    when(sourceDirectory.canRead()).thenReturn(true);
    when(specDirectory.canRead()).thenReturn(true);

    when(this.sourceScriptSearch.getDirectory()).thenReturn(sourceDirectory);
    when(this.specScriptSearch.getDirectory()).thenReturn(specDirectory);

    this.resolver = new BasicScriptResolver(sourceScriptSearch, specScriptSearch, preloadList);

    assertEquals(SPEC_DIRECTORY,this.resolver.getSpecDirectory());
  }
}
