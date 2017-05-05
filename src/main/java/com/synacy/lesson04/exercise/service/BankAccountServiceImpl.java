package com.synacy.lesson04.exercise.service;

import com.synacy.lesson04.exercise.dao.BankAccountDao;
import com.synacy.lesson04.exercise.dao.TransactionDao;
import com.synacy.lesson04.exercise.domain.*;

import java.math.BigDecimal;
import java.util.*;

public class BankAccountServiceImpl implements BankAccountService {

	private BankAccountDao bankAccountDao;
	private TransactionDao transactionDao;

	@Override
	public void withdraw(BankAccount bankAccount, BigDecimal amount)
		throws InsufficientBalanceException {

		BigDecimal balance = bankAccount.getBalance();
		if(amount.compareTo(balance) > 0){
			throw new InsufficientBalanceException(bankAccount, balance, amount);
		}

		BigDecimal netBalance = balance.subtract(amount);

		Transaction transaction = new Transaction(
				bankAccount, TransactionType.CREDIT, amount, new Date());
		bankAccount.setBalance(netBalance);
		transaction.setStatus(TransactionStatus.CLEARED);
		transactionDao.saveTransaction(transaction);

		bankAccountDao.saveBankAccount(bankAccount);
	}

	@Override
	public void deposit(BankAccount bankAccount, BigDecimal amount) {

		BigDecimal currentBalance = bankAccount.getBalance();
		BigDecimal newBalance = currentBalance.add(amount);

		Transaction transaction = new Transaction(bankAccount, TransactionType.DEBIT, amount, new Date());
		bankAccount.setBalance(newBalance);
		transaction.setStatus(TransactionStatus.CLEARED);
		transactionDao.saveTransaction(transaction);

		bankAccountDao.saveBankAccount(bankAccount);
	}

	@Override
	public void transfer(BankAccount sourceBankAccount, BankAccount destinationBankAccount, BigDecimal amount)
			throws InsufficientBalanceException {

		BigDecimal currentBalance = sourceBankAccount.getBalance();

		if(currentBalance.compareTo(amount) < 0) {
			throw new InsufficientBalanceException(sourceBankAccount, currentBalance, amount);
		}

		BigDecimal newBalance = currentBalance.subtract(amount);

		Transaction creditTransaction = new Transaction(sourceBankAccount, TransactionType.CREDIT, amount, new Date());
		sourceBankAccount.setBalance(newBalance);

		currentBalance = destinationBankAccount.getBalance();
		newBalance = currentBalance.add(amount);

		Transaction debitTransaction = new Transaction(destinationBankAccount, TransactionType.DEBIT, amount, new Date());
		destinationBankAccount.setBalance(newBalance);

		transactionDao.saveTransaction(creditTransaction);
		bankAccountDao.saveBankAccount(sourceBankAccount);
		transactionDao.saveTransaction(debitTransaction);
		bankAccountDao.saveBankAccount(destinationBankAccount);
	}

	@Override
	public List<Transaction> fetchAllTransactions(BankAccount bankAccount) {
		List<Transaction> sortedTransactions = new ArrayList<>(transactionDao.fetchAllTransactionsOfBankAccount(bankAccount));

		Collections.sort(sortedTransactions, (transaction1, transaction2) ->
				transaction1.getTransactionDate().after(transaction2.getTransactionDate())? -1 : 1);

		return sortedTransactions;
	}

	public BankAccountDao getBankAccountDao() {
		return bankAccountDao;
	}

	public void setBankAccountDao(BankAccountDao bankAccountDao) {
		this.bankAccountDao = bankAccountDao;
	}

	public TransactionDao getTransactionDao() {
		return transactionDao;
	}

	public void setTransactionDao(TransactionDao transactionDao) {
		this.transactionDao = transactionDao;
	}
}
