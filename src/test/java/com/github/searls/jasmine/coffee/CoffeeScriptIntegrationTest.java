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
package com.github.searls.jasmine.coffee;

import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import static org.assertj.core.api.Assertions.assertThat;

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

  private CoffeeScript subject;

  private Map<String, String> mockCache;

  @Before
  public void before() throws Exception {
    subject = new CoffeeScript();
    mockCache = Collections.synchronizedMap(new WeakHashMap<String, String>());
    injectFakeCache(mockCache);
  }

  @Test
  public void itCompiles() throws IOException {
    String result = subject.compile(COFFEE);

    assertThat(result).isEqualTo(JAVASCRIPT);
  }

  @Test
  public void itReliesOnTheCache() throws IOException {
    String expected = "win";
    subject.compile(COFFEE);
    mockCache.put(StringEscapeUtils.escapeEcmaScript(COFFEE), expected);

    String result = subject.compile(COFFEE);

    assertThat(result).isEqualTo(expected);
  }

  private void injectFakeCache(Map<String, String> cacheMap) throws Exception {
    Field cache = subject.getClass().getDeclaredField("cache");
    cache.setAccessible(true);
    cache.set(subject, cacheMap);
  }

}
