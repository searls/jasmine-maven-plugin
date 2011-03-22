package com.github.searls.jasmine.model;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class JasmineResultTest {

	private JasmineResult jasmineResult = new JasmineResult();;

	@Test
	public void shouldParseDescriptionWhenSuccessful() {
		jasmineResult.setDetails(
				"Some results\n" +
				"More results\n" +
				"1 spec, 0 failures");

		boolean success = jasmineResult.didPass();

		assertThat(success, is(true));
	}

	@Test
	public void shouldFailWhenFail() {
		jasmineResult.setDetails(
				"Describe Kaka wants 0 failures \n" +
				"it is Swedish for cookie\n" +
				"Results: 2 specs, 1 failure");

		boolean success = jasmineResult.didPass();

		assertThat(success, is(false));
	}
	

	@Test
	public void shouldFailWhenMultipleOfTenFails() {
		jasmineResult.setDetails("Results: 2 specs, 10 failures");

		boolean success = jasmineResult.didPass();

		assertThat(success, is(false));
	}

}
