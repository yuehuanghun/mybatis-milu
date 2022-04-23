package com.yuehuanghun.mybatis.milu.example;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfiguration {

	@Bean
	@ConfigurationProperties(prefix = "datasources.example1.ds")
	public DataSource example1Source() {
		return new HikariDataSource();
	}

	@Bean
	@ConfigurationProperties(prefix = "datasources.example2.ds")
	public DataSource example2Source() {
		return new HikariDataSource();
	}
}
