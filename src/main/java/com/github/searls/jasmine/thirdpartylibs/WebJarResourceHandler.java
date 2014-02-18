package com.github.searls.jasmine.thirdpartylibs;

import org.webjars.WebJarAssetLocator;

import java.io.InputStream;
import java.util.SortedMap;
import java.util.regex.Pattern;

import static org.webjars.WebJarAssetLocator.getFullPathIndex;

public class WebJarResourceHandler extends AbstractThirdPartyLibsResourceHandler {

  private final WebJarAssetLocator webJarAssetLocator;
  private final ClassLoader projectClassLoader;

  public WebJarResourceHandler(ClassLoader projectClassLoader) {
    this.projectClassLoader = projectClassLoader;
    webJarAssetLocator = createWebJarAssetLocator();
  }

  @Override
  protected InputStream findResource(String resourcePath) {
    String fullPath;
    try {
      fullPath = webJarAssetLocator.getFullPath(resourcePath);
    } catch (Exception ignoreToRespondWith404) {
      return null;
    }
    return projectClassLoader.getResourceAsStream(fullPath);
  }

  private WebJarAssetLocator createWebJarAssetLocator() {
    SortedMap<String, String> fullPathIndex = getFullPathIndex(Pattern.compile(".*"), projectClassLoader);
    return new WebJarAssetLocator(fullPathIndex);
  }
}
