package com.github.searls.jasmine.coffee;

import static org.apache.commons.lang3.StringUtils.*;

public class DetectsCoffee {

  public boolean detect(String path) {
    return endsWith(substringBefore(path,"?"),".coffee");
  }

}
