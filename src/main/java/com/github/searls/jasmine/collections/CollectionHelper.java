package com.github.searls.jasmine.collections;

import java.util.Collections;
import java.util.List;

public abstract class CollectionHelper {

	@SuppressWarnings("unchecked")
	public static <T> List<T> list(List<T> list) {
		return list == null ? Collections.EMPTY_LIST : list;
	}

}
