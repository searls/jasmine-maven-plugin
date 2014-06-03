package com.github.searls.jasmine.format;

import java.util.Collection;

public class FormatsScriptTags {

  public String format(Collection<String> sourceLocations) {
    StringBuilder sb = new StringBuilder();
    for (String location : sourceLocations) {
      sb.append("<script type=\"text/javascript\" src=\"").append(location).append("\"></script>").append("\n");
    }
    return sb.toString();
  }

}
