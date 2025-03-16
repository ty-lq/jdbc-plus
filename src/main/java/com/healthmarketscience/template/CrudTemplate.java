package com.healthmarketscience.template;

import com.healthmarketscience.wrapper.QueryCondition;

import java.util.List;
import java.util.Optional;

public interface CrudTemplate<T> {
    int insert(T entity);

    int deleteById(Object id);

    int delete();

    int updateById(T entity);

    int update();

    Optional<T> selectById(Object id);

    Optional<List<T>> selectList(QueryCondition... conditions);

}
