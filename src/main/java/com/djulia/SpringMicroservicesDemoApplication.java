package com.djulia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.hateoas.config.EnableEntityLinks;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EnableHypermediaSupport(type= EnableHypermediaSupport.HypermediaType.HAL)
public class SpringMicroservicesDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringMicroservicesDemoApplication.class, args);
	}
}
