package com.synacy.lesson04.demo.service;

import com.synacy.lesson04.demo.domain.BankAccount;
import com.synacy.lesson04.demo.domain.Transaction;

public interface TransactionService {

	void applyTransaction(Transaction transaction, BankAccount bankAccount);

}
