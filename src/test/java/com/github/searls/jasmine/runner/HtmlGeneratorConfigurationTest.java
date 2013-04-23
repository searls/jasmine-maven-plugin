package com.github.searls.jasmine.runner;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.searls.jasmine.config.JasmineConfiguration;
import com.github.searls.jasmine.io.IOUtilsWrapper;
import com.github.searls.jasmine.io.scripts.ScriptResolver;

@RunWith(PowerMockRunner.class)
@PrepareForTest(FileUtils.class)
public class HtmlGeneratorConfigurationTest {
	HtmlGeneratorConfiguration generatorConfiguration;


	@Test
	public void shouldReadCustomTemplateWhenOneIsProvided() throws IOException {
		mockStatic(FileUtils.class);

		File expected = mock(File.class);
		IOUtilsWrapper ioUtilsWrapper = mock(IOUtilsWrapper.class);
		this.generatorConfiguration = this.initGenerator(ioUtilsWrapper,null,expected);
		this.generatorConfiguration.getRunnerTemplate();

		verifyStatic(times(1));
		FileUtils.readFileToString(expected);
	}

	@Test
	public void shouldReadSpecRunnerTemplateWhenOneIsProvided() throws IOException {
		IOUtilsWrapper ioUtilsWrapper = mock(IOUtilsWrapper.class);
		this.generatorConfiguration = this.initGenerator(ioUtilsWrapper,SpecRunnerTemplate.REQUIRE_JS,null);
		this.generatorConfiguration.getRunnerTemplate();
		verify(ioUtilsWrapper, times(1)).toString(SpecRunnerTemplate.REQUIRE_JS.getTemplate());
	}

	@Test
	public void shouldReadDefaultSpecRunnerTemplateWhenNoneIsProvided() throws IOException {
		IOUtilsWrapper ioUtilsWrapper = mock(IOUtilsWrapper.class);
		this.generatorConfiguration = this.initGenerator(ioUtilsWrapper,null,null);
		this.generatorConfiguration.getRunnerTemplate();
		verify(ioUtilsWrapper, times(1)).toString(SpecRunnerTemplate.DEFAULT.getTemplate());
	}

	@Test
	public void shouldHaveCorrectReporterType() throws IOException {
		this.generatorConfiguration = this.initGenerator(null,null,null);
		assertSame(ReporterType.JsApiReporter, this.generatorConfiguration.getReporterType());
	}

	@Test
	public void shouldHaveCorrectSpecRunnerTemplate() throws IOException {
		this.generatorConfiguration = this.initGenerator(null,SpecRunnerTemplate.REQUIRE_JS,null);
		assertSame(SpecRunnerTemplate.REQUIRE_JS, this.generatorConfiguration.getSpecRunnerTemplate());
	}

	private HtmlGeneratorConfiguration initGenerator(IOUtilsWrapper ioUtilsWrapper, SpecRunnerTemplate template, File customTemplate) throws IOException {
		JasmineConfiguration mock = mock(JasmineConfiguration.class);
		when(mock.getSpecRunnerTemplate()).thenReturn(template);
		when(mock.getCustomRunnerTemplate()).thenReturn(customTemplate);
		return new HtmlGeneratorConfiguration(ioUtilsWrapper, ReporterType.JsApiReporter, mock, mock(ScriptResolver.class));
	}
}
