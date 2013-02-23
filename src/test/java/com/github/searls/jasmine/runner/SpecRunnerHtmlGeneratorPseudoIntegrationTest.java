package com.github.searls.jasmine.runner;

import static com.github.searls.jasmine.Matchers.containsScriptTagWith;
import static com.github.searls.jasmine.Matchers.containsScriptTagWithSource;
import static com.github.searls.jasmine.Matchers.containsStyleTagWith;
import static com.github.searls.jasmine.runner.SpecRunnerHtmlGenerator.JASMINE_CSS;
import static com.github.searls.jasmine.runner.SpecRunnerHtmlGenerator.JASMINE_HTML_JS;
import static com.github.searls.jasmine.runner.SpecRunnerHtmlGenerator.JASMINE_JS;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

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
import com.github.searls.jasmine.io.scripts.ScriptResolver;
import com.github.searls.jasmine.io.scripts.ScriptResolverException;

@RunWith(MockitoJUnitRunner.class)
public class SpecRunnerHtmlGeneratorPseudoIntegrationTest {

  private static final String HTML5_DOCTYPE = "<!DOCTYPE html>";
  private static final String SOURCE_ENCODING = "as9du20asd xanadu";

  private final Set<String> scripts = new LinkedHashSet<String>(asList("A"));

  static {
    LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
  }

  private SpecRunnerHtmlGenerator subject;

  @Mock private ScriptResolver scriptResolver;
  @Mock private HtmlGeneratorConfiguration generatorConfiguration;
  @Rule public ExpectedException expectedException = ExpectedException.none();


  @Before
  public void setupGeneratorConfiguration() throws IOException{
    when(this.generatorConfiguration.getSourceEncoding()).thenReturn(SOURCE_ENCODING);
    when(this.generatorConfiguration.getReporterType()).thenReturn(ReporterType.HtmlReporter);
    when(this.generatorConfiguration.getScriptResolver()).thenReturn(this.scriptResolver);
    when(this.generatorConfiguration.getRunnerTemplate()).thenReturn(new IOUtilsWrapper().toString(SpecRunnerTemplate.DEFAULT.getTemplate()));
    this.subject = new DefaultSpecRunnerHtmlGenerator(this.generatorConfiguration);
  }

  @Test
  public void shouldBuildBasicHtmlWhenNoDependenciesAreProvided() {
    String html = this.subject.generate();

    assertThat(html, containsString("<html>"));
    assertThat(html, containsString("</html>"));
  }

  @Test
  public void shouldPutInADocTypeWhenNoDependenciesAreProvided() throws Exception {
    String html = this.subject.generate();

    assertThat(html, containsString(HTML5_DOCTYPE));
    assertThat(this.getPage(html).getDoctype().getName(), is("html"));
  }

  @Test
  public void shouldAssignSpecifiedSourceEncoding() throws Exception {
    String html = this.subject.generate();

    HtmlMeta contentType = this.getPage(html).getFirstByXPath("//meta");
    assertThat(contentType.getContentAttribute(), is("text/html; charset=" + SOURCE_ENCODING));
  }

  @Test
  public void shouldDefaultSourceEncodingWhenUnspecified() throws Exception {
    when(this.generatorConfiguration.getSourceEncoding()).thenReturn(null);

    String html = this.subject.generate();

    HtmlMeta contentType = this.getPage(html).getFirstByXPath("//meta");
    assertThat(contentType.getContentAttribute(), is("text/html; charset=" + SpecRunnerHtmlGenerator.DEFAULT_SOURCE_ENCODING));
  }

  @Test
  public void populatesJasmineSource() throws Exception {
    String expected = "javascript()";
    when(this.generatorConfiguration.IOtoString(eq(JASMINE_JS))).thenReturn(expected);

    String html = this.subject.generate();

    assertThat(html, containsScriptTagWith(expected));
  }

  @Test
  public void populatesJasmineHtmlSource() throws Exception {
    String expected = "javascript()";
    when(this.generatorConfiguration.IOtoString(eq(JASMINE_HTML_JS))).thenReturn(expected);


    String html = this.subject.generate();

    assertThat(html, containsScriptTagWith(expected));
  }

  @Test
  public void shouldPopulateCSSIntoHtmlWhenProvided() throws Exception {
    String expected = "h1 { background-color: awesome}";

    when(this.generatorConfiguration.IOtoString(eq(JASMINE_CSS))).thenReturn(expected);
    String html = this.subject.generate();

    assertThat(html, containsStyleTagWith(expected));
  }

  @Test
  public void containsScriptTagOfSource() throws ScriptResolverException {
    String expected = this.scripts.iterator().next();
    when(this.scriptResolver.getAllScripts()).thenReturn(this.scripts);

    String html = this.subject.generate();

    assertThat(html, containsScriptTagWithSource(expected));
  }

  private HtmlPage getPage(String html) throws Exception {
    MockWebConnection webConnection = new MockWebConnection();
    webConnection.setDefaultResponse(html);
    WebClient webClient = new WebClient();
    webClient.setWebConnection(webConnection);
    webClient.getOptions().setThrowExceptionOnScriptError(false);
    webClient.setIncorrectnessListener(new IncorrectnessListener() {
      @Override
      public void notify(String arg0, Object arg1) {
      }
    });
    return webClient.getPage("http://blah");
  }
}
