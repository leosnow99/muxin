package com.muxin;

import com.muxin.utils.IdWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MuxinApplication {
	public static void main(String[] args) {
		SpringApplication.run(MuxinApplication.class, args);
	}
	
	@Bean
	public IdWorker idWorker() {
		return new IdWorker(1, 1);
	}
}
