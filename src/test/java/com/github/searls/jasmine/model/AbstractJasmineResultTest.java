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
package com.github.searls.jasmine.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractJasmineResultTest {

  @Test
  public void shouldParseDescriptionWhenSuccessful() {
    AbstractJasmineResult result = ImmutableJasmineResult.builder()
      .details("Some results\nMore results\n1 spec, 0 failures")
      .build();

    assertThat(result.didPass()).isTrue();
  }

  @Test
  public void shouldFailWhenFail() {
    AbstractJasmineResult result = ImmutableJasmineResult.builder()
      .details("Describe Kaka wants 0 failures \nit is Swedish for cookie\nResults: 2 specs, 1 failure")
      .build();

    assertThat(result.didPass()).isFalse();
  }


  @Test
  public void shouldFailWhenMultipleOfTenFails() {
    AbstractJasmineResult result = ImmutableJasmineResult.builder()
      .details("Results: 2 specs, 10 failures")
      .build();

    assertThat(result.didPass()).isFalse();
  }

}
