package com.synacy.lesson04.exercise.dao;

import com.synacy.lesson04.exercise.domain.BankAccount;
import com.synacy.lesson04.exercise.domain.Transaction;

import java.util.Set;

public interface TransactionDao {

	void saveTransaction(Transaction transaction);

	Set<Transaction> fetchAllTransactionsOfBankAccount(BankAccount bankAccount);

}
