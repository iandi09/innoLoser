package de.innovas.service;

import java.util.List;

import de.innovas.entities.RoundInfo;
import de.innovas.entities.Weight;

public interface WeightEntryService {

	public List<Weight> getWeightEntryList(RoundInfo round, Integer kw);

	public Weight getLatestWeightEntry(RoundInfo round, Integer kw);

	public void saveWeightEntry(Weight weightEntry);
}
