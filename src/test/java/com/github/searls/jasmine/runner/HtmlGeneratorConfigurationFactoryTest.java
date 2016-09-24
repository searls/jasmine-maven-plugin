package com.github.searls.jasmine.runner;

import com.github.searls.jasmine.config.JasmineConfiguration;
import com.github.searls.jasmine.io.IoUtilities;
import com.github.searls.jasmine.io.scripts.ScriptResolver;
import com.google.common.base.Optional;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(FileUtils.class)
public class HtmlGeneratorConfigurationFactoryTest {

  private static final String CUSTOM_TEMPLATE_STRING = "custom-template";
  private static final String CUSTOM_CONFIG_STRING = "custom-config";
  private static final String RUNNER_TEMPLATE_STRING = "runner-template";

  private static final String ENCODING = StandardCharsets.UTF_8.name();

  @Mock
  private IoUtilities ioUtilities;

  @Mock
  private JasmineConfiguration jasmineConfiguration;

  @Mock
  private File customRunnerConfiguration;

  @Mock
  private File customTemplateFile;

  @Mock
  private ScriptResolver scriptResolver;

  @InjectMocks
  private HtmlGeneratorConfigurationFactory generatorConfigurationFactory;

  @Before
  public void before() throws IOException {
    mockStatic(FileUtils.class);
    when(ioUtilities.readFileToString(customTemplateFile)).thenReturn(CUSTOM_TEMPLATE_STRING);
    when(ioUtilities.readFileToString(customRunnerConfiguration)).thenReturn(CUSTOM_CONFIG_STRING);
    when(ioUtilities.resourceToString(SpecRunnerTemplate.DEFAULT.getTemplate())).thenReturn(RUNNER_TEMPLATE_STRING);
    when(ioUtilities.resourceToString(SpecRunnerTemplate.REQUIRE_JS.getTemplate())).thenReturn(CUSTOM_TEMPLATE_STRING);
  }

  @Test
  public void shouldReadCustomTemplateWhenOneIsProvided() throws IOException {
    this.whenConfiguredWith(SpecRunnerTemplate.DEFAULT, customTemplateFile);

    HtmlGeneratorConfiguration htmlGeneratorConfiguration = this.generatorConfigurationFactory.create(
      jasmineConfiguration,
      scriptResolver
    );
    assertThat(htmlGeneratorConfiguration.getRunnerTemplate())
      .isEqualTo(CUSTOM_TEMPLATE_STRING);
    assertThat(htmlGeneratorConfiguration.getCustomRunnerConfiguration())
      .isEqualTo(CUSTOM_CONFIG_STRING);
  }

  @Test
  public void shouldReadSpecRunnerTemplateWhenOneIsProvided() throws IOException {
    this.whenConfiguredWith(SpecRunnerTemplate.REQUIRE_JS, null);

    HtmlGeneratorConfiguration htmlGeneratorConfiguration = this.generatorConfigurationFactory.create(
      jasmineConfiguration,
      scriptResolver
    );

    assertThat(htmlGeneratorConfiguration.getRunnerTemplate()).isEqualTo(CUSTOM_TEMPLATE_STRING);
  }

  @Test
  public void shouldReadDefaultSpecRunnerTemplateWhenNoneIsProvided() throws IOException {
    this.whenConfiguredWith(SpecRunnerTemplate.DEFAULT, null);

    HtmlGeneratorConfiguration htmlGeneratorConfiguration = this.generatorConfigurationFactory.create(
      jasmineConfiguration,
      scriptResolver
    );

    assertThat(htmlGeneratorConfiguration.getRunnerTemplate()).isEqualTo(RUNNER_TEMPLATE_STRING);
  }

  @Test
  public void shouldHaveCorrectReporterType() throws IOException {
    this.whenConfiguredWith(SpecRunnerTemplate.DEFAULT, null);

    HtmlGeneratorConfiguration htmlGeneratorConfiguration = this.generatorConfigurationFactory.create(
      jasmineConfiguration,
      scriptResolver
    );

    assertThat(htmlGeneratorConfiguration.getReporterType()).isEqualTo(ReporterType.JsApiReporter);
  }

  @Test
  public void shouldHaveCorrectSpecRunnerTemplate() throws IOException {
    this.whenConfiguredWith(SpecRunnerTemplate.REQUIRE_JS, null);

    HtmlGeneratorConfiguration htmlGeneratorConfiguration = this.generatorConfigurationFactory.create(
      jasmineConfiguration,
      scriptResolver
    );

    assertThat(htmlGeneratorConfiguration.getSpecRunnerTemplate()).isEqualTo(SpecRunnerTemplate.REQUIRE_JS);
  }

  private void whenConfiguredWith(SpecRunnerTemplate template,
                                  File customTemplate) throws IOException {
    when(jasmineConfiguration.getSourceEncoding()).thenReturn(ENCODING);
    when(jasmineConfiguration.getSpecRunnerTemplate()).thenReturn(template);
    when(jasmineConfiguration.getReporterType()).thenReturn(ReporterType.JsApiReporter);
    when(jasmineConfiguration.getCustomRunnerConfiguration()).thenReturn(Optional.of(customRunnerConfiguration));
    when(jasmineConfiguration.getCustomRunnerTemplate()).thenReturn(Optional.fromNullable(customTemplate));
  }
}
