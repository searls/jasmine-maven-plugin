package com.github.searls.jasmine.coffee;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.lang.reflect.Field;

import org.junit.Test;

public class CoffeeBeansTest {
	private static final String COFFEE = "coffee";
	private static final boolean BARE_OPTION = false;

	@Test
	public void constructionProperty() throws Exception {
		CoffeeBeans cup = new CoffeeBeans(COFFEE, true);
		
		assertThat((String)getFieldValue(cup, "coffee"),is(COFFEE));
		assertThat((Boolean)getFieldValue(cup, "coffeeBareOption"),is(true));
	}
	
	@Test
	public void getCacheKeyResult() throws Exception {
		String expected = "Eval#coffee,Options#null";
		CoffeeBeans cup = new CoffeeBeans(COFFEE, BARE_OPTION);
		
		assertThat(cup.getCacheKey(),is(expected));
	}

	@Test
	public void whenCoffeeNullGetCacheKeyResult() throws Exception {
		String expected = "Eval#null,Options#null";
		CoffeeBeans cup = new CoffeeBeans(null, BARE_OPTION);
		
		assertThat(cup.getCacheKey(),is(expected));
	}
	
	@Test
	public void escapeResult() throws Exception {
		CoffeeBeans cup = new CoffeeBeans("あいうえお", true);
		
		assertThat(cup.escape(),is("\\u3042\\u3044\\u3046\\u3048\\u304A"));
	}

	@Test
	public void getOptionsReturnCoffeeBareOptionIsTrue() throws Exception {
		String expected = "{bare: true}";
		CoffeeBeans cup = new CoffeeBeans(COFFEE, true);

		assertThat(cup.getOptions(),is(expected));
	}

	@Test
	public void getOptionsReturnCoffeeBareOptionIsFalse() throws Exception {
		String expected = null;
		CoffeeBeans cup = new CoffeeBeans(COFFEE, false);

		assertThat(cup.getOptions(),is(expected));
	}

	private static Object getFieldValue(CoffeeBeans cup, String fieldName) throws Exception {
		Field field = cup.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		
		return field.get(cup);
	}
}
