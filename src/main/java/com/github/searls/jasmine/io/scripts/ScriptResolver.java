package com.github.searls.jasmine.io.scripts;

import java.util.Set;

public interface ScriptResolver {

  String getSourceDirectory() throws ScriptResolverException;

  String getSpecDirectory() throws ScriptResolverException;
  
  String getBaseDirectory() throws ScriptResolverException;

  Set<String> getSources() throws ScriptResolverException;

  Set<String> getSpecs() throws ScriptResolverException;

  Set<String> getPreloads() throws ScriptResolverException;

  Set<String> getAllScripts() throws ScriptResolverException;
}
