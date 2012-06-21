package com.github.searls.jasmine.runner;

import com.github.searls.jasmine.AbstractJasmineMojo;
import com.github.searls.jasmine.io.FileUtilsWrapper;
import com.github.searls.jasmine.io.IOUtilsWrapper;
import com.github.searls.jasmine.io.scripts.ScriptResolver;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
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

    @Test
    public void shouldReturnTheSourceAndSpecDirectoryConfigurationOptionsSpecifiedByTheResolver() throws IOException {
        ScriptResolver scriptResolverMock = mock(ScriptResolver.class);
        AbstractJasmineMojo jasmineMojoMock = mock(AbstractJasmineMojo.class);

        String specsDirectory = "specsDirectory";
        String sourceDirectory = "sourceDirectory";
        String specDirectoryRelative = "sourceDirectoryRelative";
        String sourceDirectoryRelative = "sourceDirectoryRelative";

        when(scriptResolverMock.getSpecDirectoryPath()).thenReturn(specsDirectory);
        when(scriptResolverMock.getSourceDirectory()).thenReturn(sourceDirectory);
        when(scriptResolverMock.getSpecDirectoryRelativePath()).thenReturn(specDirectoryRelative);
        when(scriptResolverMock.getSourceDirectoryRelativePath()).thenReturn(sourceDirectoryRelative);

        HtmlGeneratorConfiguration generatorConfiguration = new HtmlGeneratorConfiguration(ReporterType.JsApiReporter, jasmineMojoMock, scriptResolverMock);

        assertThat(generatorConfiguration.getSpecDirectory(), is(specsDirectory));
        assertThat(generatorConfiguration.getSourceDirectory(), is(sourceDirectory));
        assertThat(generatorConfiguration.getSpecDirectoryRelativePath(), is(specDirectoryRelative));
        assertThat(generatorConfiguration.getSourceDirectoryRelativePath(), is(sourceDirectoryRelative));
    }

    private HtmlGeneratorConfiguration initGenerator(File expectedCustomRunnerTemplate) throws IOException {
        AbstractJasmineMojo mock = mock(AbstractJasmineMojo.class);
        when(mock.getCustomRunnerTemplate()).thenReturn(expectedCustomRunnerTemplate);
        return new HtmlGeneratorConfiguration(ReporterType.JsApiReporter, mock, mock(ScriptResolver.class));
    }
}
