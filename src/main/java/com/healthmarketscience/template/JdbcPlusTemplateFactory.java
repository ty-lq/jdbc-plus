package com.healthmarketscience.template;

import com.healthmarketscience.core.JdbcPlusProperties;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcPlusTemplateFactory {
    private static JdbcPlusTemplate jdbcPlusTemplate;
    private static JdbcPlusProperties jdbcPlusProperties;

    public static JdbcPlusTemplate getJdbcPlusTemplate() {
        return jdbcPlusTemplate;
    }

    public static JdbcPlusTemplate create(JdbcTemplate jdbcTemplate) {
        jdbcPlusTemplate = new JdbcPlusTemplate(jdbcTemplate);
        return jdbcPlusTemplate;
    }

    public static JdbcPlusProperties getJdbcPlusProperties() {
        return jdbcPlusProperties;
    }

    public static void setJdbcPlusProperties(JdbcPlusProperties properties) {
        jdbcPlusProperties = properties;
    }
}
