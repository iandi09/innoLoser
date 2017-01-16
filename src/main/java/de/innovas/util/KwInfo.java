package de.innovas.util;

import java.util.Calendar;

public class KwInfo {
	
	private int year;
	private int kw;
	
	public KwInfo(int year, int kw) {
		this.year = year;
		this.kw = kw;
	}
	
	public static KwInfo thisWeek() {
		Calendar cal = Calendar.getInstance();
		return new KwInfo(cal.get(Calendar.YEAR), cal.get(Calendar.WEEK_OF_YEAR));
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getKw() {
		return kw;
	}

	public void setKw(int kw) {
		this.kw = kw;
	}
	
	public boolean before(KwInfo kwInfo) {
		if (kwInfo.getYear() != this.year) {
			return this.year < kwInfo.getYear();
		}
		return this.kw < kwInfo.getKw();
	}
}
