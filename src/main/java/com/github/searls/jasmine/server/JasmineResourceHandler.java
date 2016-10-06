package com.github.searls.jasmine.server;

import com.github.searls.jasmine.runner.CreatesRunner;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JasmineResourceHandler extends ResourceHandler {

  private final CreatesRunner createsRunner;

  public JasmineResourceHandler(CreatesRunner createsRunner) {
    this.createsRunner = createsRunner;
  }

  @Override
  public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    this.createSpecRunnerIfNecessary(target);
    Resource resource = this.getResource(baseRequest);
    response.addDateHeader("EXPIRES", 0L);
    //Not testable. Who knew test-driving an LSP violation would be this hard. Sigh.  :-(
    super.handle(target, baseRequest, baseRequest, response);
  }

  private void createSpecRunnerIfNecessary(String target) throws IOException {
    if ("/".equals(target)) {
      this.createsRunner.create();
    }
  }
}
