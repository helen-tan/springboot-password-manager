package com.personal.springboot_password_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/* For Mongo Debug */
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.context.ApplicationContext;
// import org.springframework.context.annotation.Bean;
// import org.springframework.core.env.Environment;
// import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootApplication
public class PasswordManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PasswordManagerApplication.class, args);
	}

	// @Bean
	// CommandLineRunner inspectMongo(
	// ApplicationContext context,
	// Environment environment) {

	// return args -> {
	// System.out.println("========== MONGO DEBUG ==========");

	// System.out.println("Environment URI: "
	// + environment.getProperty("spring.mongodb.uri"));

	// System.out.println("Environment database: "
	// + environment.getProperty("spring.mongodb.database"));

	// String[] mongoTemplates = context.getBeanNamesForType(MongoTemplate.class);

	// for (String name : mongoTemplates) {
	// MongoTemplate template = context.getBean(name, MongoTemplate.class);

	// System.out.println("MongoTemplate bean: " + name);
	// System.out.println("Database: " + template.getDb().getName());
	// }

	// System.out.println("=================================");
	// };
	// }
}
