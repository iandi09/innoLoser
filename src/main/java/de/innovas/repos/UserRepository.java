package de.innovas.repos;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import de.innovas.entities.User;

public interface UserRepository extends CrudRepository<User, String> {

	public List<User> findAll();

	public User findByName(String name);
}