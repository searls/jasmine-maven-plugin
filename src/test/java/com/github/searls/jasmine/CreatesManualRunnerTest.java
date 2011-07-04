package com.github.searls.jasmine;

import static java.util.Arrays.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.searls.jasmine.io.FileUtilsWrapper;
import com.github.searls.jasmine.io.scripts.RelativizesASetOfScripts;
import com.github.searls.jasmine.io.scripts.ResolvesCompleteListOfScriptLocations;
import com.github.searls.jasmine.model.ScriptSearch;
import com.github.searls.jasmine.runner.ReporterType;
import com.github.searls.jasmine.runner.SpecRunnerHtmlGenerator;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CreatesManualRunner.class)
public class CreatesManualRunnerTest {

	private static final String SOURCE_DIR = "sauces";
	private static final String SPEC_DIR = "specks";
	private static final String SOURCE_ENCODING = "UTF-Pandaz";
	private static final String MANUAL_RUNNER_NAME = "Jerry. That's a nice name.";
	private static final List<String> PRELOAD_SOURCES = new ArrayList<String>();
	private static final Set<String> RESOLVED_SOURCES = new HashSet<String>(asList("resolved"));
	private static final Set<String> RELATIVIZED_SOURCES = new HashSet<String>(asList("relativized"));
	
	private AbstractJasmineMojo config = new AbstractJasmineMojo(){ public void run() throws Exception {}};
	
	@InjectMocks private CreatesManualRunner subject = new CreatesManualRunner(config);
	
	@Mock private ResolvesCompleteListOfScriptLocations resolvesCompleteListOfScriptLocations;
	@Mock private RelativizesASetOfScripts relativizesASetOfScripts;
	@Mock private FileUtilsWrapper fileUtilsWrapper;
	@Mock private SpecRunnerHtmlGenerator specRunnerHtmlGenerator;
	
	@Mock private Log log;

	@Mock private ScriptSearch sources;
	@Mock private ScriptSearch specs;
	
	@Mock private File sourceDirectory;
	@Mock private File specDirectory;
	@Mock private File runnerDestination;
	@Mock private File jasmineTargetDir;
	@Mock private File customRunnerTemplate;
	
	@Before
	public void fakeLogging() {
		config.setLog(log);
	}
	
	@Before
	public void fakeOutDirectories() {
		when(sources.getDirectory()).thenReturn(sourceDirectory);
		when(sourceDirectory.getAbsolutePath()).thenReturn(SOURCE_DIR);
		when(sourceDirectory.exists()).thenReturn(true);

		when(specs.getDirectory()).thenReturn(specDirectory);
		when(specDirectory.getAbsolutePath()).thenReturn(SPEC_DIR);
		when(specDirectory.exists()).thenReturn(true);
	}
	
	@Before
	public void setupMojoProps() {
		config.sources = sources;
		config.specs = specs;
		config.manualSpecRunnerHtmlFileName = MANUAL_RUNNER_NAME;
		config.jasmineTargetDir = jasmineTargetDir;
		config.sourceEncoding = SOURCE_ENCODING;
		config.customRunnerTemplate = customRunnerTemplate;
		config.preloadSources = PRELOAD_SOURCES;
	}
	
	@Before
	public void stubConstructionOfExistingRunnerFile() throws Exception {
		whenNew(File.class).withParameterTypes(File.class, String.class).withArguments(jasmineTargetDir,MANUAL_RUNNER_NAME).thenReturn(runnerDestination);
	}
	
	@Before
	public void stubResolutionOfTestScripts() throws IOException {
		when(resolvesCompleteListOfScriptLocations.resolve(sources, specs, PRELOAD_SOURCES)).thenReturn(RESOLVED_SOURCES);
	}
	
	@Before
	public void stubRelativizationOfTestScripts() throws IOException {
		when(relativizesASetOfScripts.relativize(jasmineTargetDir,RESOLVED_SOURCES)).thenReturn(RELATIVIZED_SOURCES);
	}
	
	@Before
	public void stubConstructionOfHtmlGenerator() throws Exception {
		whenNew(SpecRunnerHtmlGenerator.class).withArguments(RELATIVIZED_SOURCES, SOURCE_ENCODING).thenReturn(specRunnerHtmlGenerator);
	}
	
	@Test
	public void whenRunnerDoesNotExistThenCreateNewRunner() throws Exception {
		String expected = "I'm a new spec runner yay!";
        when(runnerDestination.exists()).thenReturn(false);
        when(specRunnerHtmlGenerator.generate(ReporterType.TrivialReporter, customRunnerTemplate)).thenReturn(expected);
        
        subject.create();
        
        verify(fileUtilsWrapper).writeStringToFile(runnerDestination, expected, SOURCE_ENCODING);
	}

	@Test
	public void whenRunnerExistsAndDiffersThenWriteNewOne() throws IOException {
		String expected = "HTRML!!!!111!111oneoneone";
		when(runnerDestination.exists()).thenReturn(true);
		when(fileUtilsWrapper.readFileToString(runnerDestination)).thenReturn("old and crusty runner");
		when(specRunnerHtmlGenerator.generate(ReporterType.TrivialReporter, customRunnerTemplate)).thenReturn(expected);
		
		subject.create();
		
		verify(fileUtilsWrapper).writeStringToFile(runnerDestination, expected, SOURCE_ENCODING);
	}
	
	@Test
	public void whenRunnerExistsAndIsSameThenDoNothing() throws IOException {
		String existing = "HTRML!!!!111!111oneoneone";
		when(runnerDestination.exists()).thenReturn(true);
		when(fileUtilsWrapper.readFileToString(runnerDestination)).thenReturn(existing);
		when(specRunnerHtmlGenerator.generate(ReporterType.TrivialReporter, customRunnerTemplate)).thenReturn(existing);
		
		subject.create();
		
		neverWriteAFile();
		verify(log).info("Skipping spec runner generation, because an identical spec runner already exists.");
	}
	
	@Test
	public void whenExistingRunnerFailsToLoadThenWriteNewOne() throws IOException {
		String expected = "HTRML!!!!111!111oneoneone";
		when(runnerDestination.exists()).thenReturn(true);
		when(fileUtilsWrapper.readFileToString(runnerDestination)).thenThrow(new IOException());
		when(specRunnerHtmlGenerator.generate(ReporterType.TrivialReporter, customRunnerTemplate)).thenReturn(expected);
		
		subject.create();
		
		verify(fileUtilsWrapper).writeStringToFile(runnerDestination, expected, SOURCE_ENCODING);
		verify(log).warn("An error occurred while trying to open an existing manual spec runner. Continuing.");
	}

	private void neverWriteAFile() throws IOException {
		verify(fileUtilsWrapper, never()).writeStringToFile(any(File.class), anyString(), anyString());
	}
}
