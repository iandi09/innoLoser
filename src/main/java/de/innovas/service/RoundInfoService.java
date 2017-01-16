package de.innovas.service;


import java.util.List;
import java.util.Map;

import de.innovas.entities.RoundInfo;
import de.innovas.util.KwInfo;

public interface RoundInfoService {
	
	public List<RoundInfo> getAll();
	
	public Map<Integer, RoundInfo> getAllAsMap();
	
	public RoundInfo getLatestRoundInfo();
	
	public RoundInfo startNewRound();

	public void saveNewParticipant(RoundInfo round, String value);

	public KwInfo getEndKw(RoundInfo roundInfo);
	
}
