package com.github.searls.jasmine.io.scripts;

import static org.hamcrest.Matchers.endsWith;
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
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Assert;
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
		this.root = this.tempFolder.newFolder("root");
		this.sourceFolder = this.createFile(this.root, "src/main/webapp/js", "src.js").getParentFile();
		this.createFile(this.root, "src/main/webapp/js/lib", "dep.js");
		this.createFile(this.root, "src/main/webapp/js/vendor", "vendor.js");
		this.testFolder = this.createFile(this.root, "src/test/javascript", "spec.js").getParentFile();

		this.initScriptResolver();

	}

	private void initScriptResolver() throws IOException {
		MavenProject project = mock(MavenProject.class);
		when(project.getBasedir()).thenReturn(this.root);
		AbstractJasmineMojo config = mock(AbstractJasmineMojo.class);
		when(config.getMavenProject()).thenReturn(project);
		when(config.getPreloadSources()).thenReturn(null);
		when(config.getSrcDirectoryName()).thenReturn("src");
		when(config.getSpecDirectoryName()).thenReturn("spec");
		when(config.getSources()).thenReturn(new ScriptSearch(this.sourceFolder, ScansDirectory.DEFAULT_INCLUDES, Arrays.asList(this.excludes)));
		when(config.getSpecs()).thenReturn(new ScriptSearch(this.testFolder, ScansDirectory.DEFAULT_INCLUDES, Collections.<String>emptyList()));
		this.projectDirScripResolver = new ProjectDirScripResolver(config);
		this.projectDirScripResolver.resolveScripts();
	}

	@Test
	public void shouldResolveSources() throws Exception {
		Set<String> sources = this.projectDirScripResolver.getSources();
		assertHasItemStartingWith(sources, "src/main/webapp/js/src.js");
		assertHasItemStartingWith(sources, "src/main/webapp/js/lib/dep.js");
		assertEquals(2, sources.size());
	}

	@Test
	public void shouldResolveSpecs() throws Exception {

		Set<String> specs = this.projectDirScripResolver.getSpecs();
		assertHasItemStartingWith(specs, "src/test/javascript/spec.js");
		assertEquals(1, specs.size());
	}

	@Test
	@Ignore //TODO: Uhm, not sure if this should work?
	public void shouldResolvePreloads() throws Exception {
		MavenProject project = mock(MavenProject.class);
		when(project.getBasedir()).thenReturn(this.root);
		AbstractJasmineMojo config = mock(AbstractJasmineMojo.class);
		when(config.getMavenProject()).thenReturn(project);
		when(config.getPreloadSources()).thenReturn(null);
		when(config.getSrcDirectoryName()).thenReturn("src");
		when(config.getSpecDirectoryName()).thenReturn("spec");
		when(config.getSources()).thenReturn(new ScriptSearch(this.sourceFolder, ScansDirectory.DEFAULT_INCLUDES, Collections.<String>emptyList()));
		when(config.getSpecs()).thenReturn(new ScriptSearch(this.testFolder, ScansDirectory.DEFAULT_INCLUDES, Arrays.asList(new String[]{"vendor/vendor.js"})));
		ProjectDirScripResolver projectDirScripResolver = new ProjectDirScripResolver(config);
		projectDirScripResolver.resolveScripts();
		Set<String> preloads = projectDirScripResolver.getPreloads();
		assertHasItemEndingWith(preloads, "vendor/vendor.js");
		assertEquals(1, preloads.size());
	}

	@Test
	public void shouldResolveAllScripts() throws Exception {
		Set<String> sources = this.projectDirScripResolver.getAllScripts();
		assertHasItemStartingWith(sources, "src/main/webapp/js/src.js");
		assertHasItemStartingWith(sources, "src/main/webapp/js/lib/dep.js");
		assertHasItemStartingWith(sources, "src/test/javascript/spec.js");
		assertEquals(3, sources.size());
	}

	@Test
	public void shouldReturnSourcesDirectory() throws Exception {
		assertThat(this.projectDirScripResolver.getSourceDirectory(), Matchers.endsWith("src"));
	}

	@Test
	public void shouldReturnSpecDirectory() throws Exception {
		assertThat(this.projectDirScripResolver.getSpecDirectory(), Matchers.endsWith("spec"));
	}

	private File createFile(File root, String dir, String filename) throws IOException {
		File directory = new File(root, dir);
		directory.mkdirs();
		File newFile = new File(directory, filename);
		FileUtils.touch(newFile);
		return newFile;
	}

	private static void assertHasItemEndingWith(Set<String> set, String endWith) {
		Matcher<Iterable<? super String>> hasItem = Matchers.hasItem(endsWith(endWith));
		Assert.assertThat(set, hasItem);
	}

	private static void assertHasItemStartingWith(Set<String> set, String startWith) {
		Matcher<Iterable<? super String>> hasItem = Matchers.hasItem(startsWith(startWith));
		Assert.assertThat(set, hasItem);
	}
}
