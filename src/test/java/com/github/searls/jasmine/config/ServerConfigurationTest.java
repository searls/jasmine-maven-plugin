/*-
 * #%L
 * jasmine-maven-plugin
 * %%
 * Copyright (C) 2010 - 2017 Justin Searls
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.github.searls.jasmine.config;

import org.junit.jupiter.api.Test;

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
