package com.rctoyshop.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class BackendApplication {

	@PostConstruct
	public void init() {
		java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Asia/Taipei"));
	}

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@org.springframework.context.annotation.Bean
	public org.springframework.boot.CommandLineRunner commandLineRunner(
			com.rctoyshop.backend.repository.UserRepository userRepository) {
		return args -> {
			System.out.println("================= DB USER DUMP START =================");
			userRepository.findAll().forEach(u -> {
				System.out.println("ID: " + u.getId() +
						", Email: [" + u.getEmail() + "]" +
						", Password: [" + u.getPassword() + "]" +
						", Role: " + u.getRole() +
						", Status: " + u.getStatus());
			});
			System.out.println("================= DB USER DUMP END ===================");
		};
	}
}
