package com.budget.control.backend.config;

import com.zaxxer.hikari.HikariConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfiguration {

    //Database values configuration
    @Value("${spring.datasource.url}")
    String url;
    @Value("${spring.datasource.username}")
    String username;
    @Value("${spring.datasource.password}")
    String password;
    @Value("${spring.datasource.driver-class-name}")
    String driver;

    @Bean
    public DataSource hikariDataSource(){
        // HikariConfig is a class that allows you to configure the connection pool settings
        HikariConfig config = new HikariConfig();
        config.setUsername(username);
        config.setPassword(password);
        config.setJdbcUrl(url);
        config.setDriverClassName(driver);

        config.setMaximumPoolSize(10); // Maximum number of connections in the pool
        config.setMinimumIdle(1); // Minimum number of connections in the pool
        config.setPoolName("budget-control-db-pool"); // Name of the pool
        config.setMaxLifetime(1800000); // Maximum lifetime of a connection in the pool 1.8m ms = 30m
        config.setConnectionTimeout(30000); // Maximum time to wait for a connection from the pool 30s
        config.setConnectionTestQuery("SELECT 1"); // Query to test the connection

        return new com.zaxxer.hikari.HikariDataSource(config);
    }
}
