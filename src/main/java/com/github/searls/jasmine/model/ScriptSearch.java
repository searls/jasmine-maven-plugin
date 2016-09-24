package com.github.searls.jasmine.model;

import org.immutables.value.Value;

import java.io.File;
import java.util.List;

@Value.Immutable
public interface ScriptSearch {
  File getDirectory();
  List<String> getIncludes();
  List<String> getExcludes();
}
