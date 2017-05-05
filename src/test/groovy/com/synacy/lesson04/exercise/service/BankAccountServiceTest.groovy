package com.synacy.lesson04.exercise.service

import com.synacy.lesson04.exercise.dao.BankAccountDao
import com.synacy.lesson04.exercise.dao.TransactionDao
import com.synacy.lesson04.exercise.domain.BankAccount
import com.synacy.lesson04.exercise.domain.InsufficientBalanceException
import com.synacy.lesson04.exercise.domain.Transaction
import com.synacy.lesson04.exercise.domain.TransactionStatus
import com.synacy.lesson04.exercise.domain.TransactionType
import spock.lang.Specification

import java.text.SimpleDateFormat

class BankAccountServiceTest extends Specification {

	BankAccountService bankAccountService

	BankAccountDao bankAccountDao
	TransactionDao transactionDao

	def setup() {
		bankAccountService = new BankAccountServiceImpl()

		bankAccountDao = Mock()
		transactionDao = Mock()

		bankAccountService.bankAccountDao = bankAccountDao
		bankAccountService.transactionDao = transactionDao
	}

	def "withdraw should throw an exception if bank account's balance is less than given amount"() {
		given:
		def bankAccount = Mock(BankAccount)
		def currentBalance = 2258.25
		bankAccount.getBalance() >> new BigDecimal(currentBalance)

		def withdrawnAmount = 5000.00

		when:
		bankAccountService.withdraw(bankAccount, withdrawnAmount)

		then:
		InsufficientBalanceException exception = thrown()
		bankAccount == exception.bankAccount
		currentBalance == exception.currentBalance
		withdrawnAmount == exception.amountToDiminish
	}

	def "withdraw should diminish given bank account's balance with given amount"() {
		given:
		def bankAccount = Mock(BankAccount)
		bankAccount.getBalance() >> new BigDecimal(2258.25)

		when:
		bankAccountService.withdraw(bankAccount, 500.00)

		then:
		1 * bankAccount.setBalance(1758.25)

		then:
		1 * bankAccountDao.saveBankAccount(bankAccount)
	}

	def "withdraw should record the transaction of the account's balance"() {
		given:
		def bankAccount = Mock(BankAccount)
		bankAccount.getBalance() >> new BigDecimal(2258.25)

		def amountWithdrawn = 500.00

		when:
		bankAccountService.withdraw(bankAccount, amountWithdrawn)

		then:
		1 * transactionDao.saveTransaction(*_) >> { Transaction transaction ->
			assert bankAccount == transaction.bankAccount
			assert TransactionType.CREDIT == transaction.type
			assert amountWithdrawn == transaction.amount
			assert null != transaction.transactionDate
		}
	}

	def "deposit should deposit the amount into the BankAccount"() {
		given:
		BankAccount bankAccount = Mock(BankAccount)
		bankAccount.balance >> new BigDecimal(3000.00)

		def amount = 500.00

		when: bankAccountService.deposit(bankAccount, amount)

		then:
		1 * bankAccount.setBalance(3500.00)

		then:
		1 * bankAccountDao.saveBankAccount(bankAccount)
	}

	def "deposit should record the transaction of the account's balance"() {
		given:
		BankAccount bankAccount = Mock(BankAccount)
		bankAccount.balance >> new BigDecimal(3000.00)

		def amount = 500.00

		when: bankAccountService.deposit(bankAccount, amount)

		then:
		1 * transactionDao.saveTransaction(*_) >> { Transaction transaction ->
			assert bankAccount == transaction.bankAccount
			assert TransactionType.DEBIT == transaction.type
			assert amount == transaction.amount
			assert null != transaction.transactionDate
		}
	}

	def "transfer should transfer given amount from a bank account to another bank account"() {
		given:
		BankAccount sourceBankAccount = Mock(BankAccount)
		sourceBankAccount.getBalance() >> new BigDecimal(8000)

		BankAccount destinationAccount = Mock(BankAccount)
		destinationAccount.getBalance() >> new BigDecimal(1000)

		def givenAmount = 3000

		when: bankAccountService.transfer(sourceBankAccount, destinationAccount, givenAmount)

		then:
		1 * sourceBankAccount.setBalance(5000)
		1 * destinationAccount.setBalance(4000)

		then:
		1 * bankAccountDao.saveBankAccount(sourceBankAccount)
		1 * bankAccountDao.saveBankAccount(destinationAccount)
	}

	def "transfer should record transaction of both account when transferring the given amount"() {
		given:
		BankAccount sourceBankAccount = Mock(BankAccount)
		sourceBankAccount.getBalance() >> new BigDecimal(8000)

		BankAccount destinationAccount = Mock(BankAccount)
		destinationAccount.getBalance() >> new BigDecimal(1000)

		def givenAmount = 3000

		when: bankAccountService.transfer(sourceBankAccount, destinationAccount, givenAmount)

		then:
		1 * transactionDao.saveTransaction(*_) >> { Transaction transaction ->
			assert sourceBankAccount == transaction.bankAccount
			assert TransactionType.CREDIT == transaction.type
			assert givenAmount == transaction.amount
			assert null != transaction.transactionDate
		}

		1 * transactionDao.saveTransaction(*_) >> { Transaction transaction ->
			assert destinationAccount == transaction.bankAccount
			assert TransactionType.DEBIT == transaction.type
			assert givenAmount == transaction.amount
			assert null != transaction.transactionDate
		}
	}

	def "transfer should throw an exception when source bank account balance is lesser than the given amount to transfer"() {
		given:
		BankAccount sourceBankAccount = Mock(BankAccount)
		def currentBalance = 2000
		sourceBankAccount.getBalance() >> currentBalance

		BankAccount destinationAccount = Mock(BankAccount)
		destinationAccount.getBalance() >> new BigDecimal(1000)

		def givenAmount = 3000

		when: bankAccountService.transfer(sourceBankAccount, destinationAccount, givenAmount)

		then:
		InsufficientBalanceException exception = thrown()
		sourceBankAccount == exception.bankAccount
		currentBalance == exception.currentBalance
		givenAmount == exception.amountToDiminish
	}

	def "fetchAllTransactions should fetch all transactions sorting from most recent"() {
		given:
		BankAccount bankAccount = Mock(BankAccount)

		SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss")
		String dateInString = "01-01-2016 10:20:56"
		Date date = sdf.parse(dateInString)

		Transaction transaction1 = Mock(Transaction)
		transaction1.bankAccount >> bankAccount
		transaction1.transactionDate >> date

		dateInString = "01-01-2017 10:20:56"
		date = sdf.parse(dateInString)

		Transaction transaction2 = Mock(Transaction)
		transaction2.bankAccount >> bankAccount
		transaction2.transactionDate >> date

		dateInString = "01-01-2014 10:20:56"
		date = sdf.parse(dateInString)

		Transaction transaction3 = Mock(Transaction)
		transaction3.bankAccount >> bankAccount
		transaction3.transactionDate >> date

		dateInString = "01-01-2015 10:20:56"
		date = sdf.parse(dateInString)

		Transaction transaction4 = Mock(Transaction)
		transaction4.bankAccount >> bankAccount
		transaction4.transactionDate >> date

		Set allTransactions = [
			transaction3, transaction1, transaction4, transaction2
		]

		List expectedResult = [
				transaction2, transaction1, transaction4, transaction3
		]

		transactionDao.fetchAllTransactionsOfBankAccount(bankAccount) >> allTransactions

		expect:
		expectedResult ==  bankAccountService.fetchAllTransactions(bankAccount)
	}

}
