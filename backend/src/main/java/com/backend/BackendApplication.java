package com.backend;

import com.backend.domain.Role;
import com.backend.service.AdminService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	CommandLineRunner run(AdminService adminService) {
		return args -> {

			adminService.saveRole(new Role(null, "ROLE_SUPER_ADMIN"));
			adminService.saveRole(new Role(null, "ROLE_ADMIN"));

			adminService.register("Kshitiz", "Jain", "coolkshitiz786@gmail.com", 2, true, true);

		};
	}

}
