package com.github.searls.jasmine.collections;

import static java.util.Arrays.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class ScriptSorterTest {

	private ScriptSorter subject = new ScriptSorter();

	@Test
	public void nullIncludesReturnsSame() {
		List<String> list = asList("blah", "pants");

		subject.sort(list, null);

		assertThat(list, is(asList("blah", "pants")));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void emptyIncludesReturnsSame() {
		List<String> list = asList("blah");

		subject.sort(list, Collections.EMPTY_LIST);

		assertThat(list, is(asList("blah")));
	}

	@Test
	public void sortsAnExactMatchOverANonMatch() {
		List<String> list = asList("a.js", "b.js");

		subject.sort(list, asList("b.js"));

		assertThat(list, is(asList("b.js", "a.js")));
	}

	@Test
	public void sortsAnExactMatchOverALaterExactMatch() {
		List<String> list = asList("a.js", "b.js");

		subject.sort(list, asList("b.js", "a.js"));

		assertThat(list, is(asList("b.js", "a.js")));
	}

	@Test
	public void sortsAWildcardMatchOverANonMatch() {
		List<String> list = asList("a.json", "b.js");

		subject.sort(list, asList("*.js"));

		assertThat(list, is(asList("b.js", "a.json")));
	}
	
	@Test
	public void sortsARealWorldTestAsExpected() {
		List<String> list = asList("breadcrumbs.js",
				"category/category-management.js",
				"a.js",
				"customer/customcode/custom-code-preferences.js",
				"customer/view.js",
				"b.js",
				"jquery/cookie/jquery.cookie.js",
				"jquery/jcrumb/jquery.jcrumbs-1.0.js",
				"jquery/underscore/underscore-min-1.1.2.js",
				"utils.js",
				"yui2/animation/animation-min.js",
				"yui2/paginator/paginator.js",
				"yui2/selector/selector-min.js",
				"yui2/yahoo-dom-event/yahoo-dom-event.js",
				"c.js",
				"panda/sad.js");
		
		subject.sort(list,asList("jquery/**/*.js", "yui2/**/*.js", "**/*.js"));
		
		assertThat(list, is(asList("jquery/cookie/jquery.cookie.js",
				"jquery/jcrumb/jquery.jcrumbs-1.0.js",
				"jquery/underscore/underscore-min-1.1.2.js",
				"yui2/animation/animation-min.js",
				"yui2/paginator/paginator.js",
				"yui2/selector/selector-min.js",
				"yui2/yahoo-dom-event/yahoo-dom-event.js",
				"category/category-management.js",
				"customer/customcode/custom-code-preferences.js",
				"customer/view.js",
				"panda/sad.js",
				"a.js",
				"b.js",
				"breadcrumbs.js",
				"c.js",
				"utils.js")));
	}
	
}
