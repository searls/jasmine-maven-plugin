package com.github.searls.jasmine.mojo;

import static com.github.searls.jasmine.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.io.File;

import org.apache.maven.plugin.MojoFailureException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.searls.jasmine.exception.StringifiesStackTraces;
import com.github.searls.jasmine.mojo.AbstractJasmineMojo;

@RunWith(MockitoJUnitRunner.class)
public class AbstractJasmineMojoTest {

  @InjectMocks @Spy AbstractJasmineMojo subject = new AbstractJasmineMojo() {
    @Override
		public void run() throws Exception {}
  };
  @Mock private final StringifiesStackTraces stringifiesStackTraces = new StringifiesStackTraces();

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

}
