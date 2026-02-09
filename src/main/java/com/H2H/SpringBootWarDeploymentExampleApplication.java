package com.H2H;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.H2H.SpringBootWarDeploymentExampleApplication;

@SpringBootApplication
@EnableScheduling
public class SpringBootWarDeploymentExampleApplication extends SpringBootServletInitializer {
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SpringBootWarDeploymentExampleApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWarDeploymentExampleApplication.class, args);
	}
}
