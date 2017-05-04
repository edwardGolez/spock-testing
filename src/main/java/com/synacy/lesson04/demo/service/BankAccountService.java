package com.synacy.lesson04.demo.service;

import com.synacy.lesson04.demo.domain.BankAccount;
import com.synacy.lesson04.demo.domain.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface BankAccountService {

	void withdraw(BankAccount bankAccount, BigDecimal amount);

	void deposit(BankAccount bankAccount, BigDecimal amount);

	void transfer(BankAccount sourceBankAccount, BankAccount destinationBankAccount, BigDecimal amount);

	List<Transaction> fetchAllTransactions(BankAccount bankAccount);

}
