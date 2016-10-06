package com.github.searls.jasmine.config;

import org.immutables.value.Value;

import java.net.MalformedURLException;
import java.net.URL;

@Value.Immutable
public abstract class ServerConfiguration {
  public abstract String getUriScheme();
  public abstract String getServerHostname();
  public abstract int getServerPort();

  public URL getServerURL() throws MalformedURLException {
    return new URL(this.getUriScheme() + "://" + this.getServerHostname() + ":" + getServerPort());
  }
}
