package com.healthmarketscience.template;

import com.healthmarketscience.core.CollectionUtils;
import com.healthmarketscience.core.EntityParse;
import com.healthmarketscience.wrapper.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SimpleTemplate<T> implements CrudTemplate<T> {
    private final JdbcPlusTemplate jdbcPlusTemplate;
    private final Class<T> entityClass;
    private final List<String> id = new ArrayList<>();
    private String idAlias;
    private final List<Column> columns = new ArrayList<>();

    public SimpleTemplate(JdbcPlusTemplate jdbcPlusTemplate, Class<T> clazz) {
        this.jdbcPlusTemplate = jdbcPlusTemplate;
        this.entityClass = clazz;
        EntityParse.resolveField(id, idAlias, columns, entityClass);
    }

    @Override
    public int insert(Object entity) {
        Table table = new Table(this.entityClass);

        List<Update> updates = new ArrayList<>();
        Field id = EntityParse.resolveInsert(entityClass, entity, updates);

        CollectionUtils.addAll(table.getUpdates(), updates);
        int result = new UpdateWrapper()
                .update(table, UpdateEnums.INSERT)
                .execute();

        if (result == 0) {
            return 0;
        }

        if (id == null) {
            return 1;
        }

        Number key = table.getKeyHolder().getKey();
        if (key != null) {
            EntityParse.setIdValue(entity, id, key);
        }

        return 1;
    }

    @Override
    public int deleteById(Object id) {
        Table table = new Table(this.entityClass);
        Condition condition = table.newCondition();
        condition.eq(this.id.get(0), id);
        CollectionUtils.addAll(table.getColumns(), List.of(new Column(this.id.get(0))));
        return new UpdateWrapper()
                .update(table, UpdateEnums.DELETE)
                .where(condition)
                .execute();
    }

    @Override
    public int delete() {
        Table table = new Table(this.entityClass);
        return new UpdateWrapper()
                .update(table, UpdateEnums.DELETE)
                .execute();
    }

    @Override
    public int updateById(Object entity) {
        List<Update> updates = new ArrayList<>();
        Object id = EntityParse.resolveUpdate(entityClass, entity, updates);

        Table table = new Table(this.entityClass);
        CollectionUtils.addAll(table.getUpdates(), updates);
        Condition condition = table.newCondition();
        condition.eq(this.id.get(0), id);

        return new UpdateWrapper().update(table)
                .where(condition)
                .execute();
    }

    @Override
    public int update() {
        return 0;
    }

    @Override
    public Optional<T> selectById(Object id) {
        if (this.id.isEmpty()) {
            throw new IllegalArgumentException("Table " + entityClass.getName() + " has no @Id annotation");
        }
        return this.selectList(QueryCondition.eq(this.id.get(0), id))
                .orElse(new ArrayList<>())
                .stream().findFirst();
    }

    @Override
    public Optional<List<T>> selectList(QueryCondition... conditions) {
        Table t = new Table(entityClass, columns);

        return Optional.of(jdbcPlusTemplate.list(new SelectWrapper()
                .query(t)
                .where(conditions)
                .fetch(), entityClass));
    }
}
