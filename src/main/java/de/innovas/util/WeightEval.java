package de.innovas.util;

import java.math.BigDecimal;

public class WeightEval {
	
	BigDecimal minWeight;
	int minWeightKw;
	int fails;
	
	public BigDecimal getMinWeight() {
		return minWeight;
	}
	public void setMinWeight(BigDecimal minWeight) {
		this.minWeight = minWeight;
	}
	public int getMinWeightKw() {
		return minWeightKw;
	}
	public void setMinWeightKw(int minWeightKw) {
		this.minWeightKw = minWeightKw;
	}
	public int getFails() {
		return fails;
	}
	public void setFails(int fails) {
		this.fails = fails;
	}
}
