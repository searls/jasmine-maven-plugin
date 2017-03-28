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

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Collection;

public class Matchers {
  @SuppressWarnings("rawtypes")
  public static Matcher<Collection> empty() {
    return new TypeSafeMatcher<Collection>() {
      @Override
      public boolean matchesSafely(Collection collection) {
        return collection.isEmpty();
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("empty");
      }
    };
  }

  public static Matcher<String> containsScriptTagWithSource(final String src) {
    return new TypeSafeMatcher<String>() {
      @Override
      public boolean matchesSafely(String html) {
        return html.contains("<script type=\"text/javascript\" src=\"" + src + "\"></script>");
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("contains <script/> tag with src='" + src + "'");
      }
    };
  }

  public static Matcher<String> containsLinkTagWithSource(final String src) {
    return new TypeSafeMatcher<String>() {
      @Override
      public boolean matchesSafely(String html) {
        return html.contains("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + src + "\"/>");
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("contains <link/> tag with src='" + src + "'");
      }
    };
  }
}
