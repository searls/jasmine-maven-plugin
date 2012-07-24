package com.github.searls.jasmine.runner;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import net.awired.jscoverage.result.JsFileResult;
import net.awired.jscoverage.result.JsRunResult;
import net.awired.jscoverage.result.LcovWriter;
import org.apache.maven.plugin.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.powermock.modules.junit4.PowerMockRunner;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.github.searls.jasmine.io.FileUtilsWrapper;
import com.github.searls.jasmine.io.IOUtilsWrapper;
import com.github.searls.jasmine.model.JasmineResult;
import com.google.common.collect.ImmutableMap;


@RunWith(PowerMockRunner.class)
public class SpecRunnerExecutorTest {

	private static final String JSCOV = "var JSCOV = {storeCurrentRunResult: function(){}, getStoredRunResult: function(){ return [{'name' : 'genre', fileResults : [{filename : 'genre.js', lineCovered : {1 : 42}}]}]; }};";
    private static final String BUILD_REPORT_JS_CONTENTS = "var jasmineMavenPlugin = {printReport: function(){ return 'pants\\nkaka'; }};";
	private static final String JUNIT_RESULTS = "var junitXmlReporter = { report: function(reporter) { return '<xml/>'; }};";
	private static HtmlUnitDriver driver;
	
	@InjectMocks private SpecRunnerExecutor subject = new SpecRunnerExecutor();
	
	@Mock private IOUtilsWrapper ioUtilsWrapper;
	@Mock private FileUtilsWrapper fileUtilsWrapper;
	
	@Mock private File file;
	@Mock private Log log;
	@Mock private LcovWriter lcovWriter;
	
	@Captor
	ArgumentCaptor<JsRunResult> runResultCaptor = ArgumentCaptor.forClass(JsRunResult.class);
	
	private URL resource = getClass().getResource("/example_nested_specrunner.html");
	
	@Before
	public void stubResourceStreams() throws IOException {
		when(ioUtilsWrapper.toString(isA(String.class))).thenReturn(BUILD_REPORT_JS_CONTENTS,JUNIT_RESULTS);
		driver = new HtmlUnitDriver(BrowserVersion.INTERNET_EXPLORER_8);
		driver.setJavascriptEnabled(true);
	}
	
	@Test
	public void shouldFindSpecsInResults() throws Exception {
		JasmineResult result = subject.execute(resource, file, driver, 300, false, log, null, false, null);
		
		assertThat(result,is(not(nullValue())));
		assertThat(result.getDescription(),containsString("kaka"));
		assertThat(result.getDetails(),containsString("pants"));
		assertThat(result.didPass(),is(false));
	}

        @Test
        public void should_build_coverage_report() throws Exception {
            File coverageReportFile = mock(File.class);
            when(coverageReportFile.getPath()).thenReturn("there.dat");
            subject.jsGetCoverageScript = JSCOV + subject.jsGetCoverageScript;
            
            subject.execute(resource, file, driver, 300, false, log, null, true, coverageReportFile);

            verify(lcovWriter).write(eq(coverageReportFile), runResultCaptor.capture());
            assertEquals("genre", runResultCaptor.getValue().getName());
            List<JsFileResult> fileResults = runResultCaptor.getValue().getFileResults();
            assertEquals("genre.js", fileResults.get(0).getFilename());
            assertEquals(ImmutableMap.of(1, 42), fileResults.get(0).getLineCovered());
        }
	
	@Test
	public void shouldExportJUnitResults() throws Exception {
		subject.execute(resource, file, driver, 300, false, log, null, false, null);
		
		verify(fileUtilsWrapper).writeStringToFile(file, "<xml/>", "UTF-8");
	}
}