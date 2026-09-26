package br.com.orbitapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class OrbitAPIApplication {

	static void main(String[] args) {
		SpringApplication.run(OrbitAPIApplication.class, args);
	}
}