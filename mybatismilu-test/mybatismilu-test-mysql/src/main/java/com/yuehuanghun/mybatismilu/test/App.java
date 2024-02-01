package com.yuehuanghun.mybatismilu.test;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.yuehuanghun.mybatis.milu.spring.EnableEntityGenericResultMap;

@SpringBootApplication
@MapperScan(basePackages = "com.yuehuanghun.mybatismilu.test.domain.mapper")
@ComponentScan(value = {"com.yuehuanghun.mybatismilu.test"})
@EnableEntityGenericResultMap
public class App 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(App.class, args);
    }
}
