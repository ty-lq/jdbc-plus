package com.healthmarketscience.wrapper;

import com.healthmarketscience.core.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class UpdateWrapper {
    private Table from;
    private final List<Condition> conditions = new ArrayList<>();
    private UpdateEnums updateEnums;

    public UpdateWrapper update(Table table) {
        return update(table, UpdateEnums.UPDATE);
    }

    public UpdateWrapper update(Table table, UpdateEnums updateEnums) {
        this.from = table;
        this.updateEnums = updateEnums;
        return this;
    }

    public UpdateWrapper where(Condition... condition) {
        CollectionUtils.addAll(conditions, condition);
        return this;
    }

    public int execute() {
        return UpdateWrapperFactory.create(updateEnums).handle(from, conditions);
    }
}
