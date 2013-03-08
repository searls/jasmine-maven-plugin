package com.github.searls.jasmine.runner;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.github.searls.jasmine.io.scripts.ScriptResolver;
import com.github.searls.jasmine.mojo.AbstractJasmineMojo;

public class SpecRunnerHtmlGeneratorFactoryTest {

  private SpecRunnerHtmlGeneratorFactory specRunnerHtmlGeneratorFactory;

  @Before
  public void init() {
    specRunnerHtmlGeneratorFactory = new SpecRunnerHtmlGeneratorFactory();
  }

  private HtmlGeneratorConfiguration setupWithTemplate(SpecRunnerTemplate o) {
    HtmlGeneratorConfiguration generatorConfiguration = mock(HtmlGeneratorConfiguration.class);
    when(generatorConfiguration.getSpecRunnerTemplate()).thenReturn(o);
    return generatorConfiguration;
  }

  @Test
  public void shouldReturnDefaultImplementationWhenCalledWithDefault() throws Exception {
    assertThat(specRunnerHtmlGeneratorFactory.createHtmlGenerator(setupWithTemplate(SpecRunnerTemplate.DEFAULT)), instanceOf(DefaultSpecRunnerHtmlGenerator.class));
  }

  @Test
  public void shouldCreateHtmlGeneratorWhenPassedValidInput() {
    AbstractJasmineMojo mock = mock(AbstractJasmineMojo.class);
    when(mock.getSpecRunnerTemplate()).thenReturn(SpecRunnerTemplate.DEFAULT);
    assertThat(specRunnerHtmlGeneratorFactory.create(ReporterType.HtmlReporter, mock, mock(ScriptResolver.class)), instanceOf(DefaultSpecRunnerHtmlGenerator.class));
  }

  @Test
  public void shouldWrapIOException() throws IOException {
    AbstractJasmineMojo mock = mock(AbstractJasmineMojo.class);
    when(mock.getSpecRunnerTemplate()).thenReturn(SpecRunnerTemplate.DEFAULT);
    ScriptResolver mock1 = mock(ScriptResolver.class);
    // doThrow(new IOException("Foo")).when(mock1).resolveScripts();
    try {
      specRunnerHtmlGeneratorFactory.create(ReporterType.HtmlReporter, mock, mock1);
    } catch (InstantiationError e) {
      assertThat(e.getMessage(), is("Foo"));
    }

  }
}
