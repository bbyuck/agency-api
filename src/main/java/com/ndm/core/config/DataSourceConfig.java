package com.ndm.core.config;

import com.ndm.core.develop.SSHTunnelingInitializer;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.sql.SQLException;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Profile({"prd", "dev"})
public class DataSourceConfig {


    @Value("${spring.datasource.hikari.jdbc-url}")
    private String jdbcUrl;
    @Value("${spring.datasource.hikari.driver-class-name}")
    private String driverClassName;
    @Value("${spring.datasource.hikari.username}")
    private String username;
    @Value("${spring.datasource.hikari.password}")
    private String password;

    @Bean
    public DataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(driverClassName);
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);

        log.info(hikariConfig.getJdbcUrl());

        return new HikariDataSource(hikariConfig);
    }

}
