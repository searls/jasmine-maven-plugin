package com.github.searls.jasmine.coffee;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Test;

public class CoffeeScriptIntegrationTest {

	private static final String COFFEE =
		"describe \"HelloWorld\", ->\n" +
		"  it \"should say hello\", ->\n" +
		"    hello_world = new HelloWorld\n" +
		"    expect(hello_world.greeting()).toBe \"Hello, World\"";

	private static final String JAVASCRIPT =
		"(function() {\n\n" +
		"  describe(\"HelloWorld\", function() {\n" +
		"    return it(\"should say hello\", function() {\n" +
		"      var hello_world;\n" +
		"      hello_world = new HelloWorld;\n" +
		"      return expect(hello_world.greeting()).toBe(\"Hello, World\");\n" +
		"    });\n" +
		"  });\n\n" +
		"}).call(this);\n";

	private CoffeeScript subject = new CoffeeScript();

	@Test
	public void itCompiles() throws IOException {
		String result = subject.compile(COFFEE);

		assertThat(result,is(JAVASCRIPT));
	}

	@Test
	public void itReliesOnTheCache() throws Exception {
		String expected = "win";
		subject.compile(COFFEE);
		injectFakeCache(Collections.singletonMap(StringEscapeUtils.escapeJavaScript(COFFEE), expected));

		String result = subject.compile(COFFEE);

		assertThat(result,is(expected));
	}

	private void injectFakeCache(Map<String,String> cacheMap) throws Exception {
		Field cache = subject.getClass().getDeclaredField("cache");
		cache.setAccessible(true);
		cache.set(subject, cacheMap);
	}

}
