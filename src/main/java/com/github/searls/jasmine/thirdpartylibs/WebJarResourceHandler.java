package com.github.searls.jasmine.thirdpartylibs;

import org.webjars.WebJarAssetLocator;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class WebJarResourceHandler extends AbstractThirdPartyLibsResourceHandler {

  private static final Pattern WILDCARD = Pattern.compile(".*");

  private final List<WebJarAssetLocator> webJarAssetLocators;
  private final ClassLoader projectClassLoader;

  public WebJarResourceHandler(ClassLoader projectClassLoader) {
    this.projectClassLoader = projectClassLoader;
    this.webJarAssetLocators = createWebJarAssetLocators(
      WebJarResourceHandler.class.getClassLoader(),
      projectClassLoader
    );
  }

  @Override
  protected InputStream findResource(String resourcePath) {
    String fullPath = findFullPath(resourcePath);
    return fullPath != null ? projectClassLoader.getResourceAsStream(fullPath) : null;
  }

  private String findFullPath(String resourcePath) {
    String fullPath = null;
    for (WebJarAssetLocator locator : webJarAssetLocators) {
      fullPath = findFullPath(locator, resourcePath);
      if (fullPath != null) {
        break;
      }
    }
    return fullPath;
  }

  private String findFullPath(WebJarAssetLocator locator, String resourcePath) {
    String fullPath = null;
    try {
      fullPath = locator.getFullPath(resourcePath);
    } catch (Exception e) {
      fullPath = findFullPathByPartial(locator, resourcePath);
    }
    return fullPath;
  }

  private String findFullPathByPartial(WebJarAssetLocator locator, String resourcePath) {

    String fullPath = null;

    int splitAt = resourcePath.indexOf("/");

    if (splitAt > 0) {
      String webjar = resourcePath.substring(0, splitAt);
      String partialPath = resourcePath.substring(splitAt + 1);
      try {
        fullPath = locator.getFullPath(webjar, partialPath);
      } catch (Exception e) {
        // TODO: at least log this exception
      }
    }

    return fullPath;
  }

  private List<WebJarAssetLocator> createWebJarAssetLocators(ClassLoader... classLoaders) {
    List<WebJarAssetLocator> locators = new ArrayList<WebJarAssetLocator>();
    for (ClassLoader classLoader : classLoaders) {
      locators.add(new WebJarAssetLocator(WebJarAssetLocator.getFullPathIndex(WILDCARD, classLoader)));
    }
    return locators;
  }
}
