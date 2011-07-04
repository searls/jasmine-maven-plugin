package com.github.searls.jasmine.format;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Test;


public class BuildsJavaScriptToWriteFailureHtmlTest {

	private static final String LEFT = "document.write(\""+StringEscapeUtils.escapeJavaScript("<div class=\"suite spec failed\">");
	private static final String RIGHT = StringEscapeUtils.escapeJavaScript("</div>")+"\")";
	
	private BuildsJavaScriptToWriteFailureHtml subject = new BuildsJavaScriptToWriteFailureHtml();
	
	@Test
	public void whenNothingIsPassedYouGetAnEmptyDiv() {
		assertThat(subject.build(""),is(LEFT+RIGHT));
	}

	@Test
	public void printsASimpleMessage() {
		assertThat(subject.build("pants"),is(LEFT+"pants"+RIGHT));
	}
	
	@Test
	public void esacpesMessageString() {
		assertThat(subject.build("<a>pant's</a>"),is(LEFT+"<a>pant\\'s<\\/a>"+RIGHT));
	}
	
}
