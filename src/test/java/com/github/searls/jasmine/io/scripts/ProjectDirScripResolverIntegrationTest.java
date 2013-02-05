package com.github.searls.jasmine.io.scripts;

import com.github.searls.jasmine.io.ScansDirectory;
import com.github.searls.jasmine.model.ScriptSearch;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Code changes because of compiler bug http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=7034548
 * @author kreyssel
 */
public class ProjectDirScripResolverIntegrationTest {
  private String[] excludes = new String[]{"vendor/vendor.js"};

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
    projectDirScripResolver = new ProjectDirScripResolver(root,
        new ScriptSearch(sourceFolder, ScansDirectory.DEFAULT_INCLUDES, Arrays.asList(excludes)),
        new ScriptSearch(testFolder, ScansDirectory.DEFAULT_INCLUDES, Collections.<String>emptyList()),
        null);
    projectDirScripResolver.resolveScripts();
  }

  @Test
  public void shouldResolveSources() throws Exception {
    Set<String> sources = projectDirScripResolver.getSources();
    Matcher<Iterable<? super String>> matcher1 = hasItem(endsWith("root/src/main/webapp/js/src.js"));
	assertThat(sources, matcher1);
	Matcher<Iterable<? super String>> matcher2 = hasItem(endsWith("root/src/main/webapp/js/lib/dep.js"));
    assertThat(sources, matcher2);
    assertEquals(2, sources.size());
  }

  @Test
  public void shouldResolveRelativeSources() throws Exception {
    Set<String> sources = projectDirScripResolver.getSourcesRelativePath();
    Matcher<Iterable<? super String>> matcher1 = hasItem(startsWith("src/main/webapp/js/src.js"));
    assertThat(sources, matcher1);
    Matcher<Iterable<? super String>> matcher2 = hasItem(startsWith("src/main/webapp/js/lib/dep.js"));
    assertThat(sources, matcher2);
    assertEquals(2, sources.size());
  }

  @Test
  public void shouldResolveSpecs() throws Exception {
    Set<String> specs = projectDirScripResolver.getSpecs();
    Matcher<Iterable<? super String>> matcher1 = hasItem(endsWith("root/src/test/javascript/spec.js"));
    assertThat(specs, matcher1);
    assertEquals(1, specs.size());
  }

  @Test
  public void shouldResolveRelativeSpecs() throws Exception {
    Set<String> specs = projectDirScripResolver.getSpecsRelativePath();
    Matcher<Iterable<? super String>> matcher1 = hasItem(startsWith("src/test/javascript/spec.js"));
    assertThat(specs, matcher1);
    assertEquals(1, specs.size());
  }

  @Test
  @Ignore //TODO: Uhm, not sure if this should work?
  public void shouldResolvePreloads() throws Exception {
    ProjectDirScripResolver projectDirScripResolver = new ProjectDirScripResolver(root,
        new ScriptSearch(sourceFolder, ScansDirectory.DEFAULT_INCLUDES, Collections.<String>emptyList()),
        new ScriptSearch(testFolder, ScansDirectory.DEFAULT_INCLUDES, Arrays.asList(new String[]{"vendor/vendor.js"})),
        null);
    projectDirScripResolver.resolveScripts();
    Set<String> preloads = projectDirScripResolver.getPreloads();
    Matcher<Iterable<? super String>> matcher1 = hasItem(endsWith("vendor/vendor.js"));
    assertThat(preloads, matcher1);
    assertEquals(1, preloads.size());
  }

  @Test
  public void shouldResolveAllScripts() throws Exception {
    Set<String> sources = projectDirScripResolver.getAllScripts();
    Matcher<Iterable<? super String>> matcher1 = hasItem(endsWith("root/src/main/webapp/js/src.js"));
    assertThat(sources,matcher1);
	Matcher<Iterable<? super String>> matcher2 = hasItem(endsWith("root/src/main/webapp/js/lib/dep.js"));
    assertThat(sources, matcher2);
    Matcher<Iterable<? super String>> matcher3 = hasItem(endsWith("root/src/test/javascript/spec.js"));
    assertThat(sources, matcher3);
    assertEquals(3, sources.size());
  }

  @Test
  public void shouldResolveAllScriptsRelativePath() throws Exception {
    Set<String> sources = projectDirScripResolver.getAllScriptsRelativePath();
    Matcher<Iterable<? super String>> matcher1 = hasItem(startsWith("src/main/webapp/js/src.js"));
    assertThat(sources, matcher1);
    Matcher<Iterable<? super String>> matcher2 = hasItem(startsWith("src/main/webapp/js/lib/dep.js"));
    assertThat(sources, matcher2);
    Matcher<Iterable<? super String>> matcher3 = hasItem(startsWith("src/test/javascript/spec.js"));
    assertThat(sources, matcher3);
    assertEquals(3, sources.size());
  }

  @Test
  public void shouldReturnSourcesDirectory() throws Exception {
    assertThat(projectDirScripResolver.getSourceDirectory(), endsWith("root/src/main/webapp/js/"));
  }

  @Test
  public void shouldReturnSourcesDirectoryRelativePath() throws Exception {
    assertThat(projectDirScripResolver.getSourceDirectoryRelativePath(), startsWith("src/main/webapp/js"));
  }

  @Test
  public void shouldReturnSpecDirectory() throws Exception {
    assertThat(projectDirScripResolver.getSpecDirectoryPath(), endsWith("root/src/test/javascript/"));
  }

  @Test
  public void shouldReturnSpecDirectoryRelativePath() throws Exception {
    assertThat(projectDirScripResolver.getSpecDirectoryRelativePath(), startsWith("src/test/javascript"));
  }

  private File createFile(File root, String dir, String filename) throws IOException {
    File directory = new File(root, dir);
    directory.mkdirs();
    File newFile = new File(directory, filename);
    FileUtils.touch(newFile);
    return newFile;
  }
}
