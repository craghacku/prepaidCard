package com.imaginecurve.prepaidCard.implementation;

import java.time.LocalDateTime;

public class Transaction {
	private Long id;
	private LocalDateTime time;
	private Double amount;
	private Double accountBalanceAfterTransaction;
	private TransactionStatus status;
	public Transaction(Long id, LocalDateTime time, Double amount,
			Double accountBalanceAfterTransaction, TransactionStatus status) {
		super();
		this.id = id;
		this.time = time;
		this.amount = amount;
		this.accountBalanceAfterTransaction = accountBalanceAfterTransaction;
		this.status = status;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public LocalDateTime getTime() {
		return time;
	}
	public void setTime(LocalDateTime time) {
		this.time = time;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public Double getAccountBalanceAfterTransaction() {
		return accountBalanceAfterTransaction;
	}
	public void setAccountBalanceAfterTransaction(Double accountBalanceAfterTransaction) {
		this.accountBalanceAfterTransaction = accountBalanceAfterTransaction;
	}
	public TransactionStatus getStatus() {
		return status;
	}
	public void setStatus(TransactionStatus status) {
		this.status = status;
	}
	
}
