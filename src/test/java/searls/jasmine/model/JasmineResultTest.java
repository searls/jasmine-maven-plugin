package searls.jasmine.model;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class JasmineResultTest {

	private JasmineResult jasmineResult = new JasmineResult();;

	@Test
	public void shouldParseDescriptionWhenSuccessful() {
		jasmineResult.setDescription("1 spec, 0 failures in 0.024s");
		
		boolean success = jasmineResult.didPass();
		
		assertThat(success,is(true));
	}
	
	@Test
	public void shouldFailWhenFail() {		
		jasmineResult.setDescription("2 specs, 1 failure in 0.072s");
		
		boolean success = jasmineResult.didPass();
		
		assertThat(success,is(false));
	}

}


