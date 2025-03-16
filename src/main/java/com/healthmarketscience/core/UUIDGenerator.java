package com.healthmarketscience.core;

import java.util.UUID;

public class UUIDGenerator implements IdGenerator {
    @Override
    public Object generateId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
