package com.synacy.lesson04.exercise.service;

import com.synacy.lesson04.exercise.domain.BankAccount;
import com.synacy.lesson04.exercise.domain.InsufficientBalanceException;
import com.synacy.lesson04.exercise.domain.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface BankAccountService {

	// (1) diminish bank account balance with given amount (then save)
	// (2) create transaction then apply(set status to CLEARED) then save
	void withdraw(BankAccount bankAccount, BigDecimal amount) throws InsufficientBalanceException;

	// (1) increase bank account balance with given amount (then save)
	// (2) create transaction then apply(set status to CLEARED) then save
	void deposit(BankAccount bankAccount, BigDecimal amount);

    // (1) check if bank account's (source) has sufficient balance from the given amount to proceed transfer, otherwise throw an InsufficientBalanceException
    // (2) transfer amount from a source bank account to a desired bank account destination with given amount (then save)
    // (3) create transaction then apply(set status to CLEARED) then save
	void transfer(BankAccount sourceBankAccount, BankAccount destinationBankAccount, BigDecimal amount) throws InsufficientBalanceException;

	// please take note that transactions are to be sorted starting from most recent
	// (1) fetch all transactions from a given bank account and return a list
    // (2) make sure that transactions are sorted by the most recent transaction
    List<Transaction> fetchAllTransactions(BankAccount bankAccount);

}
