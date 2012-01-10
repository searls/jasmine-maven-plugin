package com.github.searls.jasmine.runner;

import com.github.searls.jasmine.AbstractJasmineMojo;
import com.github.searls.jasmine.io.scripts.ScriptResolver;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SpecRunnerHtmlGeneratorFactoryTest {

	private SpecRunnerHtmlGeneratorFactory specRunnerHtmlGeneratorFactory;

	@Before
	public void init() {
		specRunnerHtmlGeneratorFactory = new SpecRunnerHtmlGeneratorFactory();
	}

	@Test
	public void shouldThrowExceptionWhenPassedNull() throws Exception {
		try {
			specRunnerHtmlGeneratorFactory.createHtmlGenerator(setupWithTemplate(null));
			fail("Should throw IllegalArgumentException when passed null");
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), notNullValue());
		}
	}

	private HtmlGeneratorConfiguration setupWithTemplate(String o) {
		HtmlGeneratorConfiguration generatorConfiguration = mock(HtmlGeneratorConfiguration.class);
		when(generatorConfiguration.getSpecRunnerTemplate()).thenReturn(o);
		return generatorConfiguration;
	}

	@Test
	public void shouldThrowWhenPassedInvalidStrategy() throws Exception {
		try {
			specRunnerHtmlGeneratorFactory.createHtmlGenerator(setupWithTemplate("INVALID"));
			fail("Should not allow invalid input");
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), notNullValue());
		}
	}

	@Test
	public void shouldReturnDefaultImplementationWhenCalledWithDefault() throws Exception {
		assertThat(specRunnerHtmlGeneratorFactory.createHtmlGenerator(setupWithTemplate("DEFAULT")), instanceOf(DefaultSpecRunnerHtmlGenerator.class));
	}

	@Test
	public void shouldReturnRequireJsImplementationWhenCalledWithRequireJs() throws Exception {
		assertThat(specRunnerHtmlGeneratorFactory.createHtmlGenerator(setupWithTemplate("REQUIRE_JS")), instanceOf(RequireJsSpecRunnerHtmlGenerator.class));
	}

	@Test
	public void shouldCreateHtmlGeneratorWhenPassedValidInput() {
		AbstractJasmineMojo mock = mock(AbstractJasmineMojo.class);
		when(mock.getSpecRunnerTemplate()).thenReturn("DEFAULT");
		assertThat(specRunnerHtmlGeneratorFactory.create(ReporterType.TrivialReporter, mock, mock(ScriptResolver.class)), instanceOf(DefaultSpecRunnerHtmlGenerator.class));
	}

	@Test
	public void shouldWrapIOException() throws IOException {
		AbstractJasmineMojo mock = mock(AbstractJasmineMojo.class);
		when(mock.getSpecRunnerTemplate()).thenReturn("DEFAULT");
		ScriptResolver mock1 = mock(ScriptResolver.class);
		doThrow(new IOException("Foo")).when(mock1).resolveScripts();
		try {
			specRunnerHtmlGeneratorFactory.create(ReporterType.TrivialReporter, mock, mock1);
		} catch (InstantiationError e) {
			assertThat(e.getMessage(), is("Foo"));
		}

	}
}
