package com.virnect.data.global.util;

import java.util.Collection;
import java.util.List;

public class ListUtils {

	public static <E> void addAllIfNotNull(List<E> list, Collection<? extends E> c) {
		if (c != null && c.size() > 0) {
			list.addAll(c);
		}
	}

}
