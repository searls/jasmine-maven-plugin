package com.github.searls.jasmine.format;

import org.apache.commons.lang3.StringEscapeUtils;

public class BuildsJavaScriptToWriteFailureHtml {

  public String build(String html) {
    return "document.write(\""+StringEscapeUtils.escapeEcmaScript("<div class=\"suite spec failed\">"+html+"</div>")+"\")";
  }

}
