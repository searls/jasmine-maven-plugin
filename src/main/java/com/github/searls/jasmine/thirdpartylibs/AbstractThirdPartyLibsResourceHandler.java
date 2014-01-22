package com.github.searls.jasmine.thirdpartylibs;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.http.HttpHeaders;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ResourceHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;

public abstract class AbstractThirdPartyLibsResourceHandler extends ResourceHandler {

  @Override
  public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    if (baseRequest.isHandled())
      return;

    String resourcePath = getResourcePath(target);
    InputStream resource = findResource(resourcePath);

    if (resource != null) {
      String javascript = IOUtils.toString(resource, "UTF-8");
      setHeaders(response, javascript);
      writeResponse(response, javascript);
      baseRequest.setHandled(true);
    }
  }

  abstract protected InputStream findResource(String resourcePath);

  private String getResourcePath(String url) {
    return url.replaceFirst("^/", "");
  }

  private void setHeaders(HttpServletResponse response, String javascript) {
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/javascript");
    response.addDateHeader("EXPIRES", 0L);
    response.setDateHeader(HttpHeaders.LAST_MODIFIED, new Date().getTime());
    try {
      int contentLength = javascript.getBytes("UTF-8").length;
      response.setHeader(HttpHeaders.CONTENT_LENGTH, Integer.toString(contentLength));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("The JVM does not support javascript default encoding.", e);
    }
  }

  private void writeResponse(HttpServletResponse response, String javascript) throws IOException {
    response.getWriter().write(javascript);
  }
}
