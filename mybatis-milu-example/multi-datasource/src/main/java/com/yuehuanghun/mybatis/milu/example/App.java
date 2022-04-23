package com.yuehuanghun.mybatis.milu.example;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@ComponentScan(basePackages = { "com.yuehuanghun.mybatis.milu.example" })
@MapperScans({ @MapperScan(basePackages = {
		"com.yuehuanghun.mybatis.milu.example.example1.mapper" }, sqlSessionTemplateRef = "example1SqlSessionTemplate"),
		@MapperScan(basePackages = {
				"com.yuehuanghun.mybatis.milu.example.example2.mapper" }, sqlSessionTemplateRef = "example2SqlSessionTemplate") })
@EnableCaching
public class App {
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
