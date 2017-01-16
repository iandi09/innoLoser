package de.innovas.service;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import de.innovas.entities.RoundInfo;
import de.innovas.repos.RoundInfoRepository;
import de.innovas.util.KwInfo;

@Service
public class RoundInfoServiceImpl implements RoundInfoService {
	
	@Autowired
	private RoundInfoRepository roundInfoRepo;
	
	public List<RoundInfo> getAll() {
		return roundInfoRepo.findAll();
	}
	
	public Map<Integer, RoundInfo> getAllAsMap() {
		List<RoundInfo> roundInfoList = roundInfoRepo.findAll();
		Map<Integer, RoundInfo> roundInfoMap = new HashMap<>();
		for (RoundInfo round : roundInfoList) {
			roundInfoMap.put(round.getNumber(), round);
		}
		return roundInfoMap;
	}
	
	public RoundInfo getLatestRoundInfo() {
		PageRequest request = new PageRequest(0, 1, new Sort(Sort.Direction.DESC, "number"));
		List<RoundInfo> roundInfoFromDb = roundInfoRepo.findLatestRound(request).getContent();
		RoundInfo roundInfo = null;
		if (!roundInfoFromDb.isEmpty()) {
			roundInfo = roundInfoFromDb.get(0);
		}
		return roundInfo;
	}
	
	public RoundInfo startNewRound() {
		RoundInfo latestRound = getLatestRoundInfo();
		RoundInfo newRound = new RoundInfo();
		int number = (latestRound != null) ? latestRound.getNumber() + 1 : 1;
		newRound.setNumber(number);
		Calendar cal = Calendar.getInstance();
		newRound.setYear(cal.get(Calendar.YEAR));
		newRound.setStartKw(cal.get(Calendar.WEEK_OF_YEAR));
		newRound.setParticipants(new ArrayList<String>());
		roundInfoRepo.save(newRound);
		return newRound;
	}

	public void saveNewParticipant(RoundInfo round, String name) {
		round.getParticipants().add(name);
		roundInfoRepo.save(round);
	}
	
	public KwInfo getEndKw(RoundInfo round) {
		int endKw = round.getStartKw() + RoundInfo.ROUND_LENGTH;
		int diffYear = endKw / 52;
		endKw %= 52;
		return new KwInfo(round.getYear() + diffYear, endKw);
	}
	
}
