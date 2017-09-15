package de.innovas.entities;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
public class RoundInfo {
	
	public static int ROUND_LENGTH = 26;

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", updatable = false, nullable = false)
	private int id;

	private int number;

	private int year;
	private int startKw;

	@ElementCollection
	@Embedded
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
