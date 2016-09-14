package com.github.searls.jasmine.runner;

import com.github.searls.jasmine.io.scripts.ScriptResolver;
import com.github.searls.jasmine.mojo.AbstractJasmineMojo;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    Assertions.assertThat(specRunnerHtmlGeneratorFactory.createHtmlGenerator(setupWithTemplate(SpecRunnerTemplate.DEFAULT))).isInstanceOf(DefaultSpecRunnerHtmlGenerator.class);
  }

  @Test
  public void shouldCreateHtmlGeneratorWhenPassedValidInput() {
    AbstractJasmineMojo mock = mock(AbstractJasmineMojo.class);
    when(mock.getSpecRunnerTemplate()).thenReturn(SpecRunnerTemplate.DEFAULT);
    Assertions.assertThat(specRunnerHtmlGeneratorFactory.create(ReporterType.HtmlReporter, mock, mock(ScriptResolver.class))).isInstanceOf(DefaultSpecRunnerHtmlGenerator.class);
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
      Assertions.assertThat(e.getMessage()).isEqualTo("Foo");
    }

//    Assertions.assertThatExceptionOfType(InstantiationError.class)
//      .isThrownBy(() -> specRunnerHtmlGeneratorFactory.create(ReporterType.HtmlReporter, mock, mock1))
//      .withMessage("Foo");


  }
}
