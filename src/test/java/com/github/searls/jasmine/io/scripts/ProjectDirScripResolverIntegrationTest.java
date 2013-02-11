package com.github.searls.jasmine.io.scripts;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.maven.project.MavenProject;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.github.searls.jasmine.AbstractJasmineMojo;
import com.github.searls.jasmine.io.ScansDirectory;
import com.github.searls.jasmine.model.ScriptSearch;

public class ProjectDirScripResolverIntegrationTest {
  private final String[] excludes = new String[]{"vendor/vendor.js"};

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();
  private File root;
  private File sourceFolder;
  private File testFolder;
  private ProjectDirScripResolver projectDirScripResolver;

  @Before
  public void initFolders() throws IOException {
    root = tempFolder.newFolder("root");
    sourceFolder = createFile(root, "src/main/webapp/js", "src.js").getParentFile();
    createFile(root, "src/main/webapp/js/lib", "dep.js");
    createFile(root, "src/main/webapp/js/vendor", "vendor.js");
    testFolder = createFile(root, "src/test/javascript", "spec.js").getParentFile();

    initScriptResolver();

  }

  private void initScriptResolver() throws IOException {
  	MavenProject project = mock(MavenProject.class);
  	when(project.getBasedir()).thenReturn(root);
  	AbstractJasmineMojo config = mock(AbstractJasmineMojo.class);
  	when(config.getMavenProject()).thenReturn(project);
    when(config.getPreloadSources()).thenReturn(null);
    when(config.getSrcDirectoryName()).thenReturn("src");
    when(config.getSpecDirectoryName()).thenReturn("spec");
    when(config.getSources()).thenReturn(new ScriptSearch(sourceFolder, ScansDirectory.DEFAULT_INCLUDES, Arrays.asList(excludes)));
    when(config.getSpecs()).thenReturn(new ScriptSearch(testFolder, ScansDirectory.DEFAULT_INCLUDES, Collections.<String>emptyList()));
    projectDirScripResolver = new ProjectDirScripResolver(config);
    projectDirScripResolver.resolveScripts();
  }

  @Test
  public void shouldResolveSources() throws Exception {
    Set<String> sources = projectDirScripResolver.getSources();
    assertThat(sources, hasItem(endsWith("root/src/main/webapp/js/src.js")));
    assertThat(sources, hasItem(endsWith("root/src/main/webapp/js/lib/dep.js")));
    assertEquals(2, sources.size());
  }

  @Test
  public void shouldResolveRelativeSources() throws Exception {
    Set<String> sources = projectDirScripResolver.getSourcesRelativePath();
    assertThat(sources, hasItem(startsWith("src/main/webapp/js/src.js")));
    assertThat(sources, hasItem(startsWith("src/main/webapp/js/lib/dep.js")));
    assertEquals(2, sources.size());
  }

  @Test
  public void shouldResolveSpecs() throws Exception {
    Set<String> specs = projectDirScripResolver.getSpecs();
    assertThat(specs, hasItem(endsWith("root/src/test/javascript/spec.js")));
    assertEquals(1, specs.size());
  }

  @Test
  public void shouldResolveRelativeSpecs() throws Exception {

    Set<String> specs = projectDirScripResolver.getSpecsRelativePath();
    assertThat(specs, hasItem(startsWith("src/test/javascript/spec.js")));
    assertEquals(1, specs.size());
  }

  @Test
  @Ignore //TODO: Uhm, not sure if this should work?
  public void shouldResolvePreloads() throws Exception {
  	MavenProject project = mock(MavenProject.class);
  	when(project.getBasedir()).thenReturn(root);
  	AbstractJasmineMojo config = mock(AbstractJasmineMojo.class);
  	when(config.getMavenProject()).thenReturn(project);
    when(config.getPreloadSources()).thenReturn(null);
    when(config.getSrcDirectoryName()).thenReturn("src");
    when(config.getSpecDirectoryName()).thenReturn("spec");
    when(config.getSources()).thenReturn(new ScriptSearch(sourceFolder, ScansDirectory.DEFAULT_INCLUDES, Collections.<String>emptyList()));
    when(config.getSpecs()).thenReturn(new ScriptSearch(testFolder, ScansDirectory.DEFAULT_INCLUDES, Arrays.asList(new String[]{"vendor/vendor.js"})));
    ProjectDirScripResolver projectDirScripResolver = new ProjectDirScripResolver(config);
    projectDirScripResolver.resolveScripts();
    Set<String> preloads = projectDirScripResolver.getPreloads();
    assertThat(preloads, hasItem(endsWith("vendor/vendor.js")));
    assertEquals(1, preloads.size());
  }

  @Test
  public void shouldResolveAllScripts() throws Exception {
    Set<String> sources = projectDirScripResolver.getAllScripts();
    assertThat(sources, hasItem(endsWith("root/src/main/webapp/js/src.js")));
    assertThat(sources, hasItem(endsWith("root/src/main/webapp/js/lib/dep.js")));
    assertThat(sources, hasItem(endsWith("root/src/test/javascript/spec.js")));
    assertEquals(3, sources.size());
  }

  @Test
  public void shouldResolveAllScriptsRelativePath() throws Exception {
    Set<String> sources = projectDirScripResolver.getAllScriptsRelativePath();
    assertThat(sources, hasItem(startsWith("src/main/webapp/js/src.js")));
    assertThat(sources, hasItem(startsWith("src/main/webapp/js/lib/dep.js")));
    assertThat(sources, hasItem(startsWith("src/test/javascript/spec.js")));
    assertEquals(3, sources.size());
  }

  @Test
  public void shouldReturnSourcesDirectory() throws Exception {
    assertThat(projectDirScripResolver.getSourceDirectory(), Matchers.endsWith("src"));
  }

  @Test
  public void shouldReturnSourcesDirectoryRelativePath() throws Exception {
    assertThat(projectDirScripResolver.getSourceDirectoryRelativePath(), Matchers.startsWith("src/main/webapp/js"));
  }

  @Test
  public void shouldReturnSpecDirectory() throws Exception {
    assertThat(projectDirScripResolver.getSpecDirectoryPath(), Matchers.endsWith("spec"));
  }

  @Test
  public void shouldReturnSpecDirectoryRelativePath() throws Exception {
    assertThat(projectDirScripResolver.getSpecDirectoryRelativePath(), Matchers.startsWith("src/test/javascript"));
  }

  private File createFile(File root, String dir, String filename) throws IOException {
    File directory = new File(root, dir);
    directory.mkdirs();
    File newFile = new File(directory, filename);
    FileUtils.touch(newFile);
    return newFile;
  }
}
