package com.healthmarketscience.sqlbuilder;

import com.healthmarketscience.common.util.AppendableExt;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class CollectionValueObject extends Expression {
    private Collection<?> _value;

    public CollectionValueObject(Object value) {
        this((Collection<?>) value);
    }

    public CollectionValueObject(Collection<?> value) {
        _value = value;
    }

    @Override
    public boolean hasParens() {
        return false;
    }

    @Override
    protected void collectSchemaObjects(ValidationContext vContext) {
    }

    @Override
    public void appendTo(AppendableExt app) throws IOException {
        String content;
        if (_value.isEmpty()) {
            content = "[]";
        } else {
            content = _value.stream()
                    .map(i -> (i instanceof String) ? "'" + i + "'" : Objects.toString(i))
                    .collect(Collectors.joining(","));
        }
        app.append("(").append(content).append(")");
    }

}
