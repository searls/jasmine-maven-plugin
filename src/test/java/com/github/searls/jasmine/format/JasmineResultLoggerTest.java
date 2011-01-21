package com.github.searls.jasmine.format;

import static org.mockito.Mockito.*;

import org.apache.maven.plugin.logging.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.searls.jasmine.model.JasmineResult;


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
		
		verify(log).info("\nResults:\n\n"+description+"\n");
	}
	
}
