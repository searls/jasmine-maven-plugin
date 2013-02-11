package com.github.searls.jasmine.runner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.github.searls.jasmine.AbstractJasmineMojo;
import com.github.searls.jasmine.io.FileUtilsWrapper;
import com.github.searls.jasmine.io.IOUtilsWrapper;
import com.github.searls.jasmine.io.scripts.ScriptResolver;

public class HtmlGeneratorConfigurationTest {
  HtmlGeneratorConfiguration generatorConfiguration;


  @Test
  public void shouldReadCustomTemplateWhenOneIsProvided() throws IOException {
    File expected = mock(File.class);
    generatorConfiguration = initGenerator(null,expected);
    FileUtilsWrapper fileUtilsWrapper = mock(FileUtilsWrapper.class);
    generatorConfiguration.setFileUtilsWrapper(fileUtilsWrapper);

    generatorConfiguration.getRunnerTemplate();
    verify(fileUtilsWrapper, times(1)).readFileToString(expected);
  }

  @Test
  public void shouldReadSpecRunnerTemplateWhenOneIsProvided() throws IOException {
    generatorConfiguration = initGenerator(SpecRunnerTemplate.REQUIRE_JS,null);
    IOUtilsWrapper ioUtilsWrapper = mock(IOUtilsWrapper.class);
    generatorConfiguration.setIoUtilsWrapper(ioUtilsWrapper);
    
    generatorConfiguration.getRunnerTemplate();
    verify(ioUtilsWrapper, times(1)).toString(SpecRunnerTemplate.REQUIRE_JS.getTemplate());
  }

  @Test
  public void shouldReadDefaultSpecRunnerTemplateWhenNoneIsProvided() throws IOException {
    generatorConfiguration = initGenerator(null,null);
    IOUtilsWrapper ioUtilsWrapper = mock(IOUtilsWrapper.class);
    generatorConfiguration.setIoUtilsWrapper(ioUtilsWrapper);
    
    generatorConfiguration.getRunnerTemplate();
    verify(ioUtilsWrapper, times(1)).toString(SpecRunnerTemplate.DEFAULT.getTemplate());
  }
  
  private HtmlGeneratorConfiguration initGenerator(SpecRunnerTemplate template, File customTemplate) throws IOException {
    AbstractJasmineMojo mock = mock(AbstractJasmineMojo.class);
   	when(mock.getSpecRunnerTemplate()).thenReturn(template);
   	when(mock.getCustomRunnerTemplate()).thenReturn(customTemplate);
    return new HtmlGeneratorConfiguration(ReporterType.JsApiReporter, mock, mock(ScriptResolver.class));
  }
}
