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

			public void describeTo(Description description) {
				description.appendText("empty");
			}
		};
	}
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
	public static Matcher<String> containsScriptTagWithSource(final String src) {
		return new TypeSafeMatcher<String>() {
			public boolean matchesSafely(String html) {
				return html.contains("<script type=\"text/javascript\" src=\"" + src + "\"></script>");
			}
			public void describeTo(Description description) {
				description.appendText("contains <script/> tag with src='"+src+"'");
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
