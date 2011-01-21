package com.github.searls.jasmine.model;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class JasmineResultTest {

	private JasmineResult jasmineResult = new JasmineResult();;

	@Test
	public void shouldParseDescriptionWhenSuccessful() {
		jasmineResult.setDescription("1 spec, 0 failures");

		boolean success = jasmineResult.didPass();

		assertThat(success, is(true));
	}

	@Test
	public void shouldFailWhenFail() {
		jasmineResult.setDescription("2 specs, 1 failure");

		boolean success = jasmineResult.didPass();

		assertThat(success, is(false));
	}

}
