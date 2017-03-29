package de.innovas.repos;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import de.innovas.entities.RoundInfo;

public interface RoundInfoRepository extends MongoRepository<RoundInfo, String> {

	public List<RoundInfo> findAll();

	public RoundInfo findByNumber(Integer number);
	
	@Query(value="{}")
	public Page<RoundInfo> findLatestRound(Pageable pageable);
}