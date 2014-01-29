package com.github.searls.jasmine.mojo;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Capability {

  private String name;
  private String value;
  private List<String> list;
  private Map<String,Object> map;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public List<String> getList() {
    return list;
  }

  public void setList(List<String> list) {
    this.list = list;
  }

  public Map<String, Object> getMap() {
    return map;
  }

  public void setMap(Map<String, Object> map) {
    for (String key : map.keySet()) {
      String valueStr = (String) map.get(key);
      if (isListString(valueStr)) {
        List<String> listValue = Arrays.asList(valueStr.substring(1, valueStr.length() - 1).split(","));
        map.put(key, listValue);
      } else if (isBooleanString(valueStr)) {
        Boolean booleanValue = Boolean.parseBoolean(valueStr);
        map.put(key, booleanValue);
      }
    }
    this.map = map;
  }
  
  private boolean isListString(String str) {
      return str.charAt(0) == '[' && str.charAt(str.length() - 1) == ']';
  }

  private boolean isBooleanString(String str) {
    return "true".equals(str) || "false".equals(str);
  }
}
