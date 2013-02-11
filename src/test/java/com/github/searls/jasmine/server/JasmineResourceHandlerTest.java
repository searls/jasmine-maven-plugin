package com.github.searls.jasmine.server;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.maven.plugin.logging.Log;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.resource.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.searls.jasmine.AbstractJasmineMojo;
import com.github.searls.jasmine.CreatesManualRunner;
import com.github.searls.jasmine.NullLog;
import com.github.searls.jasmine.coffee.DetectsCoffee;
import com.github.searls.jasmine.coffee.HandlesRequestsForCoffee;

@RunWith(PowerMockRunner.class)
@PrepareForTest(JasmineResourceHandler.class)
public class JasmineResourceHandlerTest {
  private static final String TARGET = "some url";

  @Mock private DetectsCoffee detectsCoffee;
  @Mock private HandlesRequestsForCoffee handlesRequestsForCoffee;
  @Mock private CreatesManualRunner createsManualRunner;

  @Mock AbstractJasmineMojo config;
  @Mock Request baseRequest;
  @Mock HttpServletRequest request;
  @Mock HttpServletResponse response;
  @Mock Resource resource;

  @Mock Log log;

  @InjectMocks private JasmineResourceHandler subject = new JasmineResourceHandler(mock(AbstractJasmineMojo.class)) {
    protected Resource getResource(HttpServletRequest request) throws MalformedURLException {
      return resource;
    }
  };

  @Test
  public void constructorSetsLoggingLow() throws Exception {
    whenNew(CreatesManualRunner.class).withArguments(config).thenReturn(createsManualRunner);

    new JasmineResourceHandler(config);

    verify(createsManualRunner).setLog((Log) argThat(is(NullLog.class)));
  }

  @Test
  public void whenTargetIsSlashThenCreateManualRunner() throws IOException, ServletException {
    subject.handle("/", baseRequest,request,response);

    verify(createsManualRunner).create();
  }

  @Test
  public void whenTargetIsNotSlashThenCreateManualRunner() throws IOException, ServletException {
    subject.handle("/notSlash", baseRequest,request,response);

    verify(createsManualRunner,never()).create();
  }

  @Test
  public void whenCoffeeDelegatesToCoffeeHandler() throws IOException, ServletException {
    when(detectsCoffee.detect(TARGET)).thenReturn(true);
    when(resource.exists()).thenReturn(true);

    subject.handle(TARGET, baseRequest,request,response);

    verify(handlesRequestsForCoffee).handle(baseRequest, response, resource);
  }

  @Test
  public void whenNotCoffeeDoesNotDelegateToCoffeeHandler() throws IOException, ServletException {
    when(detectsCoffee.detect(TARGET)).thenReturn(false);
    when(resource.exists()).thenReturn(true);

    subject.handle(TARGET, baseRequest,request,response);

    verify(handlesRequestsForCoffee, never()).handle(any(Request.class), any(HttpServletResponse.class), any(Resource.class));
  }

  @Test
  public void whenCoffeeButResourceIsHandledDoNotDelegateToCoffeeHandler() throws IOException, ServletException {
    when(detectsCoffee.detect(TARGET)).thenReturn(true);
    when(resource.exists()).thenReturn(true);
    when(baseRequest.isHandled()).thenReturn(true);

    subject.handle(TARGET, baseRequest,request,response);

    verify(handlesRequestsForCoffee, never()).handle(any(Request.class), any(HttpServletResponse.class), any(Resource.class));
  }

  @Test
  public void whenCoffeeButDoesNotExistDoNotDelegateToCoffeeHandler() throws IOException, ServletException {
    when(detectsCoffee.detect(TARGET)).thenReturn(true);
    when(resource.exists()).thenReturn(false);

    subject.handle(TARGET, baseRequest,request,response);

    verify(handlesRequestsForCoffee, never()).handle(any(Request.class), any(HttpServletResponse.class), any(Resource.class));
  }

  @Test
  public void whenCoffeeButResourceIsNullDoNotDelegateToCoffeeHandler() throws IOException, ServletException {
    when(detectsCoffee.detect(TARGET)).thenReturn(true);
    this.resource = null;

    subject.handle(TARGET, baseRequest,request,response);

    verify(handlesRequestsForCoffee, never()).handle(any(Request.class), any(HttpServletResponse.class), any(Resource.class));
  }

}
