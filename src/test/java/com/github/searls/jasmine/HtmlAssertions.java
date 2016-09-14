package com.github.searls.jasmine;

import org.assertj.core.api.AbstractCharSequenceAssert;

public class HtmlAssertions extends AbstractCharSequenceAssert<HtmlAssertions, String> {

  public HtmlAssertions(String actual) {
    super(actual, HtmlAssertions.class);
  }

  public HtmlAssertions containsLinkTagWithSource(String src) {
    return this
      .contains("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + src + "\"/>")
      .describedAs("contains <link/> tag with src='" + src + "'");
  }

  public HtmlAssertions containsScriptTagWithSource(String src) {
    return this
      .contains("<script type=\"text/javascript\" src=\"" + src + "\"></script>")
      .describedAs("contains <script/> tag with src='" + src + "'");
  }

  public static HtmlAssertions assertThat(String actual) {
    return new HtmlAssertions(actual);
  }
}
