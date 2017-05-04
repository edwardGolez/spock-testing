package com.synacy.lesson04.exercise.service;

import com.synacy.lesson04.exercise.dao.BankAccountDao;
import com.synacy.lesson04.exercise.dao.TransactionDao;
import com.synacy.lesson04.exercise.domain.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
	public void deposit(BankAccount bankAccount, BigDecimal amount)
			throws InvalidBankAccountStatusException {
		if (bankAccount.getStatus() != BankAccountStatus.ACTIVE)
			throw new InvalidBankAccountStatusException(bankAccount, bankAccount.getStatus());

		Transaction transaction = new Transaction(bankAccount, TransactionType.DEBIT, amount, new Date());

		BigDecimal balance = bankAccount.getBalance();

		BigDecimal netBalance = balance.add(amount);

		bankAccount.setBalance(netBalance);
		transaction.setStatus(TransactionStatus.CLEARED);

		transactionDao.saveTransaction(transaction);
		bankAccountDao.saveBankAccount(bankAccount);
	}

	@Override
	public void transfer(BankAccount sourceBankAccount, BankAccount destinationBankAccount, BigDecimal amount)
			throws InvalidBankAccountStatusException, InsufficientBalanceException {

		if (sourceBankAccount.getStatus() != BankAccountStatus.ACTIVE)
			throw new InvalidBankAccountStatusException(sourceBankAccount, sourceBankAccount.getStatus());

		if (destinationBankAccount.getStatus() != BankAccountStatus.ACTIVE)
			throw new InvalidBankAccountStatusException(destinationBankAccount, destinationBankAccount.getStatus());

		BigDecimal sourceBalance = sourceBankAccount.getBalance();

		if (amount.compareTo(sourceBalance) > 0)
			throw new InsufficientBalanceException(sourceBankAccount, sourceBankAccount.getBalance(), amount);

		sourceBankAccount.setBalance(sourceBalance.subtract(amount));
		destinationBankAccount.setBalance(destinationBankAccount.getBalance().add(amount));

		Transaction sourceTransaction = new Transaction(sourceBankAccount, TransactionType.CREDIT,
				amount, new Date());

		Transaction destinationTransaction = new Transaction(sourceBankAccount, TransactionType.DEBIT,
				amount, new Date());

		transactionDao.saveTransaction(sourceTransaction);
		transactionDao.saveTransaction(destinationTransaction);
		bankAccountDao.saveBankAccount(sourceBankAccount);
		bankAccountDao.saveBankAccount(destinationBankAccount);
	}

	@Override
	public List<Transaction> fetchAllTransactions(BankAccount bankAccount) {
		return null;
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
