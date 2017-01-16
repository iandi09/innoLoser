package de.innovas.service;

import de.innovas.entities.User;

public interface UserService {

	public User getCurrentUser();

	public String getCurrentUserName();
	
	public boolean isAnyUserRegistered();

	public void registerUser(String name, String password, boolean admin);

	public boolean checkCredentials(String name, String password);
}
