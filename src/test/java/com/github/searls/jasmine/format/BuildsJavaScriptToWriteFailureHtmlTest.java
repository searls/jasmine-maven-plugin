package com.github.searls.jasmine.format;

import org.apache.commons.lang3.StringEscapeUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class BuildsJavaScriptToWriteFailureHtmlTest {

  private static final String LEFT = "document.write(\"" + StringEscapeUtils.escapeEcmaScript("<div class=\"suite spec failed\">");
  private static final String RIGHT = StringEscapeUtils.escapeEcmaScript("</div>") + "\")";

  private final BuildsJavaScriptToWriteFailureHtml subject = new BuildsJavaScriptToWriteFailureHtml();

  @Test
  public void whenNothingIsPassedYouGetAnEmptyDiv() {
    Assertions.assertThat(subject.build("")).isEqualTo(LEFT + RIGHT);
  }

  @Test
  public void printsASimpleMessage() {
    Assertions.assertThat(subject.build("pants")).isEqualTo(LEFT + "pants" + RIGHT);
  }

  @Test
  public void esacpesMessageString() {
    Assertions.assertThat(subject.build("<a>pant's</a>")).isEqualTo(LEFT + "<a>pant\\'s<\\/a>" + RIGHT);
  }

}
