package com.github.searls.jasmine.format;

import org.apache.commons.lang.StringEscapeUtils;

public class BuildsJavaScriptToWriteFailureHtml {

	public String build(String html) {
		return "document.write(\""+StringEscapeUtils.escapeJavaScript("<div class=\"suite spec failed\">"+html+"</div>")+"\")";
	}

}
