package com.healthmarketscience.template;

import com.healthmarketscience.core.JdbcPlusProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@ConditionalOnClass({JdbcPlusTemplate.class})
@EnableConfigurationProperties(JdbcPlusProperties.class)
public class JdbcPlusTemplateAutoConfiguration {
    @Bean
    public JdbcPlusTemplate jdbcPlusTemplate(JdbcTemplate jdbcTemplate, JdbcPlusProperties jdbcPlusProperties) {
        JdbcPlusTemplateFactory.setJdbcPlusProperties(jdbcPlusProperties);
        return JdbcPlusTemplateFactory.create(jdbcTemplate);
    }
}
