package de.innovas.repos;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import de.innovas.entities.Weight;

public interface WeightRepository extends CrudRepository<Weight, String> {

	public List<Weight> findByKw(String kw);
	
	public List<Weight> findByRoundAndKw(int round, int kw);
	
	@Query("select w from Weight w where w.round = :round and w.kw = :kw")
	public Page<Weight> findLatestByRoundAndKw(int round, int kw, Pageable pageable);

}