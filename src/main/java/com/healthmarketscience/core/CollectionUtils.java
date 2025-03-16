package com.healthmarketscience.core;

import java.util.Collection;

public class CollectionUtils {
    public static void addAll(Collection collection, Object[] iterable) {
        for (Object o : iterable) {
            collection.add(o);
        }
    }

    public static <T> void addAll(Collection<T> collection1, Collection<T> collection2) {
        for (T t : collection2) {
            collection1.add(t);
        }
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }
}
