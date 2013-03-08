package com.github.searls.jasmine.runner;

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

import com.github.searls.jasmine.io.IOUtilsWrapper;
import com.github.searls.jasmine.io.scripts.ScriptResolver;
import com.github.searls.jasmine.mojo.AbstractJasmineMojo;

@RunWith(PowerMockRunner.class)
@PrepareForTest(FileUtils.class)
public class HtmlGeneratorConfigurationTest {
	HtmlGeneratorConfiguration generatorConfiguration;


	@Test
	public void shouldReadCustomTemplateWhenOneIsProvided() throws IOException {
		File expected = mock(File.class);
		this.generatorConfiguration = this.initGenerator(null,expected);

		mockStatic(FileUtils.class);

		this.generatorConfiguration.getRunnerTemplate();

		verifyStatic(times(1));
		FileUtils.readFileToString(expected);
	}

	@Test
	public void shouldReadSpecRunnerTemplateWhenOneIsProvided() throws IOException {
		this.generatorConfiguration = this.initGenerator(SpecRunnerTemplate.REQUIRE_JS,null);
		IOUtilsWrapper ioUtilsWrapper = mock(IOUtilsWrapper.class);
		this.generatorConfiguration.setIoUtilsWrapper(ioUtilsWrapper);

		this.generatorConfiguration.getRunnerTemplate();
		verify(ioUtilsWrapper, times(1)).toString(SpecRunnerTemplate.REQUIRE_JS.getTemplate());
	}

	@Test
	public void shouldReadDefaultSpecRunnerTemplateWhenNoneIsProvided() throws IOException {
		this.generatorConfiguration = this.initGenerator(null,null);
		IOUtilsWrapper ioUtilsWrapper = mock(IOUtilsWrapper.class);
		this.generatorConfiguration.setIoUtilsWrapper(ioUtilsWrapper);

		this.generatorConfiguration.getRunnerTemplate();
		verify(ioUtilsWrapper, times(1)).toString(SpecRunnerTemplate.DEFAULT.getTemplate());
	}

	private HtmlGeneratorConfiguration initGenerator(SpecRunnerTemplate template, File customTemplate) throws IOException {
		AbstractJasmineMojo mock = mock(AbstractJasmineMojo.class);
		when(mock.getSpecRunnerTemplate()).thenReturn(template);
		when(mock.getCustomRunnerTemplate()).thenReturn(customTemplate);
		return new HtmlGeneratorConfiguration(ReporterType.JsApiReporter, mock, mock(ScriptResolver.class));
	}
}
