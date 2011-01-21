package com.github.searls.jasmine.model;


public class JasmineResult {
	private String description;
	private String details;
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean didPass() {
		if(description == null) {
			throw new IllegalStateException("Can only determine success after description is set.");
		}
		return description.contains("0 failures");
	}
	
	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}
	
}
