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

import com.github.searls.jasmine.io.scripts.ScriptResolver;
import com.github.searls.jasmine.mojo.AbstractJasmineMojo;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
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
    assertThat(specRunnerHtmlGeneratorFactory.createHtmlGenerator(setupWithTemplate(SpecRunnerTemplate.DEFAULT))).isInstanceOf(DefaultSpecRunnerHtmlGenerator.class);
  }

  @Test
  public void shouldCreateHtmlGeneratorWhenPassedValidInput() {
    AbstractJasmineMojo mock = mock(AbstractJasmineMojo.class);
    when(mock.getSpecRunnerTemplate()).thenReturn(SpecRunnerTemplate.DEFAULT);
    assertThat(specRunnerHtmlGeneratorFactory.create(ReporterType.HtmlReporter, mock, mock(ScriptResolver.class))).isInstanceOf(DefaultSpecRunnerHtmlGenerator.class);
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
      assertThat(e.getMessage()).isEqualTo("Foo");
    }

//    Assertions.assertThatExceptionOfType(InstantiationError.class)
//      .isThrownBy(() -> specRunnerHtmlGeneratorFactory.create(ReporterType.HtmlReporter, mock, mock1))
//      .withMessage("Foo");


  }
}
