package com.github.searls.jasmine;

import java.util.Collection;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

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
				description.appendText("contains <script/> tag with src='"+src+"'");
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
        description.appendText("contains <link/> tag with src='"+src+"'");
      }
    };
  }
}
