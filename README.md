# jsql
基于jdbcTemplate的增删改查模板
# 开源项目
1. [sqlbuilder](https://github.com/jahlborn/sqlbuilder) 本项目是基于此项目二次开发而来。
2. [springboot](https://github.com/spring-projects/spring-boot) 本项目是基于此项目二次开发而来。

# 示例

    @Test
    public void test() {
        Table table1 = new Table("T_MALL_ORDERS_PRIZE",
                new Column("PARTY_ID"),
                new Column("PROFIT"),
                new Column("UUID"),
                new Column("SELLER_ID"));
        Condition condition = table1.newCondition();
        condition.eq("SELLER_ID", "e7a5a8828bd3e591018c526bb28515f1");

        Table table2 = new Table("T_MALL_SELLER",
                new Column("UUID", new ForeignKey("T_MALL_ORDERS_PRIZE.PARTY_ID")),
                new Column("NAME"));

        List list1 = jdbcPlusTemplate.list(new SelectWrapper()
                .query(table1)
                .left(table2)
                .where(condition)
                .fetch());
    }
}