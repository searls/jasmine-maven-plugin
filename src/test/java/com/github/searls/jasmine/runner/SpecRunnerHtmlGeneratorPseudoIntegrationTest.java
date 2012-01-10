package com.github.searls.jasmine.runner;

import static com.github.searls.jasmine.Matchers.*;
import static com.github.searls.jasmine.runner.DefaultSpecRunnerHtmlGenerator.*;
import static java.util.Arrays.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.gargoylesoftware.htmlunit.IncorrectnessListener;
import com.gargoylesoftware.htmlunit.MockWebConnection;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlMeta;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.github.searls.jasmine.io.IOUtilsWrapper;

@RunWith(MockitoJUnitRunner.class)
public class SpecRunnerHtmlGeneratorPseudoIntegrationTest {

	private static final String HTML5_DOCTYPE = "<!DOCTYPE html>";
	private static final String SOURCE_ENCODING = "as9du20asd xanadu";

	private Set<String> scripts = new LinkedHashSet<String>(asList("A"));

	static {
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
	}

	private SpecRunnerHtmlGenerator subject;

	@Mock private HtmlGeneratorConfiguration generatorConfiguration;
	@Rule public ExpectedException expectedException = ExpectedException.none();


	@Before
	public void setupGeneratorConfiguration() throws IOException{
		when(generatorConfiguration.getSourceEncoding()).thenReturn(SOURCE_ENCODING);
		when(generatorConfiguration.getReporterType()).thenReturn(ReporterType.TrivialReporter);
		when(generatorConfiguration.getRunnerTemplate(DEFAULT_RUNNER_HTML_TEMPLATE_FILE)).thenReturn(new IOUtilsWrapper().toString(DEFAULT_RUNNER_HTML_TEMPLATE_FILE));
	    subject = new DefaultSpecRunnerHtmlGenerator(generatorConfiguration);
	}

	@Test
	public void shouldBuildBasicHtmlWhenNoDependenciesAreProvided() {
		String html = subject.generate();

		assertThat(html, containsString("<html>"));
		assertThat(html, containsString("</html>"));
	}

	@Test
	public void shouldPutInADocTypeWhenNoDependenciesAreProvided() throws Exception {
		String html = subject.generate();

		assertThat(html, containsString(HTML5_DOCTYPE));
		assertThat(getPage(html).getDoctype().getName(), is("html"));
	}

	@Test
	public void shouldAssignSpecifiedSourceEncoding() throws Exception {
		String html = subject.generate();

		HtmlMeta contentType = getPage(html).getFirstByXPath("//meta");
		assertThat(contentType.getContentAttribute(), is("text/html; charset=" + SOURCE_ENCODING));
	}

	@Test
	public void shouldDefaultSourceEncodingWhenUnspecified() throws Exception {
		when(generatorConfiguration.getSourceEncoding()).thenReturn(null);

		String html = subject.generate();

		HtmlMeta contentType = getPage(html).getFirstByXPath("//meta");
		assertThat(contentType.getContentAttribute(), is("text/html; charset=" + SpecRunnerHtmlGenerator.DEFAULT_SOURCE_ENCODING));
	}

	@Test
	public void populatesJasmineSource() throws Exception {
		String expected = "javascript()";
		when(generatorConfiguration.IOtoString(eq(JASMINE_JS))).thenReturn(expected);

		String html = subject.generate();

		assertThat(html, containsScriptTagWith(expected));
	}

	@Test
	public void populatesJasmineHtmlSource() throws Exception {
		String expected = "javascript()";
		when(generatorConfiguration.IOtoString(eq(JASMINE_HTML_JS))).thenReturn(expected);


		String html = subject.generate();

		assertThat(html, containsScriptTagWith(expected));
	}

	@Test
	public void shouldPopulateCSSIntoHtmlWhenProvided() throws Exception {
		String expected = "h1 { background-color: awesome}";

		when(generatorConfiguration.IOtoString(eq(JASMINE_CSS))).thenReturn(expected);
		String html = subject.generate();

		assertThat(html, containsStyleTagWith(expected));
	}

	@Test
	public void containsScriptTagOfSource() throws IOException {
		String expected = scripts.iterator().next();

		when(generatorConfiguration.getAllScripts()).thenReturn(scripts);
		String html = subject.generate();

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
