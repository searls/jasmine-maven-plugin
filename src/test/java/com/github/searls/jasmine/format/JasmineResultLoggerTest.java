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
import org.apache.maven.plugin.logging.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class JasmineResultLoggerTest {

  @InjectMocks
  private JasmineResultLogger subject = new JasmineResultLogger();

  @Mock
  private Log log;

  @Test
  public void shouldLogHeader() {
    JasmineResult result = new JasmineResult();

    subject.log(result);

    verify(log).info(JasmineResultLogger.HEADER);
  }

  @Test
  public void shouldLogDetails() {
    String details = "Fake Details";
    JasmineResult result = new JasmineResult();
    result.setDetails(details);

    subject.log(result);

    verify(log).info(details);
  }

  @Test
  public void setterSetsLogger() {
    subject.setLog(log);

    subject.log(new JasmineResult());

    verify(log, atLeastOnce()).info(anyString());
  }

}
