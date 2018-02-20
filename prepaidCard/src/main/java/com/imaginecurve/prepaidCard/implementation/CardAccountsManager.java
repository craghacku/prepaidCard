package com.imaginecurve.prepaidCard.implementation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CardAccountsManager {
	
	public static final CardAccountsManager INSTANCE = new CardAccountsManager();
	
	private CardAccountsManager() {
		
	}
	
	private Map<Long,CardAccount> accounts = new ConcurrentHashMap<Long,CardAccount>();
	
	public Long createCard() {
		Long id = IdGenerator.INSTANCE.generate();
		CardAccount card = new CardAccount(id);
		accounts.put(id, card);
		return id;
	}
	
	public Transaction topUp (Long id, Double amount) {
		CardAccount account = accounts.get(id);
		if (account == null) {
			return null;
		}
		return account.topUp(amount);
	}
	
	public Transaction authorize(Long id, Double amount) {
		CardAccount account = accounts.get(id);
		if (account == null) {
			return null;
		}
		return account.authorize(amount);
	}
	
	public Transaction capture(Long id, Long transactionId, Double amount) {
		CardAccount account = accounts.get(id);
		if (account == null) {
			return null;
		}
		return account.capture(transactionId, amount);
	}
	
	public Transaction refund (Long id, Long transactionId, Double amount) {
		CardAccount account = accounts.get(id);
		if (account == null) {
			return null;
		}
		return account.refund(transactionId,amount);
	}
	
	public Transaction reverse (Long id, Long transactionId, Double amount) {
		CardAccount account = accounts.get(id);
		if (account == null) {
			return null;
		}
		return account.reverse(transactionId,amount);
	}
}
