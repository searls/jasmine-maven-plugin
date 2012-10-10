package com.github.searls.jasmine;

import static com.github.searls.jasmine.Matchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.HashMap;

import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.ReflectionUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.searls.jasmine.exception.StringifiesStackTraces;

@RunWith(MockitoJUnitRunner.class)
public class AbstractJasmineMojoTest {

  @InjectMocks @Spy AbstractJasmineMojo subject = new AbstractJasmineMojo() {
    @Override
    public void run() throws Exception {}
  };
  @Mock private StringifiesStackTraces stringifiesStackTraces = new StringifiesStackTraces();

  @Rule public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void executeStringifiesStackTraces() throws Exception {
    String expected = "panda";
    expectedException.expectMessage(expected);
    Exception e = new Exception();
    when(stringifiesStackTraces.stringify(e)).thenReturn(expected);
    doThrow(e).when(subject).run();

    subject.execute();
  }

  @Test
  public void rethrowsMojoFailureExceptions() throws Exception {
    String expected = "panda";
    expectedException.expect(MojoFailureException.class);
    expectedException.expectMessage(expected);
    MojoFailureException e = new MojoFailureException(expected);
    doThrow(e).when(subject).run();

    subject.execute();
  }

  @Test
  public void setsSourceIncludes() throws Exception {
    subject.execute();

    assertThat(subject.sources.getIncludes(),hasItem("**"+File.separator+"*.js"));
  }

  @Test
  public void setsSourceExcludes() throws Exception {
    subject.execute();

    assertThat(subject.sources.getExcludes(),is(empty()));
  }

  @Test
  public void setsSpecIncludes() throws Exception {
    subject.execute();

    assertThat(subject.specs.getIncludes(),hasItem("**"+File.separator+"*.js"));
  }

  @Test
  public void setsSpecExcludes() throws Exception {
    subject.execute();

    assertThat(subject.specs.getExcludes(),is(empty()));
  }

  @Test
  public void systemProperties() throws Exception {
    HashMap<String, String> systemProperties = new HashMap<String, String>();
    systemProperties.put("testProperty", "foo");
    ReflectionUtils.setVariableValueInObject(subject, "systemProperties",
        systemProperties);
    subject.execute();
    assertThat(System.getProperty("testProperty"), is("foo"));
  }
}
