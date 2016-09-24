package com.github.searls.jasmine.server;

import com.github.searls.jasmine.config.JasmineConfiguration;
import com.github.searls.jasmine.runner.CreatesRunner;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ResourceHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JasmineResourceHandler extends ResourceHandler {

  private final CreatesRunner createsRunner;
  private final JasmineConfiguration configuration;

  public JasmineResourceHandler(CreatesRunner createsRunner, JasmineConfiguration configuration) {
    this.createsRunner = createsRunner;
    this.configuration = configuration;
  }

  @Override
  public void handle(String target,
                     Request baseRequest,
                     HttpServletRequest request,
                     HttpServletResponse response) throws IOException, ServletException {
    this.createSpecRunnerIfNecessary(target);
    response.addDateHeader("EXPIRES", 0L);
    super.handle(target, baseRequest, baseRequest, response);
  }

  private void createSpecRunnerIfNecessary(String target) throws IOException {
    if ("/".equals(target)) {
      this.createsRunner.create(configuration);
    }
  }

}
