/*-
 * #%L
 * jasmine-maven-plugin
 * %%
 * Copyright (C) 2010 - 2017 Justin Searls
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.github.searls.jasmine.coffee;

import com.github.searls.jasmine.config.JasmineConfiguration;
import com.github.searls.jasmine.format.BuildsJavaScriptToWriteFailureHtml;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.http.HttpHeaders;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.resource.Resource;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

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
      response.setHeader(HttpHeaders.CONTENT_LENGTH, Integer.toString(contentLength));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(
        "The JVM does not support the compiler's default encoding.", e);
    }

  }

  private String compileCoffee(Resource resource) {
    try {
      return coffeeScript.compile(IOUtils.toString(resource.getInputStream(), "UTF-8"));
    } catch (Exception e) {
      return buildsJavaScriptToWriteFailureHtml.build("CoffeeScript Error: failed to compile <code>" + resource.getName() + "</code>. <br/>Error message:<br/><br/><code>" + e.getMessage() + "</code>");
    }
  }

}
