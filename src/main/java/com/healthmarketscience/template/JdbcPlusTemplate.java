package com.healthmarketscience.template;

import com.healthmarketscience.wrapper.SelectWrapper;
import com.healthmarketscience.wrapper.UpdateWrapperHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Statement;
import java.util.List;

public record JdbcPlusTemplate(JdbcTemplate jdbcTemplate) {
    private static final Logger log = LoggerFactory.getLogger(JdbcPlusTemplate.class);

    public <T> List<T> list(SelectWrapper selectWrapper, Class<T> clazz) {
        return list(selectWrapper, new BeanPropertyRowMapper<>(clazz));
    }

    public <T> List<T> list(SelectWrapper selectWrapper, Class<T> clazz, int maxSize) {
        return list(selectWrapper, new BeanPropertyRowMapper<>(clazz));
    }

    public List<?> list(SelectWrapper selectWrapper) {
        return list(selectWrapper, new ColumnMapRowMapper());
    }

    private <T> List<T> list(SelectWrapper selectWrapper, RowMapper<T> rowMapper) {
        String sql = selectWrapper.getQuery().validate().toString();
        log.debug(sql);
        return jdbcTemplate.query(sql, rowMapper);
    }

    public int update(UpdateWrapperHandler updateWrapperHandler) {
        String sql = updateWrapperHandler.builder();
        log.debug(sql);
        return jdbcTemplate.update(sql);
    }

    public int update(KeyHolder keyHolder, UpdateWrapperHandler updateWrapperHandler) {
        String sql = updateWrapperHandler.builder();
        log.debug(sql);
        return jdbcTemplate.update(con -> con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS), keyHolder);
    }

    public <T> CrudTemplate<T> create(Class<T> clazz) {
        return new SimpleTemplate<>(this, clazz);
    }
}
