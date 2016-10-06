package com.github.searls.jasmine.config;

import com.github.klieber.phantomjs.locate.PhantomJsLocatorOptions;
import com.github.klieber.phantomjs.locate.RepositoryDetails;
import com.github.searls.jasmine.mojo.Capability;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface WebDriverConfiguration {
  boolean isDebug();
  String getBrowserVersion();
  String getWebDriverClassName();
  List<Capability> getWebDriverCapabilities();
  PhantomJsLocatorOptions getPhantomJsLocatorOptions();
  RepositoryDetails getRepositoryDetails();
}
