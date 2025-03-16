package com.healthmarketscience.wrapper;

import com.healthmarketscience.sqlbuilder.UpdateQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;
import com.healthmarketscience.template.JdbcPlusTemplate;
import com.healthmarketscience.template.JdbcPlusTemplateFactory;

import java.util.List;

public class UpdateHandler implements UpdateWrapperHandler {
    private UpdateQuery update;
    private final JdbcPlusTemplate jdbcPlusTemplate;

    public UpdateHandler() {
        jdbcPlusTemplate = JdbcPlusTemplateFactory.getJdbcPlusTemplate();
    }

    @Override
    public int handle(Table table, List<Condition> conditions) {
        DbSpec spec = new DbSpec();
        DbSchema schema = spec.addDefaultSchema();
        DbTable t1 = schema.addTable(table.getName());
        update = new UpdateQuery(t1);

        for (Update item : table.getUpdates()) {
            DbColumn dbColumn = t1.addColumn(item.column().getName());
            update.addSetClause(dbColumn, item.value());
        }
        Wrapper.wrapperCondition(schema, update, conditions);

        return jdbcPlusTemplate.update(this);
    }

    @Override
    public String builder() {
        return update.validate().toString();
    }
}
