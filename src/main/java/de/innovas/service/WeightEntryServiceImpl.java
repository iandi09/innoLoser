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
			if (weight == null)
				continue;
			for (String name : round.getParticipants()) {
				BigDecimal weightByName = weight.getWeightMap().get(name);
				WeightEval ret = minWeightMap.putIfAbsent(name, new WeightEval(weightByName, weight.getKw()));
				if (ret != null && weightByName != null) {
					if (ret.getMinWeight().compareTo(weightByName) == 1) {
						minWeightMap.put(name, new WeightEval(weightByName, weight.getKw()));
					} else if (getWeightWithMargin(ret.getMinWeight()).compareTo(weightByName) == -1) {
						int fails = ret.getFails() + 1;
						minWeightMap.get(name).setFails(fails);
					}

				}
			}
			i--;
		}
		return minWeightMap;
	}

	private BigDecimal getWeightWithMargin(BigDecimal weight) {
		return weight.multiply(BigDecimal.valueOf(1.015)).setScale(1, BigDecimal.ROUND_HALF_UP);
	}

	public void saveWeightEntry(Weight weightEntry) {
		weightEntryRepo.save(weightEntry);
	}
}
