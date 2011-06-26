package com.github.searls.jasmine.format;

import java.util.Set;

public class FormatsScriptTags {

	public String format(Set<String> sourceLocations) {
		StringBuilder sb = new StringBuilder();
		for (String location : sourceLocations) {
			sb.append("<script type=\""+type(location)+"\" src=\"").append(location).append("\"></script>").append("\n");
		}
		return sb.toString();
	}

	private String type(String name) {
		return name.endsWith(".coffee") ? "text/coffeescript" : "text/javascript";
	}
	
}
