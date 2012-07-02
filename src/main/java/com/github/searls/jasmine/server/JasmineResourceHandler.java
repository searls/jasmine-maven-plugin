package com.github.searls.jasmine.server;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

import com.github.searls.jasmine.AbstractJasmineMojo;
import com.github.searls.jasmine.CreatesManualRunner;
import com.github.searls.jasmine.NullLog;
import com.github.searls.jasmine.coffee.DetectsCoffee;
import com.github.searls.jasmine.coffee.HandlesRequestsForCoffee;
import com.github.searls.jasmine.model.ScriptSearch;
import com.github.searls.jasmine.runner.SpecRunnerHtmlGeneratorFactory;

public class JasmineResourceHandler extends ResourceHandler {

	private DetectsCoffee detectsCoffee = new DetectsCoffee();
	private HandlesRequestsForCoffee handlesRequestsForCoffee = new HandlesRequestsForCoffee();
	private CreatesManualRunner createsManualRunner;
	private ScriptSearch sources;
	private boolean isRequireJs;

	public JasmineResourceHandler(AbstractJasmineMojo config) {
		createsManualRunner = new CreatesManualRunner(config);
		createsManualRunner.setLog(new NullLog());
		sources = config.getSources();
		isRequireJs = SpecRunnerHtmlGeneratorFactory.REQUIRE_JS.equals(config.getSpecRunnerTemplate());
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		createManualSpecRunnerIfNecessary(target);
		Resource resource = getResource(baseRequest);
		response.addDateHeader("EXPIRES", 0L);
		if (detectsCoffee.detect(target) && weCanHandleIt(baseRequest, resource)) {
			handlesRequestsForCoffee.handle(baseRequest, response, resource);
		} else {
			//Not testable. Who knew test-driving an LSP violation would be this hard. Sigh.  :-(
			super.handle(target, baseRequest, baseRequest, response);
		}
	}

	private void createManualSpecRunnerIfNecessary(String target) throws IOException {
		if ("/".equals(target)) {
			createsManualRunner.create();
		}
	}

	private boolean weCanHandleIt(Request baseRequest, Resource resource) {
		if (isRequireJs && isSource(resource)) {
			// CoffeeScript plugin should be used for translation
			return false;
		}
		return !baseRequest.isHandled() && resource != null && resource.exists();
	}

	private boolean isSource(Resource resource) {
		File sourceDirectory = sources.getDirectory();
		File file;
		try {
			file = resource.getFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		while ((file = file.getParentFile()) != null) {
			if (file.equals(sourceDirectory)) {
				return true;
			}
		}
		return false;
	}

}
