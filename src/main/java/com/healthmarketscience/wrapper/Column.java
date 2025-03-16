package com.healthmarketscience.wrapper;

import com.healthmarketscience.core.LambdaUtils;
import com.healthmarketscience.core.TextUtils;
import com.healthmarketscience.sqlbuilder.FunctionCall;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;

public class Column {
    private final String name;
    private String alias;
    private final boolean primary_key;
    private ForeignKey foreign_key;
    private SqlFunction function;

    public Column(String name) {
        this.name = name;
        this.primary_key = false;
    }

    public Column(String name, String alias) {
        this.name = name;
        this.alias = alias;
        this.primary_key = false;
    }

    public Column(String name, SqlFunction function) {
        this(name, false);
        this.function = function;
    }

    public Column(String name, boolean primary_key) {
        this.name = name;
        this.primary_key = primary_key;
    }

    public Column(String name, ForeignKey foreignKey) {
        this(name, false);
        this.foreign_key = foreignKey;
    }

    public <T> Column(SFunction<T, ?> name) {
        this.name = LambdaUtils.extractColumnName(name);
        this.primary_key = false;
    }

    public <T> Column(SFunction<T, ?> name, SqlFunction function) {
        this(name);
        this.function = function;
    }

    public String getName() {
        return name;
    }

    public String getAlias() {
        return alias;
    }

    public boolean isPrimary_key() {
        return primary_key;
    }

    public ForeignKey getForeign_key() {
        return foreign_key;
    }

    public SqlFunction getFunction() {
        return function;
    }

    public FunctionCall wrapper(DbColumn dbColumn) {
        if (this.function == SqlFunction.COUNT) {
            return FunctionCall.count().addColumnParams(dbColumn);
        }

        throw new IllegalArgumentException("Unsupported function: " + this.function);
    }
}
