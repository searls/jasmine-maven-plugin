package com.github.searls.jasmine.mojo;

import static com.github.searls.jasmine.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.resource.ResourceManager;
import org.codehaus.plexus.resource.loader.FileResourceCreationException;
import org.codehaus.plexus.resource.loader.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.searls.jasmine.exception.StringifiesStackTraces;

@RunWith(MockitoJUnitRunner.class)
public class AbstractJasmineMojoTest {

	private static final String ENCODING = "UTF-8";
	private static final String SCRIPT_LOADER_PATH = "scriptloaderpath";
	private static final String PARENT_PROJECT_PATH = "/parent/project/path";

	@InjectMocks @Spy
	private final AbstractJasmineMojo subject = new AbstractJasmineMojo() {
		@Override
		public void run() throws Exception {}
	};

	@Mock private final StringifiesStackTraces stringifiesStackTraces = new StringifiesStackTraces();

	@Rule public ExpectedException expectedException = ExpectedException.none();

	private static final String CUSTOM_RUNNER_CONFIG = "customRunnerConfiguration";

	private static final String CUSTOM_RUNNER_TEMPLATE = "customRunnerTemplate";

	@Mock
	private File baseDir;

	@Mock
	private MavenProject mavenProject;

	@Mock
	private File projectFile;

	@Mock
	private File parentProjectFile;

	@Mock
	private ResourceManager locator;

	@Before
	public void before() {
		this.subject.sourceEncoding = ENCODING;
		this.subject.scriptLoaderPath = null;
		this.subject.locator = this.locator;
	}

	@Test
	public void executeStringifiesStackTraces() throws Exception {
		String expected = "panda";
		this.expectedException.expectMessage(expected);
		Exception e = new Exception();
		when(this.stringifiesStackTraces.stringify(e)).thenReturn(expected);
		doThrow(e).when(this.subject).run();

		this.subject.execute();

		verify(this.stringifiesStackTraces).stringify(e);
	}

	@Test
	public void rethrowsMojoFailureExceptions() throws Exception {
		String expected = "panda";
		this.expectedException.expect(MojoFailureException.class);
		this.expectedException.expectMessage(expected);
		MojoFailureException e = new MojoFailureException(expected);
		doThrow(e).when(this.subject).run();

		this.subject.execute();
	}

	@Test
	public void setsSourceIncludes() throws Exception {
		this.subject.execute();

		assertThat(this.subject.sources.getIncludes(),hasItem("**"+File.separator+"*.js"));
	}

	@Test
	public void setsSourceExcludes() throws Exception {
		this.subject.execute();

		assertThat(this.subject.sources.getExcludes(),is(empty()));
	}

	@Test
	public void setsSpecIncludes() throws Exception {
		this.subject.execute();

		assertThat(this.subject.specs.getIncludes(),hasItem("**"+File.separator+"*.js"));
	}

	@Test
	public void setsSpecExcludes() throws Exception {
		this.subject.execute();

		assertThat(this.subject.specs.getExcludes(),is(empty()));
	}

	@Test
	public void testGetSourceEncoding() {
		assertThat(this.subject.getSourceEncoding(), is(ENCODING));
	}

	@Test
	public void testGetCustomRunnerConfiguration() throws ResourceNotFoundException, MojoExecutionException, MojoFailureException, FileResourceCreationException {
		File customRunnerConfiguration = mock(File.class);
		this.subject.customRunnerConfiguration = CUSTOM_RUNNER_CONFIG;
		when(this.mavenProject.getFile()).thenReturn(this.projectFile);
		when(this.projectFile.getParentFile()).thenReturn(this.parentProjectFile);
		when(this.parentProjectFile.getAbsolutePath()).thenReturn(PARENT_PROJECT_PATH);
		when(this.locator.getResourceAsFile(CUSTOM_RUNNER_CONFIG)).thenReturn(customRunnerConfiguration);
		this.subject.execute();
		assertThat(this.subject.getCustomRunnerConfiguration(), is(customRunnerConfiguration));
	}

	@Test
	public void testGetCustomRunnerTemplate() throws ResourceNotFoundException, MojoExecutionException, MojoFailureException, FileResourceCreationException {
		File customRunnerTemplate = mock(File.class);
		this.subject.customRunnerTemplate = CUSTOM_RUNNER_TEMPLATE;
		when(this.mavenProject.getFile()).thenReturn(this.projectFile);
		when(this.projectFile.getParentFile()).thenReturn(this.parentProjectFile);
		when(this.parentProjectFile.getAbsolutePath()).thenReturn(PARENT_PROJECT_PATH);
		when(this.locator.getResourceAsFile(CUSTOM_RUNNER_TEMPLATE)).thenReturn(customRunnerTemplate);
		this.subject.execute();
		assertThat(this.subject.getCustomRunnerTemplate(), is(customRunnerTemplate));
	}

	@Test(expected=MojoExecutionException.class)
	public void testGetCustomRunnerTemplateNotFound() throws ResourceNotFoundException, MojoExecutionException, MojoFailureException, FileResourceCreationException {
		this.subject.customRunnerTemplate = CUSTOM_RUNNER_TEMPLATE;
		when(this.mavenProject.getFile()).thenReturn(this.projectFile);
		when(this.projectFile.getParentFile()).thenReturn(this.parentProjectFile);
		when(this.parentProjectFile.getAbsolutePath()).thenReturn(PARENT_PROJECT_PATH);
		when(this.locator.getResourceAsFile(CUSTOM_RUNNER_TEMPLATE)).thenThrow(new FileResourceCreationException(CUSTOM_RUNNER_TEMPLATE));
		this.subject.execute();
	}

	@Test
	public void testGetBaseDir() {
		when(this.mavenProject.getBasedir()).thenReturn(this.baseDir);
		assertThat(this.subject.getBasedir(),is(this.baseDir));
	}

	@Test
	public void testGetScriptLoaderPath() {
		this.subject.scriptLoaderPath = SCRIPT_LOADER_PATH;
		assertThat(this.subject.getScriptLoaderPath(),is(SCRIPT_LOADER_PATH));
	}

	@Test
	public void testGetMavenProject() {
		assertThat(this.subject.getMavenProject(), is(this.mavenProject));
	}

	@Test
	public void testGetAutoRefreshInterval() {
		this.subject.autoRefreshInterval = 5;
		assertThat(this.subject.getAutoRefreshInterval(), is(5));
	}
}
