package com.synacy.lesson04.exercise.service;

import com.synacy.lesson04.exercise.dao.BankAccountDao;
import com.synacy.lesson04.exercise.dao.TransactionDao;
import com.synacy.lesson04.exercise.domain.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
		BigDecimal balance = bankAccount.getBalance();
		BigDecimal netBalance = balance.add(amount);

		bankAccount.setBalance(netBalance);

		Transaction transaction = new Transaction(bankAccount, TransactionType.DEBIT, amount, new Date());
		transaction.setStatus(TransactionStatus.CLEARED);
		transactionDao.saveTransaction(transaction);

		bankAccountDao.saveBankAccount(bankAccount);
	}

	@Override
	public void transfer(BankAccount sourceBankAccount, BankAccount destinationBankAccount, BigDecimal amount)
			throws InsufficientBalanceException {

		BigDecimal sourceBankAccountBalance = sourceBankAccount.getBalance();
		BigDecimal destinationBankAccountBalance = destinationBankAccount.getBalance();

		if(amount.compareTo(sourceBankAccountBalance) > 0){
			throw new InsufficientBalanceException(sourceBankAccount, sourceBankAccountBalance, amount);
		}

		BigDecimal sourceNetBalance = sourceBankAccountBalance.subtract(amount);
		BigDecimal destinationNetBalance = destinationBankAccountBalance.add(amount);

		sourceBankAccount.setBalance(sourceNetBalance);
		destinationBankAccount.setBalance(destinationNetBalance);

		Transaction sourceBankAccountTransaction = new Transaction(sourceBankAccount, TransactionType.CREDIT, amount, new Date());
		sourceBankAccountTransaction.setStatus(TransactionStatus.CLEARED);

		Transaction destinationBankAccountTransaction = new Transaction(destinationBankAccount, TransactionType.DEBIT, amount, new Date());
		destinationBankAccountTransaction.setStatus(TransactionStatus.CLEARED);

		transactionDao.saveTransaction(sourceBankAccountTransaction);
		transactionDao.saveTransaction(destinationBankAccountTransaction);

		bankAccountDao.saveBankAccount(sourceBankAccount);
		bankAccountDao.saveBankAccount(destinationBankAccount);
	}

	@Override
	public List<Transaction> fetchAllTransactions(BankAccount bankAccount) {
		Set<Transaction> allTransactionSet = transactionDao.fetchAllTransactionsOfBankAccount(bankAccount);

		List<Transaction> allTransactionList = new ArrayList<>();
		allTransactionList.addAll(allTransactionSet);

		return  allTransactionList;
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
