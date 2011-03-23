package com.github.searls.jasmine.runner;

import static com.github.searls.jasmine.Matchers.*;
import static com.github.searls.jasmine.runner.SpecRunnerHtmlGenerator.*;
import static java.util.Arrays.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.logging.LogFactory;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.gargoylesoftware.htmlunit.IncorrectnessListener;
import com.gargoylesoftware.htmlunit.MockWebConnection;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlMeta;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.github.searls.jasmine.io.FileUtilsWrapper;
import com.github.searls.jasmine.io.IOUtilsWrapper;

@RunWith(MockitoJUnitRunner.class)
public class SpecRunnerHtmlGeneratorPseudoIntegrationTest {

	private static final String HTML5_DOCTYPE = "<!DOCTYPE html>";
	private static final String SOURCE_ENCODING = "as9du20asd xanadu";
	private static final Set<String> SCRIPTS = new LinkedHashSet<String>(asList("A"));
	
	static {
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
	}

	@InjectMocks
	private SpecRunnerHtmlGenerator subject = new SpecRunnerHtmlGenerator(SCRIPTS,SOURCE_ENCODING);

	@Mock private FileUtilsWrapper fileUtilsWrapper;
	@Spy private IOUtilsWrapper ioUtilsWrapper = new IOUtilsWrapper();
	@Rule public ExpectedException expectedException = ExpectedException.none();
	
	@Test
	public void shouldBuildBasicHtmlWhenNoDependenciesAreProvided() {
		String html = subject.generate(ReporterType.TrivialReporter, null);

		assertThat(html, containsString("<html>"));
		assertThat(html, containsString("</html>"));
	}

	@Test
	public void shouldPutInADocTypeWhenNoDependenciesAreProvided() throws Exception {
		String html = subject.generate(ReporterType.TrivialReporter, null);

		assertThat(html, containsString(HTML5_DOCTYPE));
		assertThat(getPage(html).getDoctype().getName(), is("html"));
	}

	@Test
	public void shouldAssignSpecifiedSourceEncoding() throws Exception {
		String html = subject.generate(ReporterType.TrivialReporter, null);

		HtmlMeta contentType = getPage(html).getFirstByXPath("//meta");
		assertThat(contentType.getContentAttribute(), is("text/html; charset=" + SOURCE_ENCODING));
	}

	@Test
	public void shouldDefaultSourceEncodingWhenUnspecified() throws Exception {
		subject = new SpecRunnerHtmlGenerator(SCRIPTS, "");

		String html = subject.generate(ReporterType.TrivialReporter, null);

		HtmlMeta contentType = getPage(html).getFirstByXPath("//meta");
		assertThat(contentType.getContentAttribute(), is("text/html; charset=" + SpecRunnerHtmlGenerator.DEFAULT_SOURCE_ENCODING));
	}

	@Test
	public void populatesJasmineSource() throws Exception {
		String expected = "javascript()";
		when(ioUtilsWrapper.toString(JASMINE_JS)).thenReturn(expected);

		String html = subject.generate(ReporterType.TrivialReporter, null);

		assertThat(html, containsScriptTagWith(expected));
	}

	@Test
	public void populatesJasmineHtmlSource() throws Exception {
		String expected = "javascript()";
		when(ioUtilsWrapper.toString(JASMINE_HTML_JS)).thenReturn(expected);

		String html = subject.generate(ReporterType.TrivialReporter, null);

		assertThat(html, containsScriptTagWith(expected));
	}
	
	@Test
	public void shouldPopulateCSSIntoHtmlWhenProvided() throws Exception {
		String expected = "h1 { background-color: awesome}";
		when(ioUtilsWrapper.toString(JASMINE_CSS)).thenReturn(expected);

		String html = subject.generate(ReporterType.TrivialReporter, null);

		assertThat(html, containsStyleTagWith(expected));
	}

	@Test
	public void shouldNotReadDefaultTemplateWhenOneIsProvided() throws IOException {
		File expected = mock(File.class);

		subject.generate(ReporterType.TrivialReporter, expected);

		verify(ioUtilsWrapper, never()).toString(DEFAULT_RUNNER_HTML_TEMPLATE_FILE);
	}

	@Test
	public void shouldReadCustomTemplateWhenOneIsProvided() throws IOException {
		File expected = mock(File.class);

		subject.generate(ReporterType.TrivialReporter, expected);

		verify(fileUtilsWrapper).readFileToString(expected);
	}
	
	@Test
	public void throwsRuntimeWhenIOExceptionIsEncountered() throws IOException {
		when(ioUtilsWrapper.toString(anyString())).thenThrow(new IOException("Am fail"));
		expectedException.expect(RuntimeException.class);
		expectedException.expectMessage("Failed to load files for dependencies, sources, or a custom runner");
		
		subject.generate(ReporterType.TrivialReporter, null);
	}
	
	@Test
	public void containsScriptTagOfSource() {
		String expected = SCRIPTS.iterator().next();

		String html = subject.generate(ReporterType.TrivialReporter, null);

		assertThat(html, containsScriptTagWithSource(expected));
	}

	private HtmlPage getPage(String html) throws Exception {
		MockWebConnection webConnection = new MockWebConnection();
		webConnection.setDefaultResponse(html);
		WebClient webClient = new WebClient();
		webClient.setWebConnection(webConnection);
		webClient.setThrowExceptionOnScriptError(false);
		webClient.setIncorrectnessListener(new IncorrectnessListener() {
			public void notify(String arg0, Object arg1) {
			}
		});
		return webClient.getPage("http://blah");
	}
}
