package com.synacy.lesson04.demo.dao;

import com.synacy.lesson04.demo.domain.BankAccount;
import com.synacy.lesson04.demo.domain.Transaction;

public interface TransactionDao {

	void saveTransaction(Transaction transaction);

	void fetchAllTransactionsOfBankAccount(BankAccount bankAccount);

}
