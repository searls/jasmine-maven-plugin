package com.github.searls.jasmine.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ResourceHandler;

import com.github.searls.jasmine.AbstractJasmineMojo;
import com.github.searls.jasmine.CreatesManualRunner;

public class JasmineResourceHandler extends ResourceHandler {

	private CreatesManualRunner createsManualRunner;

	public JasmineResourceHandler(AbstractJasmineMojo config) {
		createsManualRunner = new CreatesManualRunner(config);
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		if("/".equals(target)) {
			createsManualRunner.create();
		}
		
		super.handle(target, baseRequest, request, response);
	}

	
	
}
