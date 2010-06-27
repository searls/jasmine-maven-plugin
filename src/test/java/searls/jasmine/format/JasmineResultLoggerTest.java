package searls.jasmine.format;

import static org.mockito.Mockito.*;

import org.apache.maven.plugin.logging.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import searls.jasmine.model.JasmineResult;
import searls.jasmine.model.Spec;
import searls.jasmine.model.Suite;

@RunWith(MockitoJUnitRunner.class)
public class JasmineResultLoggerTest {

	@InjectMocks private JasmineResultLogger resultLogger = new JasmineResultLogger();
	@Mock private Log log;
	
	@Test
	public void shouldLogHeader() {
		JasmineResult result = new JasmineResult();
		result.setDescription("");
		
		resultLogger.log(result);
		
		verify(log).info(JasmineResultLogger.HEADER);
	}
	
	@Test
	public void shouldLogEmptyResultInTrivialWay() {
		String description = "Fake Result";
		JasmineResult result = new JasmineResult();
		result.setDescription(description);
		
		resultLogger.log(result);
		
		verify(log).info("\nResults:\n\n"+description+"");
	}
	
	@Test
	public void shouldLogPassingChildResult() {
		JasmineResult result = new JasmineResult();
		result.setDescription("");
		Spec spec = new Spec();
		spec.setDescription("Parent Child Spec");
		spec.setPassed(true);
		result.addChild(spec);

		resultLogger.log(result);
		
		verify(log).info("Spec "+spec.getDescription());		
	}

	@Test
	public void shouldLogFailingChildResult() {
		JasmineResult result = new JasmineResult();
		result.setDescription("");
		Spec spec = new Spec();
		spec.setDescription("Parent Child Spec");
		spec.setPassed(false);

		result.addChild(spec);

		resultLogger.log(result);
		
		verify(log).info("Spec "+spec.getDescription()+JasmineResultLogger.FAIL_APPENDAGE);		
	}
	
	@Test
	public void shouldIndentNestedSuites() {
		JasmineResult result = new JasmineResult();
		result.setDescription("");
		Suite suite1 = new Suite();
		suite1.setDescription("Suite #1");
		suite1.setPassed(true);
		Suite suite2 = new Suite();
		suite2.setDescription("Suite #2");
		suite2.setPassed(true);
		Spec spec = new Spec();
		spec.setDescription("Some spec");
		spec.setPassed(true);
		suite2.addChild(spec);
		suite1.addChild(suite2);
		result.addChild(suite1);
		
		resultLogger.log(result);
		
		verify(log).info("Suite "+suite1.getDescription());
		verify(log).info("  Suite "+suite2.getDescription());
		verify(log).info("    Spec "+spec.getDescription());
	}
	
	@Test
	public void shouldLogMessagesOfFailedSpecs() {
		JasmineResult result = new JasmineResult();
		result.setDescription("");
		Spec spec = new Spec();
		spec.setDescription("Parent Child Spec");
		spec.setPassed(false);
		spec.addMessage("42 should not equal the answer to everything");
		spec.addMessage("Yet another message");

		result.addChild(spec);

		resultLogger.log(result);
		
		verify(log).info("Spec "+spec.getDescription()+JasmineResultLogger.FAIL_APPENDAGE);
		verify(log).info("  * "+spec.getMessages().get(0));
		verify(log).info("  * "+spec.getMessages().get(1));		
	}
	
}
