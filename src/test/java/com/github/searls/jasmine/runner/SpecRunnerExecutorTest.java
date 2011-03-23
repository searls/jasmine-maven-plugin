package com.github.searls.jasmine.runner;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.maven.plugin.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.searls.jasmine.io.FileUtilsWrapper;
import com.github.searls.jasmine.io.IOUtilsWrapper;
import com.github.searls.jasmine.model.JasmineResult;


@RunWith(MockitoJUnitRunner.class)
public class SpecRunnerExecutorTest {

	private static final String BUILD_REPORT_JS_CONTENTS = "var jasmineMavenPlugin = {printReport: function(){ return 'pants\\nkaka'; }};";
	private static final String JUNIT_RESULTS = "var junitXmlReporter = { report: function(reporter) { return '<xml/>'; }};";
	private static final String BROWSER_VERSION = "INTERNET_EXPLORER_8";
	
	@InjectMocks private SpecRunnerExecutor subject = new SpecRunnerExecutor();
	
	@Mock private IOUtilsWrapper ioUtilsWrapper;
	@Mock private FileUtilsWrapper fileUtilsWrapper;
	
	@Mock private File file;
	@Mock private Log log;
	
	private URL resource = getClass().getResource("/example_nested_specrunner.html");
	
	@Before
	public void stubResourceStreams() throws IOException {
		when(ioUtilsWrapper.toString(isA(String.class))).thenReturn(BUILD_REPORT_JS_CONTENTS,JUNIT_RESULTS);
	}
	
	@Test
	public void shouldFindSpecsInResults() throws Exception {
		JasmineResult result = subject.execute(resource, file, BROWSER_VERSION, 300, false, log, null);
		
		assertThat(result,is(not(nullValue())));
		assertThat(result.getDescription(),containsString("kaka"));
		assertThat(result.getDetails(),containsString("pants"));
		assertThat(result.didPass(),is(false));
	}
	
	@Test
	public void shouldExportJUnitResults() throws Exception {
		subject.execute(resource, file, BROWSER_VERSION, 300, false, log, null);
		
		verify(fileUtilsWrapper).writeStringToFile(file, "<xml/>", "UTF-8");
	}
}