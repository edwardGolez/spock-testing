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
		BigDecimal balance = bankAccount.getBalance();
		BigDecimal netBalance = balance.add(amount);

		Transaction transaction = new Transaction(
				bankAccount, TransactionType.DEBIT, amount, new Date());
		bankAccount.setBalance(netBalance);
		transaction.setStatus(TransactionStatus.CLEARED);

		transactionDao.saveTransaction(transaction);
		bankAccountDao.saveBankAccount(bankAccount);
	}

	@Override
	public void transfer(BankAccount sourceBankAccount, BankAccount destinationBankAccount, BigDecimal amount)
			throws InsufficientBalanceException {
        BigDecimal currentBalanceOfSource = sourceBankAccount.getBalance();
        BigDecimal currentBalanceOfDestination = destinationBankAccount.getBalance();
        if(amount.compareTo(currentBalanceOfSource) > 0){
            throw new InsufficientBalanceException(sourceBankAccount, currentBalanceOfSource, amount);
        }

        BigDecimal netBalanceOfSource = currentBalanceOfSource.subtract(amount);
        BigDecimal netBalanceOfDestination = currentBalanceOfDestination.add(amount);
        sourceBankAccount.setBalance(netBalanceOfSource);
        destinationBankAccount.setBalance(netBalanceOfDestination);

        Transaction transactionOfSource = new Transaction(
                sourceBankAccount, TransactionType.CREDIT, amount, new Date());
        transactionOfSource.setStatus(TransactionStatus.CLEARED);
        transactionDao.saveTransaction(transactionOfSource);
        bankAccountDao.saveBankAccount(sourceBankAccount);
        Transaction transactionOfDestination = new Transaction(
                destinationBankAccount, TransactionType.DEBIT, amount, new Date());
        transactionOfDestination.setStatus(TransactionStatus.CLEARED);
        transactionDao.saveTransaction(transactionOfDestination);
        bankAccountDao.saveBankAccount(destinationBankAccount);
	}

	@Override
	public List<Transaction> fetchAllTransactions(BankAccount bankAccount) {
		List<Transaction> resultingTransactions = new ArrayList<>();
        Set<Transaction> transactions = transactionDao.fetchAllTransactionsOfBankAccount(bankAccount);

        resultingTransactions.addAll(transactions);
        resultingTransactions.sort(
                Comparator.comparing(Transaction::getTransactionDate)
						  .reversed()
		);
	    return resultingTransactions;
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
