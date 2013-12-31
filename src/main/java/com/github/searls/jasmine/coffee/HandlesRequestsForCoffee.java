package com.github.searls.jasmine.coffee;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.http.HttpHeaders;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.resource.Resource;

import com.github.searls.jasmine.config.JasmineConfiguration;
import com.github.searls.jasmine.format.BuildsJavaScriptToWriteFailureHtml;
import com.github.searls.jasmine.runner.SpecRunnerTemplate;

public class HandlesRequestsForCoffee {

  private CoffeeScript coffeeScript = new CoffeeScript();
  private BuildsJavaScriptToWriteFailureHtml buildsJavaScriptToWriteFailureHtml = new BuildsJavaScriptToWriteFailureHtml();
  private JasmineConfiguration configuration;

  public HandlesRequestsForCoffee(JasmineConfiguration configuration) {
	  this.configuration = configuration;
  }
  
  public void handle(Request baseRequest, HttpServletResponse response, Resource resource) throws IOException {
    baseRequest.setHandled(true);
    String javascript = null;
    if (!configuration.isCoffeeScriptCompilationEnabled()) {
    	// CoffeeScript RequireJS plugin should be used for translation
    	javascript = IOUtils.toString(resource.getInputStream(), "UTF-8");
    } else {
        javascript = compileCoffee(resource);
    }
    setHeaders(response, resource, javascript);
    writeResponse(response, javascript);
  }

  private void writeResponse(HttpServletResponse response, String javascript) throws IOException {
    response.getWriter().write(javascript);
  }

  private void setHeaders(HttpServletResponse response, Resource resource, String javascript) {
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/javascript");
    response.setDateHeader(HttpHeaders.LAST_MODIFIED, resource.lastModified());
    try {
      int contentLength = javascript.getBytes("UTF-8").length;
      response.setHeader(HttpHeaders.CONTENT_LENGTH,Integer.toString(contentLength));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(
        "The JVM does not support the compiler's default encoding.", e);
    }

  }

  private String compileCoffee(Resource resource) {
    try {
      return coffeeScript.compile(IOUtils.toString(resource.getInputStream(), "UTF-8"));
    } catch(Exception e) {
      return buildsJavaScriptToWriteFailureHtml.build("CoffeeScript Error: failed to compile <code>"+resource.getName()+"</code>. <br/>Error message:<br/><br/><code>"+e.getMessage()+"</code>");
    }
  }

}
