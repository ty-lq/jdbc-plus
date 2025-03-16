package com.healthmarketscience.wrapper;

import com.healthmarketscience.core.LambdaUtils;
import com.healthmarketscience.sqlbuilder.BinaryCondition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Condition {
    private final String table;
    private final List<QueryCondition> queryConditions = new ArrayList<>();

    public String getTable() {
        return table;
    }

    public Condition(String table) {
        this.table = table;
    }

    public void eq(String column, Object value) {
        queryConditions.add(new QueryCondition(column, BinaryCondition.Op.EQUAL_TO, value));
    }

    public void gt(String column, Object value) {
        queryConditions.add(new QueryCondition(column, BinaryCondition.Op.GREATER_THAN, value));
    }

    public void notNull(String column) {
        queryConditions.add(new QueryCondition(column, BinaryCondition.Op.NOT_NULL, null));
    }

    public void eq(boolean condition, String column, Object value) {
        if (condition) {
            this.eq(column, value);
        }
    }

    public <T> void eq(SFunction<T, ?> column, Object value) {
        this.eq(LambdaUtils.extractColumnName(column), value);
    }

    public <T> void eq(boolean condition, SFunction<T, ?> column, Object value) {
        if (condition) {
            this.eq(column, value);
        }
    }

    public void in(String column, Object value) {
        if (value instanceof Collection) {
            queryConditions.add(new QueryCondition(column, BinaryCondition.Op.IN, value));
        }
        else if (value.getClass().isPrimitive() || value instanceof Number || value instanceof String || value instanceof Character || value instanceof Boolean) {
            List<Object> collection = new ArrayList<>();
            collection.add(value);
            queryConditions.add(new QueryCondition(column, BinaryCondition.Op.IN, collection));
        }
        else {
            throw new IllegalArgumentException("Value must be a Collection or a primitive/wrapper type.");
        }
    }

    public <T> void in(SFunction<T, ?> column, Object value) {
        this.in(LambdaUtils.extractColumnName(column), value);
    }

    public void like(String column, Object value) {
        queryConditions.add(new QueryCondition(column, BinaryCondition.Op.LIKE, "%" + value + "%"));
    }

    public List<QueryCondition> getWheres() {
        return queryConditions;
    }


}
