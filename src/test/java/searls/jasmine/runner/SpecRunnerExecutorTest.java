package searls.jasmine.runner;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.junit.Test;

import searls.jasmine.model.JasmineResult;
import searls.jasmine.model.Spec;
import searls.jasmine.model.Suite;
import searls.jasmine.model.TestResultItem;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

public class SpecRunnerExecutorTest {

	private SpecRunnerExecutor executor = new SpecRunnerExecutor();
	
	@Test
	public void shouldFindSpecsInResults() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		URL resource = getClass().getResource("/example_nested_specrunner.html");
		JasmineResult result = executor.execute(resource.toString());
		
		assertThat(result,is(not(nullValue())));
		assertThat(result.getDescription(),startsWith("5 specs, 4 failures in 0.033s"));
		assertThat(result.didPass(),is(false));
		
		List<TestResultItem> specsAndSuites = result.getChildren();
		assertThat(specsAndSuites.size(),is(2));
		
		Suite failSpec = (Suite) specsAndSuites.get(0);
		assertThat(failSpec.getDescription(),is("FailSpec"));
		assertThat(failSpec.getChildren().size(),is(2));
		
		Suite nestedFail = (Suite) failSpec.getChildren().get(0);
		assertThat(nestedFail.getDescription(),is("NestedFail"));
		assertThat(nestedFail.getChildren().size(),is(1));
		
		Spec nestedFailSpec = (Spec) nestedFail.getChildren().get(0);
		assertThat(nestedFailSpec.getDescription(),is("FailSpec NestedFail should fail deeply."));
		assertThat(nestedFailSpec.getMessages(),hasItems("Expected true to be false."));
		
		Spec failSpecShouldFail = (Spec) failSpec.getChildren().get(1);
		assertThat(failSpecShouldFail.getDescription(),is("FailSpec should fail."));
		assertThat(failSpecShouldFail.didPass(),is(false));
		assertThat(failSpecShouldFail.getMessages(),hasItems("Expected true to be false."));
		
		Suite helloWorldSuite = (Suite) specsAndSuites.get(1);
		assertThat(helloWorldSuite.getDescription(),is("HelloWorld"));
		assertThat(helloWorldSuite.didPass(),is(false));
		assertThat(helloWorldSuite.getChildren().size(),is(3));
		
		Spec passingHelloSpec = (Spec) helloWorldSuite.getChildren().get(0);
		assertThat(passingHelloSpec.didPass(),is(true));
		assertThat(passingHelloSpec.getDescription(),is("HelloWorld should say hello."));
		assertThat(passingHelloSpec.getMessages().size(),is(0));
		
		Spec failingGoodbyeSpec = (Spec) helloWorldSuite.getChildren().get(1);
		assertThat(failingGoodbyeSpec.didPass(),is(false));
		assertThat(failingGoodbyeSpec.getDescription(),is("HelloWorld should say goodbye."));
		assertThat(failingGoodbyeSpec.getMessages(),hasItems("Expected 'Hello, World' to be 'Goodbye, World'."));
		
		Spec failingGoodbyeSpec2 = (Spec) helloWorldSuite.getChildren().get(2);
		assertThat(failingGoodbyeSpec2.didPass(),is(false));
		assertThat(failingGoodbyeSpec2.getDescription(),is("HelloWorld should fail."));
		assertThat(failingGoodbyeSpec2.getMessages(),hasItems("Expected 5 to be 6."));
	}
	
}
