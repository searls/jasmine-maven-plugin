package com.github.searls.jasmine.format;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import java.util.HashSet;

import org.junit.Test;

public class FormatsScriptTagsTest {
  private FormatsScriptTags subject = new FormatsScriptTags();

  @Test
  public void formatsOneScriptNicely() {
    String expected = "pantsjs";

    String result = subject.format(new HashSet<String>(asList(expected)));

    assertThat(result,containsString(expectedScriptTagFormat(expected)));
  }

  @Test
  public void formatsTwoScriptsNicely() {
    String first = "A";
    String second = "B";

    String result = subject.format(new HashSet<String>(asList(first,second)));

    assertThat(result,containsString(expectedScriptTagFormat(first)+"\n"+expectedScriptTagFormat(second)));
  }

  private String expectedScriptTagFormat(String scriptName) {
    return expectedScriptTagFormat(scriptName, "text/javascript");
  }

  private String expectedScriptTagFormat(String scriptName, String scriptType) {
    return "<script type=\""+scriptType+"\" src=\""+scriptName+"\"></script>";
  }

}
