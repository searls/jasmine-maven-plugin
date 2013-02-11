package com.github.searls.jasmine.io;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isA;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(IOUtils.class)
public class IOUtilsWrapperIntegrationTest {
  private final IOUtilsWrapper subject = new IOUtilsWrapper();
  private final InputStream inputStream = mock(InputStream.class);

  @Before
  public void powerfullyMockStaticClasses() {
    mockStatic(IOUtils.class);
  }

  @Test
  public void shouldDelegateToString() throws IOException {
    String expected = "pants";
    when(IOUtils.toString(inputStream)).thenReturn(expected );

    String result = subject.toString(inputStream);

    assertThat(result,is(expected));
  }

  @Test
  public void shouldDelegateResourceStringsToString() throws IOException {
    String expected = "banana";
    when(IOUtils.toString(isA(InputStream.class))).thenReturn(expected);

    String result = subject.toString("/ioUtils.txt");

    assertThat(result,is(expected));
  }
}
