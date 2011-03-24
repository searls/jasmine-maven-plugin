package com.github.searls.jasmine.collections;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.plexus.util.AbstractScanner;

public class ScriptSorter {

	private static final int LEFT = -1;
	private static final int RIGHT = 1;

	public void sort(List<String> unsorted, final List<String> includes) {
		if (CollectionUtils.isNotEmpty(includes)) {
			Collections.sort(unsorted, new Comparator<String>() {
				public int compare(String left, String right) {
					for (String include : includes) {
						if (matches(left, include)) {
							return LEFT;
						} else if (matches(right, include)) {
							return RIGHT;
						}
					}
					return left.compareTo(right);
				}
			});
		} 
	}

	private boolean matches(String query, String pattern) {
		return pattern.equals(query) || AbstractScanner.match(pattern, query);
	}
}
