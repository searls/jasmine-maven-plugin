package com.github.searls.jasmine;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.powermock.api.mockito.PowerMockito.whenNew;
import net.awired.jscoverage.instrumentation.JsInstrumentedSource;
import net.awired.jscoverage.instrumentation.JsInstrumentor;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.searls.jasmine.coffee.CompilesAllCoffeeInDirectory;
import com.github.searls.jasmine.io.DirectoryCopier;
import com.github.searls.jasmine.io.FileUtilsWrapper;
import com.github.searls.jasmine.io.ScansDirectory;
import com.github.searls.jasmine.model.ScriptSearch;


@RunWith(PowerMockRunner.class)
@PrepareForTest(ProcessResourcesMojo.class)
public class ProcessResourcesMojoTest {

	private static final String SRC_DIR_NAME = "blarh";
	
	@InjectMocks private ProcessResourcesMojo subject = new ProcessResourcesMojo();
	
	@Mock private DirectoryCopier directoryCopier;
	@Mock private CompilesAllCoffeeInDirectory compilesAllCoffeeInDirectory;
        @Mock private JsInstrumentor jsInstrumentor;
        @Mock private ScansDirectory scansDirectory;
        @Mock private FileUtilsWrapper fileUtilsWrapper;

	
	@Mock private File jasmineTargetDir;
	
	@Mock private File sourceDir;
	@Mock private File targetDir;
	@Mock private ScriptSearch sources;
	
	@Mock private Log log;
	
	@Before
	public void killLogging() {
		subject.setLog(log);
	}
	
	@Before
	public void isolateSubject() throws Exception {
		subject.jasmineTargetDir = jasmineTargetDir;
		subject.srcDirectoryName = SRC_DIR_NAME;
		subject.sources = sources;

		whenNew(File.class).withArguments(jasmineTargetDir,SRC_DIR_NAME).thenReturn(targetDir);
	}
	
	@Before
	public void stubSources() {
		when(sources.getDirectory()).thenReturn(sourceDir);
	}
	
	@Test
	public void basicLogging() throws IOException {
		subject.run();
		
		verify(log).info("Processing JavaScript Sources");
	}

	@Test
	public void whenDirectoryExistsCopy() throws IOException, MojoExecutionException, MojoFailureException {
		when(sourceDir.exists()).thenReturn(true);
		
		subject.run();
		
		verify(directoryCopier).copyDirectory(sources.getDirectory(), targetDir);
	}
	
	@Test
	public void whenDirectoryExistsCoffeeTime() throws IOException {
		when(sourceDir.exists()).thenReturn(true);
		
		subject.run();
		
		verify(compilesAllCoffeeInDirectory).compile(targetDir);
	}
	
	@Test
	public void whenDirectoryDoesNotExistDoNotDoStuff() throws IOException, MojoExecutionException, MojoFailureException {
		when(sources.getDirectory()).thenReturn(sourceDir);
		when(sourceDir.exists()).thenReturn(false);
		
		subject.run();
		
		verify(directoryCopier,never()).copyDirectory(any(File.class),any(File.class));
		verify(compilesAllCoffeeInDirectory,never()).compile(any(File.class));
		verify(log).warn(ProcessResourcesMojo.MISSING_DIR_WARNING);
	}
	
	@Test
	public void should_instrument_source_when_coverage_activated() throws Exception {
	    subject.coverage = true;
	    subject.instrumentedDirectoryName = "instrumented";
            List<String> filesFound = Arrays.asList("dir/genre.js");
            File sourceFile = Mockito.mock(File.class);
            File parent = Mockito.mock(File.class);
            File instrumentedFile = Mockito.mock(File.class);   
            when(instrumentedFile.getParentFile()).thenReturn(parent);
            JsInstrumentedSource instrumented = new JsInstrumentedSource();
            instrumented.setIntrumentedSource("instruemented source code");
            when(sourceDir.exists()).thenReturn(true);
            when(scansDirectory.scan(targetDir, ScansDirectory.DEFAULT_INCLUDES, new ArrayList<String>())).thenReturn(filesFound);
            when(jsInstrumentor.instrument(filesFound.get(0), "source code")).thenReturn(instrumented );
            when(fileUtilsWrapper.readFileToString(sourceFile)).thenReturn("source code");
            whenNew(File.class).withArguments(jasmineTargetDir, SRC_DIR_NAME).thenReturn(targetDir);
            whenNew(File.class).withArguments(subject.jasmineTargetDir, "instrumented/dir/genre.js").thenReturn(instrumentedFile);
            whenNew(File.class).withArguments(targetDir, filesFound.get(0)).thenReturn(sourceFile);
            
	    subject.run();
	    
            verify(fileUtilsWrapper).forceMkdir(parent);
            verify(fileUtilsWrapper).writeStringToFile(instrumentedFile, "instruemented source code", "UTF-8");
	}
	
}
