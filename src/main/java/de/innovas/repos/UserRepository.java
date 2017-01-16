package de.innovas.repos;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.innovas.entities.User;

public interface UserRepository extends MongoRepository<User, String> {

	public List<User> findAll();

	public User findByName(String name);
}