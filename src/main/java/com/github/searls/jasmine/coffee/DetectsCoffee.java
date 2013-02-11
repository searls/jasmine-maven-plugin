package com.github.searls.jasmine.coffee;

import org.apache.commons.lang3.StringUtils;

public class DetectsCoffee {

  public boolean detect(String path) {
    return StringUtils.endsWith(StringUtils.substringBefore(path,"?"),".coffee");
  }

}
