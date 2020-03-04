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
package com.github.searls.jasmine.format;

import com.github.searls.jasmine.model.JasmineResult;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JasmineResultLoggerTest {

  private TestLogger logger = TestLoggerFactory.getTestLogger(JasmineResultLogger.class);

  @Mock
  private JasmineResult jasmineResult;

  @InjectMocks
  private JasmineResultLogger subject = new JasmineResultLogger();

  @After
  public void clearLoggers() {
    TestLoggerFactory.clear();
  }

  @Test
  public void shouldLogHeader() {
    subject.log(jasmineResult);
    assertMessage(JasmineResultLogger.HEADER);
  }

  @Test
  public void shouldLogDetails() {
    String details = "Fake Details";
    when(jasmineResult.getDetails()).thenReturn(details);

    subject.log(jasmineResult);

    assertMessage(JasmineResultLogger.HEADER);
    assertMessage(details);
  }

  private void assertMessage(String ... expectedMessages) {
    List<String> messages = logger.getLoggingEvents()
      .stream()
      .map(loggingEvent -> loggingEvent != null ? loggingEvent.getMessage() : null)
      .collect(Collectors.toList());

    for (String expectedMessage : expectedMessages) {
      Assertions.assertThat(messages)
        .describedAs("Expected %s to be logged.", expectedMessage)
        .contains(expectedMessage);
    }
  }
}
