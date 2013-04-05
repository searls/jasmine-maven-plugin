package com.github.searls.jasmine.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

import com.github.searls.jasmine.coffee.DetectsCoffee;
import com.github.searls.jasmine.coffee.HandlesRequestsForCoffee;
import com.github.searls.jasmine.runner.CreatesRunner;

public class JasmineResourceHandler extends ResourceHandler {

  private final DetectsCoffee detectsCoffee = new DetectsCoffee();
  private final HandlesRequestsForCoffee handlesRequestsForCoffee = new HandlesRequestsForCoffee();
  private final CreatesRunner createsRunner;

  public JasmineResourceHandler(CreatesRunner createsRunner) {
    this.createsRunner = createsRunner;
  }

  @Override
  public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    this.createSpecRunnerIfNecessary(target);
    Resource resource = this.getResource(baseRequest);
    response.addDateHeader("EXPIRES", 0L);
    if (this.detectsCoffee.detect(target) && this.weCanHandleIt(baseRequest, resource)) {
      this.handlesRequestsForCoffee.handle(baseRequest, response, resource);
    } else {
      //Not testable. Who knew test-driving an LSP violation would be this hard. Sigh.  :-(
      super.handle(target, baseRequest, baseRequest, response);
    }
  }

  private void createSpecRunnerIfNecessary(String target) throws IOException {
    if ("/".equals(target)) {
      this.createsRunner.create();
    }
  }

  private boolean weCanHandleIt(Request baseRequest, Resource resource) {
    return !baseRequest.isHandled() && resource != null && resource.exists();
  }

}
