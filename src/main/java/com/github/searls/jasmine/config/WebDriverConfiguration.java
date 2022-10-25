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

import com.github.searls.jasmine.mojo.Capability;
import org.immutables.value.Value;
import org.openqa.selenium.MutableCapabilities;

import javax.annotation.Nullable;
import java.net.URL;
import java.util.List;

@Value.Immutable
public interface WebDriverConfiguration {
  String getWebDriverClassName();
  List<Capability> getWebDriverCapabilities();
  @Nullable
  MutableCapabilities getWebDriverOptions();
  URL getRemoteWebDriverUrl();
}
