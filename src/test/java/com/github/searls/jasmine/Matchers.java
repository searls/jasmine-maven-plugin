package com.github.searls.jasmine;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class Matchers {
	public static Matcher<String> containsScriptTagWith(final String script) {
		return new TypeSafeMatcher<String>() {
			public boolean matchesSafely(String html) {
				return html.contains("<script type=\"text/javascript\">" + script + "</script>");
			}
			public void describeTo(Description description) {
				description.appendText("contains <script/> tag with contents '"+script+"'");
			}
		};
	}
	public static Matcher<String> containsStyleTagWith(final String style) {
		return new TypeSafeMatcher<String>() {
			public boolean matchesSafely(String html) {
				return html.contains("<style type=\"text/css\">" + style + "</style>");
			}
			public void describeTo(Description description) {
				description.appendText("contains <style/> tag with contents '"+style+"'");
			}
		};
	}
}
