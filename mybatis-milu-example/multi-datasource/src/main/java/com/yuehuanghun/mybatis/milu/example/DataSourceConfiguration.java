package com.yuehuanghun.mybatis.milu.example;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

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

	@Bean
	@Primary
	public PlatformTransactionManager example1SourceTransactionManager(@Qualifier("example1Source") DataSource example1Source) {
		return new DataSourceTransactionManager(example1Source);
	}

	@Bean
	public PlatformTransactionManager example2SourceTransactionManager(@Qualifier("example2Source") DataSource example2Source) {
		return new DataSourceTransactionManager(example2Source);
	}
}
