package com.github.searls.jasmine.io.scripts;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ContextPathScriptResolverTest {

  private static final String ROOT_CONTEXT_PATH = "";
  private static final String SOURCE_CONTEXT_PATH = "src";
  private static final String SPEC_CONTEXT_PATH = "spec";

  private static final String BASE_DIRECTORY = "/the/base/directory";
  private static final String SOURCE_DIRECTORY = "/the/source/directory";
  private static final String SPEC_DIRECTORY = "/the/spec/directory";

  @Mock
  private ScriptResolver scriptResolver;

  private ScriptResolver contextPathScriptResolver;

  @Before
  public void before() {
    this.contextPathScriptResolver = new ContextPathScriptResolver(scriptResolver, ROOT_CONTEXT_PATH, SOURCE_CONTEXT_PATH, SPEC_CONTEXT_PATH);
  }

  @Test
  public void testGetSourceDirectory() throws ScriptResolverException {
    assertEquals(SOURCE_CONTEXT_PATH,this.contextPathScriptResolver.getSourceDirectory());
  }

  @Test
  public void testGetSpecDirectory() throws ScriptResolverException {
    assertEquals(SPEC_CONTEXT_PATH,this.contextPathScriptResolver.getSpecDirectory());
  }

  @Test
  public void testGetSources() throws ScriptResolverException {
    String[] scripts = new String[] {"scriptA","scriptB","lib/scriptC"};

    when(this.scriptResolver.getSourceDirectory()).thenReturn(SOURCE_DIRECTORY);
    when(this.scriptResolver.getSources()).thenReturn(setOf(SOURCE_DIRECTORY,scripts));

    Set<String> expected = setOf(SOURCE_CONTEXT_PATH,scripts);

    assertEquals(expected, this.contextPathScriptResolver.getSources());
  }

  @Test
  public void testGetSpecs() throws ScriptResolverException {
    String[] scripts = new String[] {"scriptA","scriptB","lib/scriptC"};

    when(this.scriptResolver.getSpecDirectory()).thenReturn(SPEC_DIRECTORY);
    when(this.scriptResolver.getSpecs()).thenReturn(setOf(SPEC_DIRECTORY,scripts));

    Set<String> expected = setOf(SPEC_CONTEXT_PATH,scripts);

    assertEquals(expected, this.contextPathScriptResolver.getSpecs());
  }

  @Test
  public void testGetPreloads() throws ScriptResolverException {
    Set<String> preloads = new HashSet<String>();
    preloads.add(BASE_DIRECTORY+"/lib/baseScript");
    preloads.add(SPEC_DIRECTORY+"/specScript");
    preloads.add(SOURCE_DIRECTORY+"/sourceScript");
    preloads.add("http://example.org/script.js");

    when(this.scriptResolver.getBaseDirectory()).thenReturn(BASE_DIRECTORY);
    when(this.scriptResolver.getSourceDirectory()).thenReturn(SOURCE_DIRECTORY);
    when(this.scriptResolver.getSpecDirectory()).thenReturn(SPEC_DIRECTORY);
    when(this.scriptResolver.getPreloads()).thenReturn(preloads);

    Set<String> expected = new HashSet<String>();
    expected.add(ROOT_CONTEXT_PATH+"/lib/baseScript");
    expected.add(SPEC_CONTEXT_PATH+"/specScript");
    expected.add(SOURCE_CONTEXT_PATH+"/sourceScript");
    expected.add("http://example.org/script.js");

    assertEquals(expected, this.contextPathScriptResolver.getPreloads());
  }

  private static Set<String> setOf(String base, String ... strings) {
    Set<String> set = new HashSet<String>();
    for (String string : strings) {
      set.add(base+"/"+string);
    }
    return set;
  }

}
