package com.ghostchu.tracker.sapling;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan(basePackages = "com.ghostchu.tracker.sapling.mapper")
@EnableRetry
@EnableAsync
@EnableCaching
@EnableTransactionManagement
@EnableScheduling
public class SaplingApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaplingApplication.class, args);
	}

}
