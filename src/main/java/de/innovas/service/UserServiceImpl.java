package de.innovas.service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Service;

import com.vaadin.server.VaadinSession;

import de.innovas.entities.User;
import de.innovas.repos.UserRepository;
import de.innovas.util.PasswordEncoder;

@Service
@EnableJpaRepositories("de.innovas.repos")
@ComponentScan(basePackages = { "de.innovas.entities" })
@EntityScan("de.innovas.entities")  
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepo;
	
	public User getCurrentUser() {
		String currentUserName = getCurrentUserName();
		return userRepo.findByName(currentUserName);
	}
	
	public String getCurrentUserName() {
		return (String) VaadinSession.getCurrent().getAttribute("user");
	}

	public boolean isAnyUserRegistered() {
		return !userRepo.findAll().isEmpty();
	}
	
	public void registerUser(String name, String password, boolean admin) {
		User newUser;
		try {
			newUser = new User(name, PasswordEncoder.generateStrongPasswordHash(password));
			newUser.setAdmin(admin);
			userRepo.save(newUser);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean checkCredentials(String name, String password) {
		User user = userRepo.findByName(name);
		if (user != null) {
			try {
				return PasswordEncoder.validatePassword(password, user.getPassword());
			} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
}
