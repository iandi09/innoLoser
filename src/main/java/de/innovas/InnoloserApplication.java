package de.innovas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.innovas.service.UserService;

@SpringBootApplication 
public class InnoloserApplication implements CommandLineRunner {
	
	@Autowired
	UserService userService;

	public static void main(String[] args) {
		SpringApplication.run(InnoloserApplication.class, args);
	}

	@Override
	public void run(String... arg0) throws Exception {
		if (!userService.isAnyUserRegistered()) {
			userService.registerUser("admin", "admin", true);
		}
		
	}
}
