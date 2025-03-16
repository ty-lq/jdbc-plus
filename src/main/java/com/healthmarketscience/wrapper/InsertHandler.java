package com.healthmarketscience.wrapper;

import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;
import com.healthmarketscience.template.JdbcPlusTemplate;
import com.healthmarketscience.template.JdbcPlusTemplateFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import java.util.List;

public class InsertHandler implements UpdateWrapperHandler{
    private InsertQuery insert;
    private final JdbcPlusTemplate jdbcPlusTemplate;

    public InsertHandler() {
        jdbcPlusTemplate = JdbcPlusTemplateFactory.getJdbcPlusTemplate();
    }

    @Override
    public int handle(Table table, List<Condition> conditions) {
        DbSpec spec = new DbSpec();
        DbSchema schema = spec.addDefaultSchema();
        DbTable t1 = schema.addTable(table.getName());
        insert = new InsertQuery(t1);

        for (Update item : table.getUpdates()) {
            DbColumn dbColumn = t1.addColumn(item.column().getName());
            insert.addColumn(dbColumn, item.value());
        }

        table.setKeyHolder(new GeneratedKeyHolder());

        return jdbcPlusTemplate.update(table.getKeyHolder(), this);
    }

    @Override
    public String builder() {
        return insert.validate().toString();
    }
}
