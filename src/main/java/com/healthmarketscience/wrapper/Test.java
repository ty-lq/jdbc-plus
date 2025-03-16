package com.healthmarketscience.wrapper;

import com.healthmarketscience.sqlbuilder.UpdateQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;

public class Test {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            Table t1 = new Table(Users.class,
                    new Column(Users::getId, SqlFunction.COUNT),
                    new Column(Users::getName));
            Condition condition = t1.newCondition();
            condition.eq(true, Users::getId, 123);
            Table t2 = new Table("addresses",
                    new Column("id"),
                    new Column("user_id", t1.createForeignKey(Users::getId)),
                    new Column("email"));
            Condition condition1 = t2.newCondition();
            condition1.like("email", "jack@example.com");
            Table t3 = new Table("wallet",
                    new Column("id"),
                    new Column("user_id", new ForeignKey("users.id")),
                    new Column("balance"));

            System.out.println(new SelectWrapper().query(t1)
                    .join(t3)
                    .where(condition)
                    .fetch().getQuery().toString());
        }
        long end = System.currentTimeMillis();
        System.out.println((end - start) / 1000.0 + " seconds");

        DbSpec spec = new DbSpec();
        DbSchema schema = spec.addDefaultSchema();
        DbTable t1 = schema.addTable("t_person");

        DbColumn id = t1.addColumn("id");
        UpdateQuery update = new UpdateQuery(t1);
        update.addSetClause(id, 1);

        System.out.println(update.validate().toString());

    }
}
