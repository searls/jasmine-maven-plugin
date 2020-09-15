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
package com.github.searls.jasmine.format;

import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class FormatsScriptTagsTest {
  private FormatsScriptTags subject = new FormatsScriptTags();

  @Test
  public void formatsOneScriptNicely() {
    String expected = "pantsjs";

    String result = subject.format(new HashSet<>(Collections.singletonList(expected)));

    assertThat(result).contains(expectedScriptTagFormat(expected));
  }

  @Test
  public void formatsTwoScriptsNicely() {
    String first = "A";
    String second = "B";

    String result = subject.format(new HashSet<>(asList(first, second)));

    assertThat(result)
      .contains(expectedScriptTagFormat(first) + "\n" + expectedScriptTagFormat(second));
  }

  private String expectedScriptTagFormat(String scriptName) {
    return expectedScriptTagFormat(scriptName, "application/javascript");
  }

  private String expectedScriptTagFormat(String scriptName, String scriptType) {
    return "<script type=\"" + scriptType + "\" src=\"" + scriptName + "\"></script>";
  }

}
