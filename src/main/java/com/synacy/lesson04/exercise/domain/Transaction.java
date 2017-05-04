package com.synacy.lesson04.exercise.domain;

import java.math.BigDecimal;
import java.util.Date;

public class Transaction {

	private BankAccount bankAccount;
	private TransactionType type;
	private BigDecimal amount;
	private Date transactionDate;
	private TransactionStatus status;

	public Transaction() {
		this.status = TransactionStatus.PENDING;
	}

	public Transaction(BankAccount bankAccount, TransactionType type, BigDecimal amount, Date transactionDate) {
		this.bankAccount = bankAccount;
		this.type = type;
		this.amount = amount;
		this.transactionDate = transactionDate;
		this.status = TransactionStatus.PENDING;
	}

	public BankAccount getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(BankAccount bankAccount) {
		this.bankAccount = bankAccount;
	}

	public TransactionType getType() {
		return type;
	}

	public void setType(TransactionType type) {
		this.type = type;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public TransactionStatus getStatus() {
		return status;
	}

	public void setStatus(TransactionStatus status) {
		this.status = status;
	}
}
