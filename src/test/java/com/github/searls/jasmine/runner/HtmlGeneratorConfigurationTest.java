package com.github.searls.jasmine.runner;

import com.github.searls.jasmine.AbstractJasmineMojo;
import com.github.searls.jasmine.io.FileUtilsWrapper;
import com.github.searls.jasmine.io.IOUtilsWrapper;
import com.github.searls.jasmine.io.scripts.ScriptResolver;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.*;

public class HtmlGeneratorConfigurationTest {
    HtmlGeneratorConfiguration generatorConfiguration;


    @Test
    public void shouldNotReadDefaultTemplateWhenOneIsProvided() throws IOException {
        File expected = mock(File.class);
        generatorConfiguration = initGenerator(expected);
        FileUtilsWrapper fileUtilsWrapper = mock(FileUtilsWrapper.class);
        generatorConfiguration.setFileUtilsWrapper(fileUtilsWrapper);

        generatorConfiguration.getRunnerTemplate("");
        verify(fileUtilsWrapper, times(1)).readFileToString(expected);
    }

    @Test
    public void shouldReadCustomTemplateWhenOneIsProvided() throws IOException {
        generatorConfiguration = initGenerator(null);
        IOUtilsWrapper ioUtilsWrapper = mock(IOUtilsWrapper.class);
        generatorConfiguration.setIoUtilsWrapper(ioUtilsWrapper);

        generatorConfiguration.getRunnerTemplate("foo");
        verify(ioUtilsWrapper, times(1)).toString("foo");

    }

    private HtmlGeneratorConfiguration initGenerator(File expectedCustomRunnerTemplate) throws IOException {
        AbstractJasmineMojo mock = mock(AbstractJasmineMojo.class);
        when(mock.getCustomRunnerTemplate()).thenReturn(expectedCustomRunnerTemplate);
        return new HtmlGeneratorConfiguration(ReporterType.JsApiReporter, mock, mock(ScriptResolver.class));
    }
}
