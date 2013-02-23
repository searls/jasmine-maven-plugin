package com.github.searls.jasmine.runner;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.github.searls.jasmine.io.IOUtilsWrapper;
import com.github.searls.jasmine.model.JasmineResult;

@RunWith(PowerMockRunner.class)
@PrepareForTest(FileUtils.class)
public class SpecRunnerExecutorTest {

	private static final String BUILD_REPORT_JS_CONTENTS = "var jasmineMavenPlugin = {printReport: function(){ return 'pants\\nkaka'; }};";
	private static final String JUNIT_RESULTS = "var junitXmlReporter = { report: function(reporter) { return '<xml/>'; }};";
	private static HtmlUnitDriver driver;

	@InjectMocks private final SpecRunnerExecutor subject = new SpecRunnerExecutor();

	@Mock private IOUtilsWrapper ioUtilsWrapper;

	@Mock private File file;
	@Mock private Log log;

	private final URL resource = this.getClass().getResource("/example_nested_specrunner.html");

	@Before
	public void stubResourceStreams() throws IOException {
		spy(FileUtils.class);

		when(this.ioUtilsWrapper.toString(isA(String.class))).thenReturn(BUILD_REPORT_JS_CONTENTS,JUNIT_RESULTS);
		driver = new HtmlUnitDriver(BrowserVersion.INTERNET_EXPLORER_8);
		driver.setJavascriptEnabled(true);
	}

	@Test
	public void shouldFindSpecsInResults() throws Exception {
		doNothing().when(FileUtils.class);
		FileUtils.writeStringToFile(this.file, "<xml/>", "UTF-8");

		JasmineResult result = this.subject.execute(this.resource, this.file, driver, 300, false, this.log, null);

		assertThat(result,is(not(nullValue())));
		assertThat(result.getDescription(),containsString("kaka"));
		assertThat(result.getDetails(),containsString("pants"));
		assertThat(result.didPass(),is(false));

		verifyStatic();
		FileUtils.writeStringToFile(this.file, "<xml/>", "UTF-8");
	}

	@Test
	public void shouldExportJUnitResults() throws Exception {
		doNothing().when(FileUtils.class);
		FileUtils.writeStringToFile(this.file, "<xml/>", "UTF-8");

		this.subject.execute(this.resource, this.file, driver, 300, false, this.log, null);

		verifyStatic();
		FileUtils.writeStringToFile(this.file, "<xml/>", "UTF-8");
	}
}