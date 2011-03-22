package com.github.searls.jasmine.model;


public class JasmineResult {
	private String details;
	
	public String getDescription() {
		return last(getDetails().split("\n"));
	}

	public boolean didPass() {
		return getDescription().contains(" 0 failures");
	}
	
	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}
	
	private <T> T last(T[] array) {
	    return array[array.length - 1];
	}
	
}
