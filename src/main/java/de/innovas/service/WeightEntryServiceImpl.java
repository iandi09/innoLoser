package de.innovas.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import de.innovas.entities.RoundInfo;
import de.innovas.entities.Weight;
import de.innovas.repos.WeightRepository;

@Service
public class WeightEntryServiceImpl implements WeightEntryService {
	
	@Autowired
	private WeightRepository weightEntryRepo;
	
	public List<Weight> getWeightEntryList(RoundInfo round, Integer kw) {
		return weightEntryRepo.findByRoundAndKw(round.getNumber(), kw);
	}
	
	public Weight getLatestWeightEntry(RoundInfo round, Integer kw) {
		PageRequest request = new PageRequest(0,  1, new Sort(Direction.DESC, "creationDate"));
		List<Weight> weightList = weightEntryRepo.findLatestByRoundAndKw(round.getNumber(), kw, request).getContent();
		Weight weight = null;
		if (!weightList.isEmpty()) {
			weight = weightList.get(0);
		}
		return weight;
	}
	
	public void saveWeightEntry(Weight weightEntry) {
		weightEntryRepo.save(weightEntry);
	}
}
