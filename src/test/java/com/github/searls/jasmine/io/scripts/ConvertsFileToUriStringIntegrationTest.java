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
package com.github.searls.jasmine.io.scripts;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class ConvertsFileToUriStringIntegrationTest {

  private final ConvertsFileToUriString subject = new ConvertsFileToUriString();

  @Test
  public void presentsUrlRepresentationOfFile() throws IOException {
    String expected = "pants";
    File file = File.createTempFile("blerg", expected);

    String result = this.subject.convert(file);

    assertThat(result)
      .startsWith("file:")
      .endsWith(expected);
  }
}
