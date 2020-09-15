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
package com.github.searls.jasmine;

import org.assertj.core.api.AbstractCharSequenceAssert;

public class HtmlAssertions extends AbstractCharSequenceAssert<HtmlAssertions, String> {

  public HtmlAssertions(String actual) {
    super(actual, HtmlAssertions.class);
  }

  public HtmlAssertions containsLinkTagWithSource(String src) {
    return this
      .contains("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + src + "\"/>")
      .describedAs("contains <link/> tag with src='" + src + "'");
  }

  public HtmlAssertions containsScriptTagWithSource(String src) {
    return this
      .contains("<script type=\"application/javascript\" src=\"" + src + "\"></script>")
      .describedAs("contains <script/> tag with src='" + src + "'");
  }

  public static HtmlAssertions assertThat(String actual) {
    return new HtmlAssertions(actual);
  }
}
