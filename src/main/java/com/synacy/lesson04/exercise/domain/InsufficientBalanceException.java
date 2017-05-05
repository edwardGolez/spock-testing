package com.synacy.lesson04.exercise.domain;

import java.math.BigDecimal;

public class InsufficientBalanceException extends Exception {

	private BankAccount bankAccount;
	private BigDecimal currentBalance;
	private BigDecimal amountToDiminish;

	public InsufficientBalanceException(BankAccount bankAccount, BigDecimal currentBalance, BigDecimal amountToDiminish) {
		this.bankAccount = bankAccount;
		this.currentBalance = currentBalance;
		this.amountToDiminish = amountToDiminish;
	}

	public BankAccount getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(BankAccount bankAccount) {
		this.bankAccount = bankAccount;
	}

	public BigDecimal getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(BigDecimal currentBalance) {
		this.currentBalance = currentBalance;
	}

	public BigDecimal getAmountToDiminish() {
		return amountToDiminish;
	}

	public void setAmountToDiminish(BigDecimal amountToDiminish) {
		this.amountToDiminish = amountToDiminish;
	}
}
