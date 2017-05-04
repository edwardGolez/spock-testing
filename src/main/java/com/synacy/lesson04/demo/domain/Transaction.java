package com.synacy.lesson04.demo.domain;

import java.math.BigDecimal;
import java.util.Date;

public class Transaction {

	private BankAccount bankAccount;
	private TransactionType type;
	private BigDecimal amount;
	private Date transactionDate;
	private TransactionStatus status;

	public Transaction(BankAccount bankAccount, TransactionType type, BigDecimal amount, Date transactionDate) {
		this.bankAccount = bankAccount;
		this.type = type;
		this.amount = amount;
		this.transactionDate = transactionDate;
	}

}
