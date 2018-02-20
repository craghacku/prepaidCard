package com.imaginecurve.prepaidCard.implementation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CardAccount {
	private Long id;
	private Double currentAmount;
	
	private Map<Long, Transaction> transactionIdToTransaction = new HashMap<Long, Transaction>();
	private List<Transaction> confirmedTransactions = new ArrayList<Transaction>();
	private Set<Long> pendingTransactions = new HashSet<Long>();
	
	public CardAccount(Long id) {
		this.setId(id);
		currentAmount = 0.0;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public synchronized Transaction topUp(Double amount) {
		currentAmount = currentAmount + amount;
		Transaction topUpTransaction = new Transaction(
					IdGenerator.INSTANCE.generate(),
					LocalDateTime.now(),
					amount,
					currentAmount,
					TransactionStatus.CONFIRMED
				);
		transactionIdToTransaction.put(topUpTransaction.getId(), topUpTransaction);
		confirmedTransactions.add(topUpTransaction);
		return topUpTransaction;
	}
	
	public synchronized Transaction authorize(Double amount) {
		if (currentAmount < amount) {
			return new Transaction(
					IdGenerator.INSTANCE.generate(),
					LocalDateTime.now(),
					-amount,
					currentAmount,
					TransactionStatus.INSUFFICIENT_FUNDS
				);
		}
		currentAmount -= amount;
		Transaction payTransaction = new Transaction(
				IdGenerator.INSTANCE.generate(),
				LocalDateTime.now(),
				-amount,
				currentAmount,
				TransactionStatus.PENDING
			);
		pendingTransactions.add(payTransaction.getId());
		return payTransaction;
	}
	
	public synchronized Transaction capture(Long transactionId, Double amount) {
		if (!transactionIdToTransaction.containsKey(transactionId)) {
			return new Transaction(
					IdGenerator.INSTANCE.generate(),
					LocalDateTime.now(),
					-amount,
					currentAmount,
					TransactionStatus.INSUFFICIENT_AUTHORIZATION
				);
		}
		Transaction transaction = transactionIdToTransaction.get(transactionId);
		double previousAmount = -transaction.getAmount();
		if (amount > previousAmount) {
			return new Transaction(
					IdGenerator.INSTANCE.generate(),
					LocalDateTime.now(),
					-amount,
					currentAmount,
					TransactionStatus.INSUFFICIENT_AUTHORIZATION
				);
		}
		if (amount == previousAmount) {
			transaction.setTime(LocalDateTime.now());
			transaction.setAccountBalanceAfterTransaction(currentAmount);
			transaction.setStatus(TransactionStatus.CONFIRMED);
			pendingTransactions.remove(transaction);
			confirmedTransactions.add(transaction);
			return transaction;
		}
		Transaction partialCapture = new Transaction(
				IdGenerator.INSTANCE.generate(),
				LocalDateTime.now(),
				-amount,
				currentAmount,
				TransactionStatus.CONFIRMED
			);
		confirmedTransactions.add(partialCapture);
		transactionIdToTransaction.put(partialCapture.getId(), partialCapture);
		transaction.setAmount(previousAmount+amount);
		return partialCapture;
	}

	public synchronized Transaction refund(Long transactionId, Double amount) {
		Transaction transaction = transactionIdToTransaction.get(transactionId);
		if (transaction == null || transaction.getStatus() != TransactionStatus.CONFIRMED) {
			return new Transaction(
					IdGenerator.INSTANCE.generate(),
					LocalDateTime.now(),
					-amount,
					currentAmount,
					TransactionStatus.INSUFFICIENT_AUTHORIZATION
				);

		}
		if (-transaction.getAmount() < amount) {
			new Transaction(
					IdGenerator.INSTANCE.generate(),
					LocalDateTime.now(),
					-amount,
					currentAmount,
					TransactionStatus.INSUFFICIENT_FUNDS_FOR_REFUND
				);
		}
		currentAmount += amount;
		Transaction refund = new Transaction(
				IdGenerator.INSTANCE.generate(),
				LocalDateTime.now(),
				amount,
				currentAmount,
				TransactionStatus.CONFIRMED
				);
		confirmedTransactions.add(refund);
		transactionIdToTransaction.put(refund.getId(), refund);
		return refund;
	}
	
	public synchronized Transaction reverse(Long transactionId, Double amount) {
		Transaction transaction = transactionIdToTransaction.get(transactionId);
		if (transaction == null || transaction.getStatus() != TransactionStatus.PENDING) {
			return new Transaction(
					IdGenerator.INSTANCE.generate(),
					LocalDateTime.now(),
					-amount,
					currentAmount,
					TransactionStatus.INSUFFICIENT_AUTHORIZATION
				);
		}
		Double authorizedAmount = transaction.getAmount();
		if (-authorizedAmount < amount) {
			new Transaction(
					IdGenerator.INSTANCE.generate(),
					LocalDateTime.now(),
					-amount,
					currentAmount,
					TransactionStatus.INSUFFICIENT_FUNDS_FOR_REVERSE
				);
		}
		currentAmount += amount;
		transaction.setAmount(authorizedAmount + amount);
		transaction.setAccountBalanceAfterTransaction(currentAmount);
		if (authorizedAmount + amount == 0.0) {
			pendingTransactions.remove(transaction);
			transactionIdToTransaction.remove(transactionId);
			transaction.setStatus(TransactionStatus.CANCELLED);
		}
		return transaction;
	}
}
