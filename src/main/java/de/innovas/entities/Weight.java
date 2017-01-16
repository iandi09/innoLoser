package de.innovas.entities;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.springframework.data.annotation.Id;

public class Weight {

	@Id
	private String id;

	private int round;
	private int kw;
	private int number;
	private Map<String, BigDecimal> weightMap;

	private Date creationDate;
	private String user;

	public Weight() {
	}

	public Weight(int round, int kw, int number, Map<String, BigDecimal> weightMap, Date creationDate, String user) {
		this.round = round;
		this.kw = kw;
		this.number = number;
		this.weightMap = weightMap;
		this.creationDate = creationDate;
		this.user = user;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public int getKw() {
		return kw;
	}

	public void setKw(int kw) {
		this.kw = kw;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public Map<String, BigDecimal> getWeightMap() {
		return weightMap;
	}

	public void setWeightMap(Map<String, BigDecimal> weightMap) {
		this.weightMap = weightMap;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	@Override
	public Weight clone() {
		return new Weight(this.round, this.kw, this.number, this.weightMap, this.creationDate, this.user);
	}

	@Override
	public String toString() {
		return String.format("Weight[kw=%s]", this.kw);
	}

}