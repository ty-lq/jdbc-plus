package com.healthmarketscience.wrapper;

import com.healthmarketscience.core.CollectionUtils;
import com.healthmarketscience.core.TextUtils;
import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.OrderObject;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.*;

import java.util.ArrayList;
import java.util.List;

public class SelectWrapper {
    private Table from;
    private final List<Table> joins = new ArrayList<>();
    private final List<Condition> conditions = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();
    private SelectQuery query;

    public SelectWrapper query(Table table) {
        this.from = table;
        return this;
    }

    public SelectWrapper join(Table table, SelectQuery.JoinType joinType) {
        table.setJoinType(joinType);
        this.joins.add(table);
        return this;
    }

    public SelectWrapper join(Table table) {
        return this.join(table, SelectQuery.JoinType.INNER);
    }

    public SelectWrapper inner(Table table) {
        return this.join(table, SelectQuery.JoinType.INNER);
    }

    public SelectWrapper left(Table table) {
        return this.join(table, SelectQuery.JoinType.LEFT_OUTER);
    }

    public SelectWrapper right(Table table) {
        return this.join(table, SelectQuery.JoinType.RIGHT_OUTER);
    }

    public SelectWrapper where(Condition... condition) {
        CollectionUtils.addAll(conditions, condition);
        return this;
    }

    public SelectWrapper where(QueryCondition... condition) {
        List<Condition> newConditions = new ArrayList<>();
        for (QueryCondition queryCondition : condition) {
            Condition con = new Condition(this.from.getName());
            newConditions.add(con);
            if (queryCondition.getOperator().equals(BinaryCondition.Op.EQUAL_TO)) {
                con.eq(queryCondition.getColumn(), queryCondition.getValue());
            }
        }
        CollectionUtils.addAll(conditions, newConditions);
        return this;
    }

    public SelectWrapper order(Order order) {
        this.orders.add(order);
        return this;
    }

    public SelectWrapper fetch() {
        DbSpec spec = new DbSpec();
        DbSchema schema = spec.addDefaultSchema();
        DbTable t1 = schema.addTable(from.getName());
        query = new SelectQuery();
        for (Column column : from.getColumns()) {
            DbColumn dbColumn = t1.addColumn(column.getName());
            SqlFunction function = column.getFunction();
            if (function != null) {
                query.addCustomColumns(column.wrapper(dbColumn));
            } else {
                query.addColumns(dbColumn);
            }
        }
        if (!this.joins.isEmpty()) {
            for (Table join : joins) {
                DbTable itemJoin = schema.addTable(join.getName());
                if (join.getColumns().isEmpty()) {
                    continue;
                }
                Column foreignKey = null;
                for (Column column : join.getColumns()) {
                    itemJoin.addColumn(column.getName());
                    SqlFunction function = column.getFunction();
                    DbColumn dbColumn = itemJoin.addColumn(column.getName());
                    if (function != null) {
                        query.addCustomColumns(column.wrapper(dbColumn));
                    } else {
                        if (TextUtils.isNotEmpty(column.getAlias())) {
                            query.addAliasedColumn(dbColumn, column.getAlias());
                        } else {
                            query.addColumns(dbColumn);
                        }
                        if (column.getForeign_key() != null) {
                            foreignKey = column;
                        }
                    }
                }
                if (foreignKey == null) {
                    throw new IllegalArgumentException("Join " + join.getName() + " has no foreign key");
                }
                String[] ons = foreignKey.getForeign_key().condition().split("\\.");
                DbTable onTable = schema.findTable(ons[0]);
                DbJoin dbJoin = spec.addJoin(null, onTable.getName(), null, join.getName(),
                        new String[]{ons[1]}, new String[]{foreignKey.getName()});
                query.addJoins(join.getJoinType(), dbJoin);
            }
        }
        Wrapper.wrapperCondition(schema, query, conditions);
        if (!this.orders.isEmpty()) {
            for (Order order : orders) {
                DbTable orderTable = schema.findTable(order.table());
                DbColumn orderColumn = orderTable.findColumn(order.column());
                query.addOrdering(orderColumn, order.asc() ? OrderObject.Dir.ASCENDING : OrderObject.Dir.DESCENDING);
            }
        }

        return this;
    }

    public SelectQuery getQuery() {
        return query;
    }
}
