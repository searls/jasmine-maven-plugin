package com.github.searls.jasmine.coffee;

import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpHeaders;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.resource.Resource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.searls.jasmine.format.BuildsJavaScriptToWriteFailureHtml;

@RunWith(MockitoJUnitRunner.class)
public class HandlesRequestsForCoffeeTest {
  private static final String COFFEE = "coffee";
  private static final boolean BARE_OPTION = false;

  @InjectMocks HandlesRequestsForCoffee subject = new HandlesRequestsForCoffee();

  @Mock private CoffeeScript coffeeScript = new CoffeeScript();
  @Mock private BuildsJavaScriptToWriteFailureHtml buildsJavaScriptToWriteFailureHtml;

  @Mock private Request baseRequest;
  @Mock(answer=Answers.RETURNS_DEEP_STUBS) private HttpServletResponse response;
  @Mock private Resource resource;

  @Before
  public void stubResourceInputStream() throws IOException {
    when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(COFFEE.getBytes()));
  }

  @Before
  public void defaultCoffeeStubbing() throws IOException {
    when(coffeeScript.compile(COFFEE, BARE_OPTION)).thenReturn("blarg!");
  }

  @Test
  public void setsBaseRequestHandledToTrue() throws IOException {
    subject.handle(baseRequest, response, resource, false);

    verify(baseRequest).setHandled(true);
  }

  @Test
  public void setsMimeToJavaScript() throws IOException {
    subject.handle(baseRequest, response, resource, false);

    verify(response).setContentType("text/javascript");
  }

  @Test
  public void setCharacterEncodingToJavaScript() throws IOException {
    subject.handle(baseRequest, response, resource, BARE_OPTION);

    verify(response).setCharacterEncoding("UTF-8");
  }

  @Test
  public void setsResourceLastModifiedOnResponseHeader() throws IOException {
    long expected = 98123l;
    when(resource.lastModified()).thenReturn(expected);

    subject.handle(baseRequest, response, resource, false);

    verify(response).setDateHeader(HttpHeaders.LAST_MODIFIED, expected);
  }

  @Test
  public void whenCoffeeCompilesThenWriteIt() throws IOException {
    String expected = "javascript";
    when(coffeeScript.compile(COFFEE, BARE_OPTION)).thenReturn(expected);

    subject.handle(baseRequest, response, resource, false);


    verify(response.getWriter()).write(expected);
    verify(response).setHeader(HttpHeaders.CONTENT_LENGTH,Integer.toString(expected.length()));
  }

  @Test
  public void whenCoffeeCompilesHasMultiByteThenWriteIt() throws IOException {
    String expected = "あいうえお.coffee";
    when(coffeeScript.compile(COFFEE, BARE_OPTION)).thenReturn(expected);

    subject.handle(baseRequest, response, resource, BARE_OPTION);

    verify(response.getWriter()).write(expected);
    verify(response).setHeader(HttpHeaders.CONTENT_LENGTH,Integer.toString(expected.getBytes("UTF-8").length));
  }
  
  @Test
  public void whenCoffeeCompilesThenWriteItCoffeeBareOption() throws IOException {
    String expected = "javascript with bare";
    boolean bareTrue = true;
    when(coffeeScript.compile(COFFEE, bareTrue)).thenReturn(expected);

    subject.handle(baseRequest, response, resource, true);
      	
    verify(coffeeScript).compile(COFFEE, bareTrue);
    verify(response.getWriter()).write(expected);
  }
  @Test
  public void whenCoffeeCompilationFailsThenWriteTheErrorOutInItsStead() throws IOException {
    String name = "some-file.coffee";
    String message = "messages";
    String expected = "CoffeeScript Error: failed to compile <code>"+name+"</code>. <br/>Error message:<br/><br/><code>"+message+"</code>";
    when(resource.getName()).thenReturn(name);
    when(coffeeScript.compile(COFFEE, BARE_OPTION)).thenThrow(new RuntimeException(message));
    when(buildsJavaScriptToWriteFailureHtml.build(expected)).thenReturn("win");

    subject.handle(baseRequest, response, resource, false);

    verify(response.getWriter()).write("win");
  }
}
