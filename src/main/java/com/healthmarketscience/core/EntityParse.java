package com.healthmarketscience.core;

import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;
import com.healthmarketscience.template.JdbcPlusTemplateFactory;
import com.healthmarketscience.wrapper.Update;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityParse {
    private static final Map<Class<?>, List<Field>> FIELD_CACHE = new ConcurrentHashMap<>();

    public static String getTable(Class<?> clazz) {
        Table table = clazz.getAnnotation(Table.class);
        if (table == null) {
            throw new IllegalArgumentException("Table " + clazz.getName() + " has no @Table annotation");
        }
        return table.value();
    }

    public static boolean ignore(Field field) {
        return Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers());
    }

    public static List<Field> getFields(Class<?> entity) {
        return FIELD_CACHE.computeIfAbsent(entity, clazz -> {
            List<Field> fieldList = new ArrayList<>();
            for (Field field : clazz.getDeclaredFields()) {
                if (ignore(field)) {
                    continue;
                }

                field.setAccessible(true);
                fieldList.add(field);
            }
            return fieldList;
        });
    }

    public static void resolveField(List<String> id, String idAlias, List<com.healthmarketscience.wrapper.Column> columns, Class<?> entity) {
        for (Field field : getFields(entity)) {
            if (field.isAnnotationPresent(Id.class)) {
                String idValue = getIdName(field);
                id.add(idValue);
                idAlias = NamingConversionUtils.camelToSnake(field.getName());
                columns.add(new com.healthmarketscience.wrapper.Column(idValue));
                continue;
            }

            String columnName = getColumn(field);
            if (TextUtils.isEmpty(columnName)) {
                continue;
            }
            columns.add(new com.healthmarketscience.wrapper.Column(columnName));
        }
    }

    public static String getIdName(Field field) {
        Id id = field.getAnnotation(Id.class);
        String result;
        if (id != null && TextUtils.isNotEmpty(id.value())) {
            result = id.value();
        } else {
            result = JdbcPlusTemplateFactory.getJdbcPlusProperties().getMapUnderscoreToCamelCase() ? NamingConversionUtils.camelToSnake(field.getName()) : field.getName();
        }

        return result;
    }

    public static String getColumn(Field field) {
        Column column = field.getAnnotation(Column.class);
        String result;
        if (column == null) {
            result = JdbcPlusTemplateFactory.getJdbcPlusProperties().getMapUnderscoreToCamelCase() ? NamingConversionUtils.camelToSnake(field.getName()) : field.getName();
        } else {
            if (!column.ignore()) {
                result = column.value();
            } else {
                return "";
            }
        }

        return result;
    }

    public static Object resolveUpdate(Class<?> clazz, Object entity, List<Update> updates) {
        Object tableIdValue = null;

        for (Field field : getFields(clazz)) {
            try {
                if (field.isAnnotationPresent(Id.class)) {
                    tableIdValue = field.get(entity);
                }

                setUpdateColumn(entity, updates, field);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Unable to access field: " + field.getName(), e);
            }
        }

        return tableIdValue;
    }

    public static Field resolveInsert(Class<?> clazz, Object entity, List<Update> updates) {
        Field tableIdField = null;
        for (Field field : getFields(clazz)) {
            try {
                if (field.isAnnotationPresent(Id.class)) {
                    Id id = field.getAnnotation(Id.class);
                    Object val = IdGeneratorFactory.getIdGenerator(id.type()).generateId();
                    if (id.type().equals(IdType.AUTO)) {
                        tableIdField = field;
                    } else {
                        field.set(entity, val);
                        updates.add(new Update(new com.healthmarketscience.wrapper.Column(getIdName(field)), val));
                    }
                    continue;
                }

                setUpdateColumn(entity, updates, field);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Unable to access field: " + field.getName(), e);
            }
        }

        return tableIdField;
    }

    private static void setUpdateColumn(Object entity, List<Update> updates, Field field) throws IllegalAccessException {
        Object value = field.get(entity);
        if (value != null) {
            String column = getColumn(field);
            if (TextUtils.isEmpty(column)) {
                return;
            }
            updates.add(new Update(new com.healthmarketscience.wrapper.Column(column), value));
        }
    }

    public static List<com.healthmarketscience.wrapper.Column> getColumns(Class<?> clazz) {
        List<com.healthmarketscience.wrapper.Column> columns = new ArrayList<>();
        for (Field field : getFields(clazz)) {
            columns.add(new com.healthmarketscience.wrapper.Column(getColumn(field)));
        }

        return columns;
    }

    public static void setIdValue(Object entity, Field field, Number key) {
        try {
            Class<?> fieldType = field.getType();
            if (fieldType == Long.class || fieldType == long.class) {
                field.set(entity, key.longValue());
            } else if (fieldType == Integer.class || fieldType == int.class) {
                field.set(entity, key.intValue());
            } else if (fieldType == String.class) {
                field.set(entity, key.toString());
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to set primary key field", e);
        }
    }
}
