package com.healthmarketscience.core;

import java.util.HashMap;
import java.util.Map;

public class IdGeneratorFactory {
    private final static Map<IdType, IdGenerator> map = new HashMap<IdType, IdGenerator>();

    static {
        map.put(IdType.UUID, new UUIDGenerator());
        map.put(IdType.AUTO, new AutoGenerator());
        map.put(IdType.NONE, new NoneGenerator());
    }

    public static IdGenerator getIdGenerator(IdType idType) {
        return map.get(idType);
    }
}
