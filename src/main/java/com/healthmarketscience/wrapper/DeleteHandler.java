package com.healthmarketscience.wrapper;

import com.healthmarketscience.sqlbuilder.DeleteQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;
import com.healthmarketscience.template.JdbcPlusTemplate;
import com.healthmarketscience.template.JdbcPlusTemplateFactory;

import java.util.List;

public class DeleteHandler implements UpdateWrapperHandler {
    private DeleteQuery delete;
    private final JdbcPlusTemplate jdbcPlusTemplate;

    public DeleteHandler() {
        jdbcPlusTemplate = JdbcPlusTemplateFactory.getJdbcPlusTemplate();
    }

    @Override
    public int handle(Table table, List<Condition> conditions) {
        DbSpec spec = new DbSpec();
        DbSchema schema = spec.addDefaultSchema();
        DbTable t1 = schema.addTable(table.getName());
        for (Column column : table.getColumns()) {
            t1.addColumn(column.getName());
        }
        delete = new DeleteQuery(t1);
        Wrapper.wrapperCondition(schema, delete, conditions);
        return jdbcPlusTemplate.update(this);
    }

    @Override
    public String builder() {
        return delete.validate().toString();
    }
}
