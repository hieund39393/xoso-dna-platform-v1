package com.xoso.infrastructure.core.config;

import com.zaxxer.hikari.HikariConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {

    @Autowired
    private Environment env;

    @Bean
    public DataSource hikariInvestDataSource() {
        HikariConfig config = new HikariConfig();
        String URL = env.getProperty("spring.datasource.url");
        if(URL == null) {
            URL = "jdbc:postgresql://localhost:5432/xoso-dna";
        }
        String driver = env.getProperty("spring.datasource.driver-class-name");
        if (driver == null) {
            driver = "org.postgresql.Driver";
        }
        String username = env.getProperty("spring.datasource.username");
        if (username == null) {
            username = "postgres";
        }
        String password = env.getProperty("spring.datasource.password");
        if(password == null) {
            password = "postgres";
        }
        var maxPoolSize = env.getProperty("spring.datasource.maxPoolSize");
        if(StringUtils.isNotBlank(maxPoolSize)) {
            config.setMaximumPoolSize(Integer.parseInt(maxPoolSize));
        }
        var minimumIdle = env.getProperty("spring.datasource.minimumIdle");
        if(StringUtils.isNotBlank(minimumIdle)) {
            config.setMinimumIdle(Integer.parseInt(minimumIdle));
        }
        var idleTimeout = env.getProperty("spring.datasource.idleTimeout");
        if(StringUtils.isNotBlank(idleTimeout)) {
            config.setIdleTimeout(Integer.parseInt(idleTimeout));
        }
        var connectionTimeout = env.getProperty("spring.datasource.connectionTimeout");
        if(StringUtils.isNotBlank(connectionTimeout)) {
            config.setConnectionTimeout(Integer.parseInt(connectionTimeout));
        }
        config.setJdbcUrl(URL);
        config.setDriverClassName(driver);
        config.setUsername(username);
        config.setPassword(password);

        DataSource ds = new com.zaxxer.hikari.HikariDataSource(config);
        return ds;
    }
}
