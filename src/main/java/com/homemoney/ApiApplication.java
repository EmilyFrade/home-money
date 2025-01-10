package com.homemoney;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.homemoney.config.DotenvInitializer;

@SpringBootApplication
public class ApiApplication {

	public static void main(String[] args) {
		DotenvInitializer.initialize();
		SpringApplication.run(ApiApplication.class, args);
	}

}
