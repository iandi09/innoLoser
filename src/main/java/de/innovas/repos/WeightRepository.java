package de.innovas.repos;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import de.innovas.entities.Weight;

public interface WeightRepository extends MongoRepository<Weight, String> {

	public List<Weight> findByKw(String kw);
	
	public List<Weight> findByRoundAndKw(int round, int kw);
	
	@Query("{round: ?0, kw: ?1}")
	public Page<Weight> findLatestByRoundAndKw(int round, int kw, Pageable pageable);

}