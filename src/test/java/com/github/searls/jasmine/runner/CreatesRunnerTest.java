package com.github.searls.jasmine.runner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.searls.jasmine.config.JasmineConfiguration;
import com.github.searls.jasmine.io.scripts.ScriptResolver;
import com.github.searls.jasmine.model.ScriptSearch;
import com.github.searls.jasmine.mojo.AbstractJasmineMojo;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CreatesRunner.class,FileUtils.class})
public class CreatesRunnerTest {

	private static final String SOURCE_DIR = "sauces";
	private static final String SPEC_DIR = "specks";
	private static final String SOURCE_ENCODING = "UTF-Pandaz";
	private static final String MANUAL_RUNNER_NAME = "Jerry. That's a nice name.";
	public static final String SPEC_RUNNER_GENERATOR = "DEFAULT";

	@Mock private DefaultSpecRunnerHtmlGenerator specRunnerHtmlGenerator;

	@Mock private Log log;

	@Mock private ScriptSearch sources;
	@Mock private ScriptSearch specs;

	@Mock private File sourceDirectory;
	@Mock private File specDirectory;
	@Mock private File runnerDestination;
	@Mock private File jasmineTargetDir;

	@Mock private SpecRunnerHtmlGeneratorFactory specRunnerHtmlGeneratorFactory;

	@Mock private JasmineConfiguration config;

	private CreatesRunner subject;

	@Before
	public void before() {
		mockStatic(FileUtils.class);

		when(this.config.getSources()).thenReturn(this.sources);
		when(this.config.getSpecs()).thenReturn(this.specs);
		when(this.config.getSourceEncoding()).thenReturn(SOURCE_ENCODING);
		when(this.config.getJasmineTargetDir()).thenReturn(this.jasmineTargetDir);

		this.subject = new CreatesRunner(this.config,this.log,MANUAL_RUNNER_NAME,ReporterType.HtmlReporter);
	}

	@Before
	public void fakeOutDirectories() {
		when(this.sources.getDirectory()).thenReturn(this.sourceDirectory);
		when(this.sourceDirectory.getAbsolutePath()).thenReturn(SOURCE_DIR);
		when(this.sourceDirectory.exists()).thenReturn(true);

		when(this.specs.getDirectory()).thenReturn(this.specDirectory);
		when(this.specDirectory.getAbsolutePath()).thenReturn(SPEC_DIR);
		when(this.specDirectory.exists()).thenReturn(true);
	}

	@Before
	public void stubConstructionOfExistingRunnerFile() throws Exception {
		whenNew(File.class).withParameterTypes(File.class, String.class).withArguments(this.jasmineTargetDir,MANUAL_RUNNER_NAME).thenReturn(this.runnerDestination);
	}

	@Before
	public void stubConstructionOfHtmlGenerator() throws Exception {
		whenNew(SpecRunnerHtmlGeneratorFactory.class).withNoArguments().thenReturn(this.specRunnerHtmlGeneratorFactory);
		when(this.specRunnerHtmlGeneratorFactory.create(any(ReporterType.class), any(AbstractJasmineMojo.class), any(ScriptResolver.class))).thenReturn(this.specRunnerHtmlGenerator);
	}

	@Test
	public void whenRunnerDoesNotExistThenCreateNewRunner() throws Exception {
		String expected = "I'm a new spec runner yay!";
		when(this.runnerDestination.exists()).thenReturn(false);
		when(this.specRunnerHtmlGenerator.generate()).thenReturn(expected);

		this.subject.create();

		verifyStatic();
		FileUtils.writeStringToFile(this.runnerDestination, expected, SOURCE_ENCODING);
	}

	@Test
	public void whenRunnerExistsAndDiffersThenWriteNewOne() throws IOException {
		String expected = "HTRML!!!!111!111oneoneone";
		when(this.runnerDestination.exists()).thenReturn(true);
		when(FileUtils.readFileToString(this.runnerDestination)).thenReturn("old and crusty runner");
		when(this.specRunnerHtmlGenerator.generate()).thenReturn(expected);

		this.subject.create();

		verifyStatic();
		FileUtils.writeStringToFile(this.runnerDestination, expected, SOURCE_ENCODING);
	}

	@Test
	public void whenRunnerExistsAndIsSameThenDoNothing() throws IOException {
		String existing = "HTRML!!!!111!111oneoneone";
		when(this.runnerDestination.exists()).thenReturn(true);
		when(FileUtils.readFileToString(this.runnerDestination)).thenReturn(existing);
		when(this.specRunnerHtmlGenerator.generate()).thenReturn(existing);

		this.subject.create();

		this.neverWriteAFile();
		verify(this.log).info("Skipping spec runner generation, because an identical spec runner already exists.");
	}

	@Test
	public void whenExistingRunnerFailsToLoadThenWriteNewOne() throws IOException {
		String expected = "HTRML!!!!111!111oneoneone";
		when(this.runnerDestination.exists()).thenReturn(true);
		when(FileUtils.readFileToString(this.runnerDestination)).thenThrow(new IOException());
		when(this.specRunnerHtmlGenerator.generate()).thenReturn(expected);

		this.subject.create();

		verifyStatic();FileUtils.writeStringToFile(this.runnerDestination, expected, SOURCE_ENCODING);
		verify(this.log).warn("An error occurred while trying to open an existing manual spec runner. Continuing.");
	}

	private void neverWriteAFile() throws IOException {
		verifyStatic(never());
		FileUtils.writeStringToFile(any(File.class), anyString(), anyString());
	}
}
