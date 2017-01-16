package de.innovas.entities;

import java.util.List;

import org.springframework.data.annotation.Id;

public class RoundInfo {
	
	public static int ROUND_LENGTH = 24;

	@Id
	private String id;

	private int number;

	private int year;
	private int startKw;

	private List<String> participants;

	public RoundInfo() {
	}

	public RoundInfo(int number, int year, int startKw, int endKw, List<String> participants) {
		this.number = number;
		this.year = year;
		this.startKw = startKw;
		this.participants = participants;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getStartKw() {
		return startKw;
	}

	public void setStartKw(int startKw) {
		this.startKw = startKw;
	}

	public List<String> getParticipants() {
		return participants;
	}

	public void setParticipants(List<String> participants) {
		this.participants = participants;
	}
	
	@Override
	public boolean equals(Object roundInfo) {
		if (!(roundInfo instanceof RoundInfo)) {
			return false;
		}
		return this.number == ((RoundInfo) roundInfo).getNumber();
	}

	@Override
	public String toString() {
		return String.format("%s: KW: %s - %s", this.year, this.startKw, this.startKw + ROUND_LENGTH);
	}

}