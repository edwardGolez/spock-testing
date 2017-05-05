package com.synacy.lesson04.exercise.service

import com.synacy.lesson04.exercise.dao.BankAccountDao
import com.synacy.lesson04.exercise.dao.TransactionDao
import com.synacy.lesson04.exercise.domain.AccountOwner
import com.synacy.lesson04.exercise.domain.BankAccount
import com.synacy.lesson04.exercise.domain.BankAccountStatus
import com.synacy.lesson04.exercise.domain.InsufficientBalanceException
import com.synacy.lesson04.exercise.domain.InvalidBankAccountStatusException
import com.synacy.lesson04.exercise.domain.Transaction
import com.synacy.lesson04.exercise.domain.TransactionType
import spock.lang.MockingApi
import spock.lang.Specification

import javax.jws.soap.SOAPBinding

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

	def "deposit should throw exception if bank account status is not active"() {
		given:
		def bankAccount = Mock(BankAccount)

		BigDecimal currentBalance = 500
		BigDecimal amountToDeposit = 1000

		bankAccount.getBalance() >> currentBalance
		bankAccount.getStatus() >> BankAccountStatus.INACTIVE

		when:
		bankAccountService.deposit(bankAccount, amountToDeposit)

		then:
		InvalidBankAccountStatusException exception = thrown()
		bankAccount == exception.getBankAccount()
		BankAccountStatus.INACTIVE == exception.getBankAccountStatus()
	}

	def "deposit should record the transaction of the account's current balance"() {
		given:
		def accountOwner = Mock(AccountOwner)
		def bankAccount = Mock(BankAccount)
		def bankAccountStatus = BankAccountStatus.ACTIVE

		BigDecimal currentBalance = 100
		BigDecimal amountToDeposit = 1500

		bankAccount.getOwner() >> accountOwner
		bankAccount.getBalance() >> currentBalance
		bankAccount.getStatus() >> bankAccountStatus

		when:
		bankAccountService.deposit(bankAccount, amountToDeposit)

		then:

		1 * transactionDao.saveTransaction(*_) >> { Transaction transaction ->
			assert bankAccount == transaction.bankAccount
			assert TransactionType.DEBIT == transaction.type
			assert amountToDeposit == transaction.amount
			assert null != transaction.transactionDate
		}
	}

	def "deposit should record the bank account's new balance"() {
		given:
		def accountOwner = Mock(AccountOwner)
		def bankAccount = Mock(BankAccount)
		def bankAccountStatus = BankAccountStatus.ACTIVE

		BigDecimal currentBalance = 100
		BigDecimal amountToDeposit = 1500

		bankAccount.getOwner() >> accountOwner
		bankAccount.getBalance() >> currentBalance
		bankAccount.getStatus() >> bankAccountStatus

		when:
		bankAccountService.deposit(bankAccount, amountToDeposit)

		then:
		bankAccount.getBalance() >> currentBalance + amountToDeposit

		1 * bankAccountDao.saveBankAccount(bankAccount) >> { BankAccount account ->
			assert accountOwner == account.getOwner()
			assert 1600 == account.getBalance()
			assert bankAccountStatus == account.getStatus()
		}
	}

	def "transfer should throw exception if source bank account is not active"() {
		given:
		def source = Mock(BankAccount)
		def destination = Mock(BankAccount)

		BigDecimal amount = 1000000;

		source.getStatus() >> BankAccountStatus.PENDING
		destination.getStatus() >> BankAccountStatus.ACTIVE

		when:
		bankAccountService.transfer(source, destination, amount)

		then:
		InvalidBankAccountStatusException exception = thrown()
		source == exception.getBankAccount()
		BankAccountStatus.PENDING == exception.getBankAccountStatus()
	}

	def "transfer should throw exception if destination bank account is not active"() {
		given:
		def source = Mock(BankAccount)
		def destination = Mock(BankAccount)

		BigDecimal amount = 1000000;

		source.getStatus() >> BankAccountStatus.ACTIVE
		destination.getStatus() >> BankAccountStatus.INACTIVE

		when:
		bankAccountService.transfer(source, destination, amount)

		then:
		InvalidBankAccountStatusException exception = thrown()
		destination == exception.getBankAccount()
		BankAccountStatus.INACTIVE == exception.getBankAccountStatus()
	}

	def "transfer should throw exception if source bank account balance is less than the given amount"() {
		given:
		def source = Mock(BankAccount)
		def destination = Mock(BankAccount)

		BigDecimal sourceBalance = 999999
		BigDecimal amount = 1000000

		source.getStatus() >> BankAccountStatus.ACTIVE
		destination.getStatus() >> BankAccountStatus.ACTIVE

		source.getBalance() >> sourceBalance

		when:
		bankAccountService.transfer(source, destination, amount)

		then:
		InsufficientBalanceException exception = thrown()
		source == exception.getBankAccount()
		sourceBalance == exception.getCurrentBalance()
		amount == exception.getAmountToDiminish()
	}

	def "transfer should record the accounts balance"() {
		given:
		def source = Mock(BankAccount)
		def destination = Mock(BankAccount)
		def sourceTransaction = Mock(Transaction)
		def destinationTransaction = Mock(Transaction)

		BigDecimal sourceBalance = 1000
		BigDecimal destinationBalance = 0

		BigDecimal amount = 100

		source.getStatus() >> BankAccountStatus.ACTIVE
		destination.getStatus() >> BankAccountStatus.ACTIVE

		source.getBalance() >> sourceBalance
		destination.getBalance() >> destinationBalance

		when:
		bankAccountService.transfer(source, destination, amount)

		then:
		2 * transactionDao.saveTransaction(*_)
	}

	def "transfer should record the accounts new balance"() {
		given:
		def source = Mock(BankAccount)
		def destination = Mock(BankAccount)
		def sourceTransaction = Mock(Transaction)
		def destinationTransaction = Mock(Transaction)

		BigDecimal sourceBalance = 1000
		BigDecimal destinationBalance = 0

		BigDecimal amount = 100

		source.getStatus() >> BankAccountStatus.ACTIVE
		destination.getStatus() >> BankAccountStatus.ACTIVE

		source.getBalance() >> sourceBalance
		destination.getBalance() >> destinationBalance

		when:
		bankAccountService.transfer(source, destination, amount)

		then:
		2 * bankAccountDao.saveBankAccount(*_)
	}

	def "fetchAllTransactions should return bankAccount's transactions sorted by latest date "() {
		given:
		def bankAccount = Mock(BankAccount)

		def transaction1 = Mock(Transaction)
		def transaction2 = Mock(Transaction)
		def transaction3 = Mock(Transaction)

		transaction1.getTransactionDate() >> new Date().parse("yyyy/MM/dd", "1973/07/09")
		transaction2.getTransactionDate() >> new Date().parse("yyyy/MM/dd", "1990/07/09")
		transaction3.getTransactionDate() >> new Date().parse("yyyy/MM/dd", "2015/07/09")


		Set transactions = [
		        transaction1, transaction2, transaction3
		]

		transactionDao.fetchAllTransactionsOfBankAccount(bankAccount) >> transactions

		when:
		bankAccountService.fetchAllTransactions(bankAccount)

		then:
		List sortedTransaction = bankAccountService.fetchAllTransactions(bankAccount)

		transaction3.equals(sortedTransaction.get(0))
		transaction2.equals(sortedTransaction.get(1))
		transaction1.equals(sortedTransaction.get(2))
	}
}
