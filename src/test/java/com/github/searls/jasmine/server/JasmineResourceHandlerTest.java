package com.github.searls.jasmine.server;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.searls.jasmine.coffee.DetectsCoffee;
import com.github.searls.jasmine.coffee.HandlesRequestsForCoffee;
import com.github.searls.jasmine.runner.CreatesRunner;

@RunWith(PowerMockRunner.class)
//@PrepareForTest(JasmineResourceHandler.class)
public class JasmineResourceHandlerTest {
  private static final String TARGET = "some url";

  @Mock private DetectsCoffee detectsCoffee;
  @Mock private HandlesRequestsForCoffee handlesRequestsForCoffee;
  @Mock private CreatesRunner createsRunner;

  @Mock Request baseRequest;
  @Mock HttpServletRequest request;
  @Mock HttpServletResponse response;
  @Mock Resource resource;

  @Mock Log log;

  @InjectMocks private final JasmineResourceHandler subject = new JasmineResourceHandler(createsRunner) {
    @Override
    protected Resource getResource(HttpServletRequest request) throws MalformedURLException {
      return JasmineResourceHandlerTest.this.resource;
    }
  };

  //  @Test
  //  public void constructorSetsLoggingLow() throws Exception {
  //    new JasmineResourceHandler(createsRunner);
  //    assertThat(Whitebox.getInternalState(createsRunner, Log.class), is(instanceOf(NullLog.class)));
  //  }

  @Test
  public void whenTargetIsSlashThenCreateManualRunner() throws IOException, ServletException {
    this.subject.handle("/", this.baseRequest,this.request,this.response);

    verify(this.createsRunner).create();
  }

  @Test
  public void whenTargetIsNotSlashThenCreateManualRunner() throws IOException, ServletException {
    this.subject.handle("/notSlash", this.baseRequest,this.request,this.response);

    verify(this.createsRunner,never()).create();
  }

  @Test
  public void whenCoffeeDelegatesToCoffeeHandler() throws IOException, ServletException {
    when(this.detectsCoffee.detect(TARGET)).thenReturn(true);
    when(this.resource.exists()).thenReturn(true);

    this.subject.handle(TARGET, this.baseRequest,this.request,this.response);

    verify(this.handlesRequestsForCoffee).handle(this.baseRequest, this.response, this.resource);
  }

  @Test
  public void whenNotCoffeeDoesNotDelegateToCoffeeHandler() throws IOException, ServletException {
    when(this.detectsCoffee.detect(TARGET)).thenReturn(false);
    when(this.resource.exists()).thenReturn(true);

    this.subject.handle(TARGET, this.baseRequest,this.request,this.response);

    verify(this.handlesRequestsForCoffee, never()).handle(any(Request.class), any(HttpServletResponse.class), any(Resource.class));
  }

  @Test
  public void whenCoffeeButResourceIsHandledDoNotDelegateToCoffeeHandler() throws IOException, ServletException {
    when(this.detectsCoffee.detect(TARGET)).thenReturn(true);
    when(this.resource.exists()).thenReturn(true);
    when(this.baseRequest.isHandled()).thenReturn(true);

    this.subject.handle(TARGET, this.baseRequest,this.request,this.response);

    verify(this.handlesRequestsForCoffee, never()).handle(any(Request.class), any(HttpServletResponse.class), any(Resource.class));
  }

  @Test
  public void whenCoffeeButDoesNotExistDoNotDelegateToCoffeeHandler() throws IOException, ServletException {
    when(this.detectsCoffee.detect(TARGET)).thenReturn(true);
    when(this.resource.exists()).thenReturn(false);

    this.subject.handle(TARGET, this.baseRequest,this.request,this.response);

    verify(this.handlesRequestsForCoffee, never()).handle(any(Request.class), any(HttpServletResponse.class), any(Resource.class));
  }

  @Test
  public void whenCoffeeButResourceIsNullDoNotDelegateToCoffeeHandler() throws IOException, ServletException {
    when(this.detectsCoffee.detect(TARGET)).thenReturn(true);
    this.resource = null;

    this.subject.handle(TARGET, this.baseRequest,this.request,this.response);

    verify(this.handlesRequestsForCoffee, never()).handle(any(Request.class), any(HttpServletResponse.class), any(Resource.class));
  }

}
