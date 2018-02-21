package com.imaginecurve.prepaidCard.implementation;

public class CardAccountShortenedBalance {
	
	private Double currentAmount;
	private Double currentAmountIncludingBlocked;
	public CardAccountShortenedBalance(Double currentAmount, Double currentAmountIncludingBlocked) {
		super();
		this.currentAmount = currentAmount;
		this.currentAmountIncludingBlocked = currentAmountIncludingBlocked;
	}
	public Double getCurrentAmount() {
		return currentAmount;
	}
	public void setCurrentAmount(Double currentAmount) {
		this.currentAmount = currentAmount;
	}
	public Double getCurrentAmountIncludingBlocked() {
		return currentAmountIncludingBlocked;
	}
	public void setCurrentAmountIncludingBlocked(Double currentAmountIncludingBlocked) {
		this.currentAmountIncludingBlocked = currentAmountIncludingBlocked;
	}
	
}
