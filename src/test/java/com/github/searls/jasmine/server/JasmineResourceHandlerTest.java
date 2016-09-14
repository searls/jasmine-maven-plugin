package com.github.searls.jasmine.server;

import com.github.searls.jasmine.config.JasmineConfiguration;
import com.github.searls.jasmine.runner.CreatesRunner;
import org.apache.maven.plugin.logging.Log;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.resource.Resource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
//@PrepareForTest(JasmineResourceHandler.class)
public class JasmineResourceHandlerTest {

  private static final String TARGET = "some url";

  @Mock
  private CreatesRunner createsRunner;

  @Mock
  private JasmineConfiguration configuration;

  @Mock
  Request baseRequest;
  @Mock
  HttpServletRequest request;
  @Mock
  HttpServletResponse response;
  @Mock
  Resource resource;

  @Mock
  Log log;

  private JasmineResourceHandler subject;

  @Before
  public void before() {
    subject = new JasmineResourceHandler(createsRunner) {
      @Override
      protected Resource getResource(HttpServletRequest request) throws MalformedURLException {
        return JasmineResourceHandlerTest.this.resource;
      }
    };
  }

  @Test
  public void whenTargetIsSlashThenCreateManualRunner() throws IOException, ServletException {
    this.subject.handle("/", this.baseRequest, this.request, this.response);

    verify(this.createsRunner).create();
  }

  @Test
  public void whenTargetIsNotSlashThenCreateManualRunner() throws IOException, ServletException {
    this.subject.handle("/notSlash", this.baseRequest, this.request, this.response);

    verify(this.createsRunner, never()).create();
  }

}
