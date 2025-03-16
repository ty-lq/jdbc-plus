package com.healthmarketscience.wrapper;

import com.healthmarketscience.sqlbuilder.*;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;

import java.util.List;

public class Wrapper {
    public static void wrapperCondition(DbSchema schema, Query<?> query, List<Condition> conditions) {
        if (!conditions.isEmpty()) {
            for (Condition condition : conditions) {
                String item = condition.getTable();
                DbTable itemTable = schema.findTable(item);
                if (itemTable == null) {
                    throw new IllegalArgumentException("Condition " + item + " has no table");
                }
                for (QueryCondition where : condition.getWheres()) {
                    String column = where.getColumn();
                    DbColumn itemColumn = itemTable.findColumn(column);
                    if (itemColumn == null) {
                        throw new IllegalArgumentException("Condition " + item + " has no column");
                    }
                    if (query instanceof Where) {
                        ((Where) query).addCondition(where.wrapper(itemColumn));
                    }
                }
            }
        }
    }
}
