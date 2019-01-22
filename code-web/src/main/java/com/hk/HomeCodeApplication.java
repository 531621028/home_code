package com.hk;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.hk.dao")
public class HomeCodeApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomeCodeApplication.class, args);
	}

}

