package com.NewYearBlessings;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration;

@SpringBootApplication(exclude = SqlInitializationAutoConfiguration.class)
@MapperScan("com.NewYearBlessings.mapper")
public class NewYearBlessings {
    public static void main(String[] args) {
        SpringApplication.run(NewYearBlessings.class, args);
    }
}
