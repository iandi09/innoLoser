package de.innovas.service;

import java.util.List;
import java.util.Map;

import de.innovas.entities.RoundInfo;
import de.innovas.entities.Weight;
import de.innovas.util.WeightEval;

public interface WeightEntryService {

	public List<Weight> getWeightEntryList(RoundInfo round, Integer kw);

	public Weight getLatestWeightEntry(RoundInfo round, Integer kw);
	
	public Map<String, WeightEval> getWeightEvalMap(RoundInfo round);

	public void saveWeightEntry(Weight weightEntry);
}
