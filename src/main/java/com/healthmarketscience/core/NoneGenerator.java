package com.healthmarketscience.core;

public class NoneGenerator implements IdGenerator{
    @Override
    public Object generateId() {
        return null;
    }
}
