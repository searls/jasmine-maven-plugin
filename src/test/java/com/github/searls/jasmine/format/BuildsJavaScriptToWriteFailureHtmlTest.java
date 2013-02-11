package com.github.searls.jasmine.format;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.Test;


public class BuildsJavaScriptToWriteFailureHtmlTest {

  private static final String LEFT = "document.write(\""+StringEscapeUtils.escapeEcmaScript("<div class=\"suite spec failed\">");
  private static final String RIGHT = StringEscapeUtils.escapeEcmaScript("</div>")+"\")";

  private final BuildsJavaScriptToWriteFailureHtml subject = new BuildsJavaScriptToWriteFailureHtml();

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
