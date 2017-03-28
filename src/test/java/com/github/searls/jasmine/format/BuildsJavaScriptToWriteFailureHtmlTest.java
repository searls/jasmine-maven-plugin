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

import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class BuildsJavaScriptToWriteFailureHtmlTest {

  private static final String LEFT = "document.write(\"" + StringEscapeUtils.escapeEcmaScript("<div class=\"suite spec failed\">");
  private static final String RIGHT = StringEscapeUtils.escapeEcmaScript("</div>") + "\")";

  private final BuildsJavaScriptToWriteFailureHtml subject = new BuildsJavaScriptToWriteFailureHtml();

  @Test
  public void whenNothingIsPassedYouGetAnEmptyDiv() {
    assertThat(subject.build(""), is(LEFT + RIGHT));
  }

  @Test
  public void printsASimpleMessage() {
    assertThat(subject.build("pants"), is(LEFT + "pants" + RIGHT));
  }

  @Test
  public void esacpesMessageString() {
    assertThat(subject.build("<a>pant's</a>"), is(LEFT + "<a>pant\\'s<\\/a>" + RIGHT));
  }

}
