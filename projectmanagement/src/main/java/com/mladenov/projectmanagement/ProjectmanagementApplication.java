package com.mladenov.projectmanagement;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@OpenAPIDefinition(
		info = @Info(
				title = "Project Management System API",
				version = "1.0",
				description = "API documentation for Project Management System",
				contact = @Contact(name = "Angel Mladenov",
						email = "angelmladenov3@gmail.com",
						url = "https://github.com/velk20")
		),
		security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
		name = "bearerAuth",
		type = SecuritySchemeType.HTTP,
		scheme = "bearer",
		bearerFormat = "JWT"
)
@SpringBootApplication
@EnableScheduling
public class ProjectmanagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectmanagementApplication.class, args);
	}

}
