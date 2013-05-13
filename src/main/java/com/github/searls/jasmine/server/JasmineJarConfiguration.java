package com.github.searls.jasmine.server;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;

public class JasmineJarConfiguration extends WebInfConfiguration
{
  @Override
  protected List<Resource> findJars(final WebAppContext context) throws Exception {
    if (!(context instanceof MetaInfContext)) {
      throw new IllegalArgumentException("Unexpected web app context");
    }

    MetaInfContext metaInfContext = (MetaInfContext) context;
    List<File> files = ((MetaInfContext) context).getJars();
    List<Resource> resources = new ArrayList<Resource>(files.size());
    for (File f : files) {
      resources.add(Resource.newResource(f.toURI()));
    }
    return resources;
  }
}
