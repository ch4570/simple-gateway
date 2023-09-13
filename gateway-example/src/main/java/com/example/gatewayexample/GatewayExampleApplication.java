package com.example.gatewayexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class GatewayExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayExampleApplication.class, args);
	}

}
