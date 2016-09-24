package com.github.searls.jasmine.io.scripts;

import java.util.Set;

public interface ScriptResolver {

  String getSourceDirectory();

  String getSpecDirectory();

  String getBaseDirectory();

  Set<String> getSources();

  Set<String> getSpecs();

  Set<String> getPreloads();

  Set<String> getAllScripts();
}
