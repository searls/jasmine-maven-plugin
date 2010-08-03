package searls.jasmine.runner;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

import searls.jasmine.model.JasmineResult;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

public class SpecRunnerExecutorTest {

	private SpecRunnerExecutor executor = new SpecRunnerExecutor();
	
	@Test
	public void shouldFindSpecsInResults() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		URL resource = getClass().getResource("/example_nested_specrunner.html");
		JasmineResult result = executor.execute(resource);
		
		assertThat(result,is(not(nullValue())));
		assertThat(result.getDescription(),startsWith("5 specs, 4 failures"));
		assertThat(result.didPass(),is(false));
		
	}
	
}
