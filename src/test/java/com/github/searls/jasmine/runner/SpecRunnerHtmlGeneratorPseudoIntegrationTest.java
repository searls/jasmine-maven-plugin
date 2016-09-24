package com.github.searls.jasmine.runner;

import com.gargoylesoftware.htmlunit.MockWebConnection;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlMeta;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.github.searls.jasmine.HtmlAssertions;
import com.github.searls.jasmine.format.FormatsScriptTags;
import com.github.searls.jasmine.io.IoUtilities;
import com.github.searls.jasmine.io.scripts.ScriptResolver;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.github.searls.jasmine.runner.SpecRunnerHtmlGenerator.JASMINE_CSS;
import static com.github.searls.jasmine.runner.SpecRunnerHtmlGenerator.JASMINE_HTML_JS;
import static com.github.searls.jasmine.runner.SpecRunnerHtmlGenerator.JASMINE_JS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SpecRunnerHtmlGeneratorPseudoIntegrationTest {

  private static final String HTML5_DOCTYPE = "<!DOCTYPE html>";
  private static final String SOURCE_ENCODING = "as9du20asd xanadu";

  private final Set<String> scripts = new LinkedHashSet<String>(Arrays.asList("A"));

  static {
    LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
  }

  private SpecRunnerHtmlGenerator subject;

  @Mock
  private ScriptResolver scriptResolver;

  @Mock
  private HtmlGeneratorConfiguration generatorConfiguration;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Before
  public void setupGeneratorConfiguration() throws IOException {
    when(this.generatorConfiguration.getSourceEncoding()).thenReturn(SOURCE_ENCODING);
    when(this.generatorConfiguration.getReporterType()).thenReturn(ReporterType.HtmlReporter);
    when(this.generatorConfiguration.getScriptResolver()).thenReturn(this.scriptResolver);
    when(this.generatorConfiguration.getRunnerTemplate())
      .thenReturn(new IoUtilities().resourceToString(SpecRunnerTemplate.DEFAULT.getTemplate()));

    this.subject = new DefaultSpecRunnerHtmlGenerator(new FormatsScriptTags());
  }

  @Test
  public void shouldBuildBasicHtmlWhenNoDependenciesAreProvided() {
    String html = this.subject.generate(generatorConfiguration);

    assertThat(html).contains("<html>").contains("</html>");
  }

  @Test
  public void shouldPutInADocTypeWhenNoDependenciesAreProvided() throws Exception {
    String html = this.subject.generate(generatorConfiguration);

    assertThat(html).contains(HTML5_DOCTYPE);
    assertThat(this.getPage(html).getDoctype().getName()).isEqualTo("html");
  }

  @Test
  public void shouldAssignSpecifiedSourceEncoding() throws Exception {
    String html = this.subject.generate(generatorConfiguration);

    HtmlMeta contentType = this.getPage(html).getFirstByXPath("//meta");
    assertThat(contentType.getContentAttribute()).isEqualTo("text/html; charset=" + SOURCE_ENCODING);
  }

  @Test
  public void shouldDefaultSourceEncodingWhenUnspecified() throws Exception {
    when(this.generatorConfiguration.getSourceEncoding()).thenReturn(null);

    String html = this.subject.generate(generatorConfiguration);

    HtmlMeta contentType = this.getPage(html).getFirstByXPath("//meta");
    assertThat(contentType.getContentAttribute()).isEqualTo("text/html; charset=" + SpecRunnerHtmlGenerator.DEFAULT_SOURCE_ENCODING);
  }

  @Test
  public void populatesJasmineSource() throws Exception {
    String html = this.subject.generate(generatorConfiguration);
    HtmlAssertions.assertThat(html).containsScriptTagWithSource(JASMINE_JS);
  }

  @Test
  public void populatesJasmineHtmlSource() throws Exception {
    String html = this.subject.generate(generatorConfiguration);
    HtmlAssertions.assertThat(html).containsScriptTagWithSource(JASMINE_HTML_JS);
  }

  @Test
  public void shouldPopulateCSSIntoHtmlWhenProvided() throws Exception {
    String html = this.subject.generate(generatorConfiguration);
    HtmlAssertions.assertThat(html).containsLinkTagWithSource(JASMINE_CSS);
  }

  @Test
  public void containsScriptTagOfSource() {
    String expected = this.scripts.iterator().next();
    when(this.scriptResolver.getAllScripts()).thenReturn(this.scripts);

    String html = this.subject.generate(generatorConfiguration);

    HtmlAssertions.assertThat(html).containsScriptTagWithSource(expected);
  }

  private HtmlPage getPage(String html) throws Exception {
    MockWebConnection webConnection = new MockWebConnection();
    webConnection.setDefaultResponse(html);
    WebClient webClient = new WebClient();
    webClient.setWebConnection(webConnection);
    webClient.getOptions().setThrowExceptionOnScriptError(false);
    return webClient.getPage("http://blah");
  }
}
