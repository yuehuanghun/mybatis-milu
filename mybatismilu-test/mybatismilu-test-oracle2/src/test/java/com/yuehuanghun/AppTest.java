package com.yuehuanghun;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan(basePackages = "com.yuehuanghun.mybatismilu.test.domain.mapper")
@ComponentScan(value = {"com.yuehuanghun"})
public class AppTest 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(AppTest.class, args);
    }
}
