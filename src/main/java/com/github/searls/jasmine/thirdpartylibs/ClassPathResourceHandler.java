package com.github.searls.jasmine.thirdpartylibs;

import java.io.InputStream;

public class ClassPathResourceHandler extends AbstractThirdPartyLibsResourceHandler {

  private final ClassLoader projectClassLoader;

  public ClassPathResourceHandler(ClassLoader projectClassLoader) {
    this.projectClassLoader = projectClassLoader;
  }

  @Override
  protected InputStream findResource(String resourcePath) {
    return projectClassLoader.getResourceAsStream(resourcePath);
  }
}
