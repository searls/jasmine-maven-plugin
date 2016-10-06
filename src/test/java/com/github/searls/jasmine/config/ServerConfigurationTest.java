package com.github.searls.jasmine.config;

import org.junit.Test;

import java.net.MalformedURLException;

import static org.assertj.core.api.Assertions.assertThat;

public class ServerConfigurationTest {

  private static final String SCHEME = "http";
  private static final String HOSTNAME = "example.com";
  private static final int PORT = 1234;
  private static final String SERVER_URL = SCHEME+"://"+HOSTNAME+":"+PORT;

  @Test
  public void testServiceConfiguration() throws MalformedURLException {
    ServerConfiguration config = ImmutableServerConfiguration.builder()
      .uriScheme(SCHEME)
      .serverHostname(HOSTNAME)
      .serverPort(PORT)
      .build();

    assertThat(config.getUriScheme()).isEqualTo(SCHEME);
    assertThat(config.getServerHostname()).isEqualTo(HOSTNAME);
    assertThat(config.getServerPort()).isEqualTo(PORT);
    assertThat(config.getServerURL().toString()).isEqualTo(SERVER_URL);
  }

}
