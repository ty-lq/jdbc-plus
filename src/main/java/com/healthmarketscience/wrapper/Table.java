package com.healthmarketscience.wrapper;

import com.healthmarketscience.core.CollectionUtils;
import com.healthmarketscience.core.EntityParse;
import com.healthmarketscience.core.LambdaUtils;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import org.springframework.jdbc.support.KeyHolder;

import java.util.ArrayList;
import java.util.List;

public class Table {
    private final String name;
    private KeyHolder keyHolder;
    private final List<Column> columns = new ArrayList<>();
    private final List<Update> updates = new ArrayList<>();
    private SelectQuery.JoinType joinType;

    public Table(String name, Column... columns) {
        this.name = name;
        CollectionUtils.addAll(this.columns, columns);
    }

    public Table(Class<?> clazz, Column... columns) {
        this(EntityParse.getTable(clazz), columns);
        if (columns == null || columns.length == 0) {
            CollectionUtils.addAll(this.columns, EntityParse.getColumns(clazz));
        }
    }

    public Table(Class<?> clazz, List<Column> columns) {
        this.name = EntityParse.getTable(clazz);
        this.columns.addAll(columns);
    }

    public Table(String name, Update... updates) {
        this.name = name;
        CollectionUtils.addAll(this.updates, updates);
    }

    public Table(String name, List<Update> updates) {
        this.name = name;
        this.updates.addAll(updates);
    }

    public String getName() {
        return name;
    }

    public KeyHolder getKeyHolder() {
        return keyHolder;
    }

    public void setKeyHolder(KeyHolder keyHolder) {
        this.keyHolder = keyHolder;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public List<Update> getUpdates() {
        return updates;
    }

    public SelectQuery.JoinType getJoinType() {
        return joinType;
    }

    public void setJoinType(SelectQuery.JoinType joinType) {
        this.joinType = joinType;
    }

    public Column getPrimaryKey() {
        for (Column column : columns) {
            if (column.isPrimary_key()) {
                return column;
            }
        }

        return null;
    }

    public ForeignKey createForeignKey(String column) {
        return new ForeignKey(this.name + "." + column);
    }

    public <T> ForeignKey createForeignKey(SFunction<T, ?> column) {
        return this.createForeignKey(LambdaUtils.extractColumnName(column));
    }

    public Condition newCondition() {
        return new Condition(this.name);
    }

    public Order newOrder(String column) {
        return new Order(this.name, column, false);
    }

    public <T> Order newOrder(SFunction<T, ?> column) {
        return this.newOrder(LambdaUtils.extractColumnName(column));
    }

    public Order newOrder(String column, boolean asc) {
        return new Order(this.name, column, asc);
    }

    public <T> Order newOrder(SFunction<T, ?> column, boolean asc) {
        return this.newOrder(LambdaUtils.extractColumnName(column), asc);
    }

}
