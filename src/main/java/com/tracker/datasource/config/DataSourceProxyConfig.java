package com.tracker.datasource.config;

import com.tracker.datasource.PerformanceDataSourceProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceProxyConfig {

    @Bean
    @Primary
    public DataSource performanceProxyDataSource(DataSource dataSource) {
        return new PerformanceDataSourceProxy(dataSource);
    }
}
