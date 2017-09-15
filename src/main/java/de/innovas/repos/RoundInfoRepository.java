package de.innovas.repos;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import de.innovas.entities.RoundInfo;

public interface RoundInfoRepository extends CrudRepository<RoundInfo, String> {

	public List<RoundInfo> findAll();

	public RoundInfo findByNumber(Integer number);
	
	@Query(value="select r from RoundInfo r")
	public Page<RoundInfo> findLatestRound(Pageable pageable);
}