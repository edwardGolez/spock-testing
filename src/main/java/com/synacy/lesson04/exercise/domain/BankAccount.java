package com.synacy.lesson04.exercise.domain;

import java.math.BigDecimal;

public class BankAccount {

	private AccountOwner owner;
	private BigDecimal balance;
	private BankAccountStatus status;

	public AccountOwner getOwner() {
		return owner;
	}

	public void setOwner(AccountOwner owner) {
		this.owner = owner;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public BankAccountStatus getStatus() {
		return status;
	}

	public void setStatus(BankAccountStatus status) {
		this.status = status;
	}
}
