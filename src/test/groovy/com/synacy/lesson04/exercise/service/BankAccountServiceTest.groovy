package com.synacy.lesson04.exercise.service

import com.synacy.lesson04.exercise.dao.BankAccountDao
import com.synacy.lesson04.exercise.dao.TransactionDao
import com.synacy.lesson04.exercise.domain.BankAccount
import com.synacy.lesson04.exercise.domain.InsufficientBalanceException
import com.synacy.lesson04.exercise.domain.InsufficientDepositException
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
		def date = Mock(Date)

		when:
		bankAccountService.withdraw(bankAccount, amountWithdrawn)

		then:
		1 * transactionDao.saveTransaction(*_) >> { Transaction transaction ->
			assert bankAccount == transaction.bankAccount
			assert TransactionType.CREDIT == transaction.type
			assert amountWithdrawn == transaction.amount
			assert date == transaction.transactionDate
		}
	}

	def "deposit should increase given bank account's balance by the given amount"() {
		given:
		def bankAccount = Mock(BankAccount)
		bankAccount.getBalance() >> new BigDecimal(2258.25)

		when:
		bankAccountService.deposit(bankAccount, 700.00)

		then:
		1 * bankAccount.setBalance(2958.25)

		then:
		1 * bankAccountDao.saveBankAccount(bankAccount)

	}

	def "deposit should record the transaction made by the account"() {
		given:
		def bankAccount = Mock(BankAccount)
		bankAccount.getBalance() >> new BigDecimal(2258.25)
		def amountDeposited = 700.00
		def date = Mock(Date)

		when:
		bankAccountService.deposit(bankAccount, amountDeposited)

		then:
		1 * transactionDao.saveTransaction(*_) >> { Transaction transaction ->
			assert bankAccount == transaction.bankAccount
			assert TransactionType.DEBIT == transaction.type
			assert amountDeposited == transaction.amount
			assert date == transaction.transactionDate
			assert TransactionStatus.CLEARED == transaction.status
		}
	}
	def "deposit should throw an exception at deposits less than 200.00"() {
		given:
		def bankAccount = Mock(BankAccount)
		def amountDeposited = 199.99


		when:
		bankAccountService.deposit(bankAccount, amountDeposited)

		then:
		InsufficientDepositException exception = thrown()
		bankAccount == exception.bankAccount
		amountDeposited == exception.amountToDeposit
	}
	
	def "fetchAllTransactions should return all transactions from a given bank account by date by order of most recent"() {
		given:
		def bankAccount = Mock(BankAccount)

		def transaction1 = Mock(Transaction)
		def transaction2 = Mock(Transaction)
		def transaction3 = Mock(Transaction)

		def date1 = Mock(Date)
		def date2 = Mock(Date)
		def date3 = Mock(Date)

//		transaction1.transactionDate >> date1
//		transaction2.transactionDate >> date2
//		transaction3.transactionDate >> date3

		transaction1.transactionDate >> new SimpleDateFormat("MM-dd-yyyy").parse("05-31-2017")
		transaction2.transactionDate >> new SimpleDateFormat("MM-dd-yyyy").parse("05-20-2017")
		transaction3.transactionDate >> new SimpleDateFormat("MM-dd-yyyy").parse("05-04-2017")

		def expectedTransactions = [
		        transaction1, transaction2, transaction3
		]

		transactionDao.fetchAllTransactionsOfBankAccount(bankAccount) >> expectedTransactions

		expect:
		expectedTransactions.asList() == bankAccountService.fetchAllTransactions(bankAccount)

	}


}
