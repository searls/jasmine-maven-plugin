package com.github.searls.jasmine.io;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.isA;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(IOUtils.class)
public class IoUtilitiesIntegrationTest {

  private final IoUtilities subject = new IoUtilities();
  private final InputStream inputStream = mock(InputStream.class);

  @Before
  public void powerfullyMockStaticClasses() {
    mockStatic(IOUtils.class);
  }

  @Test
  public void shouldDelegateToString() throws IOException {
    String expected = "pants";
    when(IOUtils.toString(inputStream)).thenReturn(expected);

    String result = subject.toString(inputStream);

    assertThat(result).isEqualTo(expected);
  }

  @Test
  public void shouldDelegateResourceStringsToString() throws IOException {
    String expected = "banana";
    when(IOUtils.toString(isA(InputStream.class))).thenReturn(expected);

    String result = subject.resourceToString("/ioUtils.txt");

    assertThat(result).isEqualTo(expected);
  }
}
