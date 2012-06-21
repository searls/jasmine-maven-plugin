package com.github.searls.jasmine.runner;

import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RequireJsSpecRunnerHtmlGeneratorTest {
    @Test
    public void shouldPassTheSourceAndSpecDirectoryToTheTemplateWhenGeneratingTheRunner() throws IOException {
        String specDirectory = "specDirectory";
        String sourceDirectory = "sourceDirectory";

        HtmlGeneratorConfiguration configurationMock = mock(HtmlGeneratorConfiguration.class);
        when(configurationMock.getSpecDirectory()).thenReturn(specDirectory);
        when(configurationMock.getSourceDirectory()).thenReturn(sourceDirectory);
        when(configurationMock.getReporterType()).thenReturn(ReporterType.TrivialReporter);
        when(configurationMock.getRunnerTemplate(anyString())).thenReturn("$sourceDir$ $specDir$");

        SpecRunnerHtmlGenerator htmlGenerator = new RequireJsSpecRunnerHtmlGenerator(configurationMock);
        String result = htmlGenerator.generate();

        assertThat(result, containsString(specDirectory));
        assertThat(result, containsString(sourceDirectory));
    }

    @Test
    public void shouldPassTheRelativeSourceAndSpecDirectoryToTheTemplateWhenGeneratingTheRunnerWithRelativePaths() throws IOException {
        String specDirectory = "specDirectoryRelative";
        String sourceDirectory = "sourceDirectoryRelative";

        HtmlGeneratorConfiguration configurationMock = mock(HtmlGeneratorConfiguration.class);
        when(configurationMock.getSpecDirectoryRelativePath()).thenReturn(specDirectory);
        when(configurationMock.getSourceDirectoryRelativePath()).thenReturn(sourceDirectory);
        when(configurationMock.getReporterType()).thenReturn(ReporterType.TrivialReporter);
        when(configurationMock.getRunnerTemplate(anyString())).thenReturn("$sourceDir$ $specDir$");

        SpecRunnerHtmlGenerator htmlGenerator = new RequireJsSpecRunnerHtmlGenerator(configurationMock);
        String result = htmlGenerator.generateWitRelativePaths();

        assertThat(result, containsString(specDirectory));
        assertThat(result, containsString(sourceDirectory));
    }
}
