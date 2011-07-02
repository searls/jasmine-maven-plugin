package com.github.searls.jasmine.server;

import static org.mockito.Mockito.*;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.searls.jasmine.CreatesManualRunner;

@RunWith(MockitoJUnitRunner.class)
public class JasmineResourceHandlerTest {
	@InjectMocks private JasmineResourceHandler subject = new JasmineResourceHandler(null);
	
	@Mock private CreatesManualRunner createsManualRunner;
	
	@Mock Request request;
	@Mock HttpServletRequest httpServletRequest;
	@Mock HttpServletResponse httpServletResponse;
	
	@Test
	public void whenTargetIsSlashThenCreateManualRunner() throws IOException, ServletException {
		subject.handle("/", request,httpServletRequest,httpServletResponse);
		
		verify(createsManualRunner).create();
	}
	
	@Test
	public void whenTargetIsNotSlashThenCreateManualRunner() throws IOException, ServletException {
		subject.handle("/notSlash", request,httpServletRequest,httpServletResponse);
		
		verify(createsManualRunner,never()).create();
	}
	
}
