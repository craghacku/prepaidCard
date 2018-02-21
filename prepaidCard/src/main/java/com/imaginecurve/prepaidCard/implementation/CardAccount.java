package com.imaginecurve.prepaidCard.implementation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CardAccount {
	private Long id;
	private Double currentAmount;
	
	private Map<Long, Transaction> transactionIdToTransaction = new HashMap<Long, Transaction>();
	private List<Transaction> confirmedTransactions = new ArrayList<Transaction>();
	private Set<Long> pendingTransactions = new HashSet<Long>();
	private Map<Long, Double> possibleRefund = new HashMap<Long, Double>();
	
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
		transactionIdToTransaction.put(payTransaction.getId(), payTransaction);
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
			pendingTransactions.remove(transactionId);
			confirmedTransactions.add(transaction);
			possibleRefund.put(transaction.getId(), amount);
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
		transaction.setAmount(amount-previousAmount);
		possibleRefund.put(partialCapture.getId(), amount);
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
		Double possibleRefundForTransaction = possibleRefund.get(transactionId);
		
		if (possibleRefundForTransaction == null
				|| possibleRefundForTransaction < amount) {
			return new Transaction(
					IdGenerator.INSTANCE.generate(),
					LocalDateTime.now(),
					-amount,
					currentAmount,
					TransactionStatus.INSUFFICIENT_FUNDS_FOR_REFUND
				);
		}
		possibleRefund.put(transactionId, possibleRefundForTransaction-amount);
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
			return new Transaction(
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
			transactionIdToTransaction.remove(transactionId);
			pendingTransactions.remove(transactionId);
			transaction.setStatus(TransactionStatus.CANCELLED);
		}
		return transaction;
	}

	public synchronized List<Transaction> getPendingTransactions() {
		return pendingTransactions.stream()
				.map(id -> transactionIdToTransaction.get(id))
				.sorted(new Comparator<Transaction> (){

					@Override
					public int compare(Transaction o1, Transaction o2) {
						if (o1 == o2) {
							return 0;
						}
						if (o1 == null) {
							return -1;
						}
						if (o2 == null) {
							return 1;
						}
						return o1.getTime().compareTo(o2.getTime());
					}
					
				})
				.collect(Collectors.toList());
	}

	public synchronized List<Transaction> getSummary() {
		return new ArrayList<Transaction>(confirmedTransactions);
	}

	public synchronized CardAccountShortenedBalance getShortenedBalance() {
		return new CardAccountShortenedBalance(currentAmount,
				currentAmount 
				+ pendingTransactions.stream()
				.mapToDouble(id -> transactionIdToTransaction.get(id).getAmount())
				.sum());
	}
}
