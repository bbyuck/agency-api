package com.ndm.core.config;

import com.ndm.core.develop.SSHTunnelingInitializer;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Slf4j
@Profile({"local"})
@Configuration
@RequiredArgsConstructor
public class LocalDataSourceConfig {

    @Value("${spring.datasource.hikari.jdbc-url}")
    private String jdbcUrl;
    @Value("${spring.datasource.hikari.driver-class-name}")
    private String driverClassName;
    @Value("${spring.datasource.hikari.username}")
    private String username;
    @Value("${spring.datasource.hikari.password}")
    private String password;

    @Bean
    public SSHTunnelingInitializer sshTunnelingInitializer() {
        return new SSHTunnelingInitializer();
    }

    @Bean
    public DataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(driverClassName);
        hikariConfig.setJdbcUrl(jdbcUrl.replace("[forwardedPort]", Integer.toString(sshTunnelingInitializer().buildSshConnection())));
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);

        log.info(hikariConfig.getJdbcUrl());

        return new HikariDataSource(hikariConfig);
    }
}
