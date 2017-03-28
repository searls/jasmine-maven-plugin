/*-
 * #%L
 * jasmine-maven-plugin
 * %%
 * Copyright (C) 2010 - 2017 Justin Searls
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.github.searls.jasmine.runner;

import com.gargoylesoftware.htmlunit.IncorrectnessListener;
import com.gargoylesoftware.htmlunit.MockWebConnection;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlMeta;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.github.searls.jasmine.io.IOUtilsWrapper;
import com.github.searls.jasmine.io.scripts.ScriptResolver;
import com.github.searls.jasmine.io.scripts.ScriptResolverException;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.github.searls.jasmine.Matchers.containsLinkTagWithSource;
import static com.github.searls.jasmine.Matchers.containsScriptTagWithSource;
import static com.github.searls.jasmine.runner.SpecRunnerHtmlGenerator.JASMINE_CSS;
import static com.github.searls.jasmine.runner.SpecRunnerHtmlGenerator.JASMINE_HTML_JS;
import static com.github.searls.jasmine.runner.SpecRunnerHtmlGenerator.JASMINE_JS;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SpecRunnerHtmlGeneratorPseudoIntegrationTest {

  private static final String HTML5_DOCTYPE = "<!DOCTYPE html>";
  private static final String SOURCE_ENCODING = "as9du20asd xanadu";

  private final Set<String> scripts = new LinkedHashSet<String>(asList("A"));

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
    String html = this.subject.generate();

    assertThat(html, containsScriptTagWithSource(JASMINE_JS));
  }

  @Test
  public void populatesJasmineHtmlSource() throws Exception {
    String html = this.subject.generate();

    assertThat(html, containsScriptTagWithSource(JASMINE_HTML_JS));
  }

  @Test
  public void shouldPopulateCSSIntoHtmlWhenProvided() throws Exception {
    String html = this.subject.generate();

    assertThat(html, containsLinkTagWithSource(JASMINE_CSS));
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
