package searls.jasmine.runner;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import searls.jasmine.io.FileUtilsWrapper;
import searls.jasmine.io.IOUtilsWrapper;

import com.gargoylesoftware.htmlunit.MockWebConnection;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlMeta;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

@RunWith(MockitoJUnitRunner.class)
public class SpecRunnerHtmlGeneratorIntegrationTest {

	private static final String HTML5_DOCTYPE = "<!DOCTYPE html>";

	private static final String SOURCE_ENCODING = "as9du20asd xanadu";
	
	@InjectMocks private SpecRunnerHtmlGenerator specRunnerHtmlGenerator = new SpecRunnerHtmlGenerator(null, null, null, SOURCE_ENCODING);
	
	@Mock private FileUtilsWrapper fileUtilsWrapper;
	@Spy private IOUtilsWrapper ioUtilsWrapper = new IOUtilsWrapper();

	private List<Artifact> deps = new ArrayList<Artifact>();
	
	@Test
	public void shouldBuildBasicHtmlWhenNoDependenciesAreProvided() {
		String html = specRunnerHtmlGenerator.generate(deps, ReporterType.TrivialReporter, null);
		
		assertThat(html, containsString("<html>"));
		assertThat(html, containsString("</html>"));
	}

	@Test
	public void shouldPutInADocTypeWhenNoDependenciesAreProvided() {
		String html = specRunnerHtmlGenerator.generate(deps, ReporterType.TrivialReporter, null);
		
		assertThat(html, containsString(HTML5_DOCTYPE));
		assertThat(getPage(html).getDoctype().getName(),is("html"));
	}
	
	@Test
	public void shouldAssignSpecifiedSourceEncoding() {
		String html = specRunnerHtmlGenerator.generate(deps, ReporterType.TrivialReporter, null);
		
		HtmlMeta contentType = getPage(html).getFirstByXPath("//meta");
		assertThat(contentType.getContentAttribute(),is("text/html; charset="+SOURCE_ENCODING));
	}

	@Test
	public void shouldPopulateJasmineSourceIntoHtmlWhenProvided() throws Exception {
		String expectedContents = "javascript()";
		deps.add(mockDependency("com.pivotallabs", "jasmine", "1.0.1", "js", expectedContents));

		String html = specRunnerHtmlGenerator.generate(deps, ReporterType.TrivialReporter, null);

		assertThat(html, containsString("<script type=\"text/javascript\">" + expectedContents + "</script>"));
	}

	@Test
	public void shouldPopulateMultipleJavascriptSourcesIntoHtmlWhenProvided() throws Exception {
		String jasmineString = "javascript_jasmine()";
		String jasmineHtmlString = "javascript_jasmine_html()";
		deps.add(mockDependency("com.pivotallabs", "jasmine", "1.0.1", "js", jasmineString));
		deps.add(mockDependency("com.pivotallabs", "jasmine-html", "1.0.1", "js", jasmineHtmlString));

		String html = specRunnerHtmlGenerator.generate(deps, ReporterType.TrivialReporter, null);

		assertThat(html, containsString("<script type=\"text/javascript\">" + jasmineString + "</script>"));
		assertThat(html, containsString("<script type=\"text/javascript\">" + jasmineHtmlString + "</script>"));
	}

	@Test
	public void shouldPopulateCSSIntoHtmlWhenProvided() throws Exception {
		String css = "h1 { background-color: awesome}";
		deps.add(mockDependency("com.pivotallabs", "jasmine-css", "1.0.1", "css", css));

		String html = specRunnerHtmlGenerator.generate(deps, ReporterType.TrivialReporter, null);

		assertThat(html, containsString("<style type=\"text/css\">" + css + "</style>"));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void shouldNotReadDefaultTemplateWhenOneIsProvided() throws IOException {
		File expected = mock(File.class);
		
		specRunnerHtmlGenerator.generate(Collections.EMPTY_LIST, ReporterType.TrivialReporter, expected);

		verify(ioUtilsWrapper,never()).toString(isA(InputStream.class));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldReadCustomTemplateWhenOneIsProvided() throws IOException {
		File expected = mock(File.class);
		
		specRunnerHtmlGenerator.generate(Collections.EMPTY_LIST, ReporterType.TrivialReporter, expected);

		verify(fileUtilsWrapper).readFileToString(expected);
	}
	
	private Artifact mockDependency(String groupId, String artifactId, String version, String type, String fileContents) throws Exception {
		Artifact dep = mock(Artifact.class);
		when(dep.getGroupId()).thenReturn(groupId);
		when(dep.getArtifactId()).thenReturn(artifactId);
		when(dep.getVersion()).thenReturn(version);
		when(dep.getType()).thenReturn(type);

		File f = mock(File.class);
		when(fileUtilsWrapper.readFileToString(f)).thenReturn(fileContents);
		when(dep.getFile()).thenReturn(f);

		return dep;
	}

	private HtmlPage getPage(String html) {
		MockWebConnection webConnection = new MockWebConnection();
		webConnection.setDefaultResponse(html);
		WebClient webClient = new WebClient();
		webClient.setWebConnection(webConnection);
		webClient.setThrowExceptionOnScriptError(false);
		try {
			return webClient.getPage("http://blah");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
