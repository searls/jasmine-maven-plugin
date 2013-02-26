package com.github.searls.jasmine.coffee;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

public class DetectsCoffeeTest {

	private final DetectsCoffee subject = new DetectsCoffee();

	@Test
	public void whenAStringEndingInCoffeeThatsCoffee() {
		assertThat("/some/path/to/pants.coffee",is(this.coffee()));
	}

	@Test
	public void whenAStringDoesNotEndInCoffeeThatsNotCoffee() {
		assertThat("/some/path/to/pants.cafe",is(not(this.coffee())));
	}

	@Test
	public void whenCoffeeHasAQueryStringThatsCoffee() {
		assertThat("/some/path/to/pants.coffee?stillCoffee=true",is(this.coffee()));
	}

	@Test
	public void whenJavaScriptHasACoffeeQueryThatsNotCoffee() {
		assertThat("/some/path/to/pants.js?extension=lulz.coffee",is(not(this.coffee())));
	}

	private TypeSafeMatcher<String> coffee() {
		return new TypeSafeMatcher<String>() {
			@Override
			public boolean matchesSafely(String path) {
				return DetectsCoffeeTest.this.subject.detect(path);
			}
			@Override
			public void describeTo(Description desc) {}
		};
	}
}
