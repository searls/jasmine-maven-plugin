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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JasmineResultTest {

  private JasmineResult subject = new JasmineResult();
  ;

  @Test
  public void shouldParseDescriptionWhenSuccessful() {
    subject.setDetails(
      "Some results\n" +
        "More results\n" +
        "1 spec, 0 failures");

    boolean success = subject.didPass();

    assertThat(success).isTrue();
  }

  @Test
  public void shouldFailWhenFail() {
    subject.setDetails(
      "Describe Kaka wants 0 failures \n" +
        "it is Swedish for cookie\n" +
        "Results: 2 specs, 1 failure");

    boolean success = subject.didPass();

    assertThat(success).isFalse();
  }


  @Test
  public void shouldFailWhenMultipleOfTenFails() {
    subject.setDetails("Results: 2 specs, 10 failures");

    boolean success = subject.didPass();

    assertThat(success).isFalse();
  }

}
