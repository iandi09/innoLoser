package de.innovas.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import de.innovas.entities.RoundInfo;
import de.innovas.entities.Weight;
import de.innovas.repos.WeightRepository;
import de.innovas.util.WeightEval;

@Service
public class WeightEntryServiceImpl implements WeightEntryService {

	@Autowired
	private WeightRepository weightEntryRepo;

	public List<Weight> getWeightEntryList(RoundInfo round, Integer kw) {
		return weightEntryRepo.findByRoundAndKw(round.getNumber(), kw);
	}

	public Weight getLatestWeightEntry(RoundInfo round, Integer kw) {
		PageRequest request = new PageRequest(0, 1, new Sort(Direction.DESC, "creationDate"));
		List<Weight> weightList = weightEntryRepo.findLatestByRoundAndKw(round.getNumber(), kw, request).getContent();
		Weight weight = null;
		if (!weightList.isEmpty()) {
			weight = weightList.get(0);
		}
		return weight;
	}

	public Map<String, WeightEval> getWeightEvalMap(RoundInfo round) {
		int kw = round.getStartKw();
		int i = RoundInfo.ROUND_LENGTH;
		Map<String, WeightEval> minWeightMap = new HashMap<>();
		while (i > 0) {
			Weight weight = getLatestWeightEntry(round, kw++ % 52);
			if (weight == null) continue;
			for (String name : round.getParticipants()) {
				BigDecimal weightByName = weight.getWeightMap().get(name);
				BigDecimal ret = minWeightMap.putIfAbsent(name, weightByName);
				if (ret != null && weightByName != null && ret.compareTo(weightByName) == 1) {
					minWeightMap.put(name, weightByName);
				}
			}
			i--;
		}
		return minWeightMap;
	}

	public void saveWeightEntry(Weight weightEntry) {
		weightEntryRepo.save(weightEntry);
	}
}
