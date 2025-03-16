package com.healthmarketscience.wrapper;

import com.healthmarketscience.core.LambdaUtils;
import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.Condition;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;

public class QueryCondition {
    private final String column;
    private final BinaryCondition.Op operator;
    private final Object value;

    public QueryCondition(String column, BinaryCondition.Op operator, Object value) {
        this.column = column;
        this.operator = operator;
        this.value = value;
    }

    public String getColumn() {
        return column;
    }

    public BinaryCondition.Op getOperator() {
        return operator;
    }

    public Object getValue() {
        return value;
    }

    public static QueryCondition eq(String column, Object value) {
        return new QueryCondition(column, BinaryCondition.Op.EQUAL_TO, value);
    }

    public static  <T> QueryCondition eq(SFunction<T, ?> column, Object value) {
        return eq(LambdaUtils.extractColumnName(column), value);
    }

    public Condition wrapper(DbColumn dbColumn) {
        return new BinaryCondition(operator, dbColumn, value);
    }
}