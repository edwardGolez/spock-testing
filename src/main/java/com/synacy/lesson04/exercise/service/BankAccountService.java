package com.synacy.lesson04.exercise.service;

import com.synacy.lesson04.exercise.domain.BankAccount;
import com.synacy.lesson04.exercise.domain.InsufficientBalanceException;
import com.synacy.lesson04.exercise.domain.InvalidBankAccountStatusException;
import com.synacy.lesson04.exercise.domain.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface BankAccountService {

	// diminish bank account balance with given amount (then save)
	// create transaction then apply(set status to CLEARED) then save
	void withdraw(BankAccount bankAccount, BigDecimal amount) throws InsufficientBalanceException;

	void deposit(BankAccount bankAccount, BigDecimal amount) throws InvalidBankAccountStatusException;

	void transfer(BankAccount sourceBankAccount, BankAccount destinationBankAccount, BigDecimal amount) throws InsufficientBalanceException, InvalidBankAccountStatusException;

	// please take note that transactions are to be sorted starting from most recent
	List<Transaction> fetchAllTransactions(BankAccount bankAccount) throws Exception;

}
