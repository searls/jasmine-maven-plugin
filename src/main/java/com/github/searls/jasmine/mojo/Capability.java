package com.github.searls.jasmine.mojo;

import java.util.List;
import java.util.Map;

public class Capability {

  private String name;
  private Object value;
  private List<Object> list;
  private Map<String,Object> map;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public List<Object> getList() {
    return list;
  }

  public void setList(List<Object> list) {
    this.list = list;
  }

  public Map<String, Object> getMap() {
    return map;
  }

  public void setMap(Map<String, Object> map) {
    this.map = map;
  }
}
