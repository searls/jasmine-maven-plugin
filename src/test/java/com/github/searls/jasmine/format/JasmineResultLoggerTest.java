package com.github.searls.jasmine.format;

import com.github.searls.jasmine.model.JasmineResult;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import javax.annotation.Nullable;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JasmineResultLoggerTest {

  private static final Function<LoggingEvent, String> EVENT_MESSAGE_FUNCTION = new Function<LoggingEvent, String>() {
    @Nullable
    @Override
    public String apply(@Nullable LoggingEvent loggingEvent) {
      return loggingEvent != null ? loggingEvent.getMessage() : null;
    }
  };

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
    List<String> messages = Lists.transform(logger.getLoggingEvents(), EVENT_MESSAGE_FUNCTION);
    for (String expectedMessage : expectedMessages) {
      Assertions.assertThat(messages)
        .describedAs("Expected %s to be logged.", expectedMessage)
        .contains(expectedMessage);
    }
  }
}
