package com.github.searls.jasmine.format;

import java.util.Set;

public class FormatsScriptTags {

  public String format(Set<String> sourceLocations) {
    StringBuilder sb = new StringBuilder();
    for (String location : sourceLocations) {
      sb.append("<script type=\"text/javascript\" src=\"").append(location).append("\"></script>").append("\n");
    }
    return sb.toString();
  }

}
