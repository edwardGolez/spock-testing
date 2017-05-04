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
import spock.lang.Specification

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

	def "deposit should throw exception if bank account status is not active" () {
		given:
		def bankAccount = Mock(BankAccount)
		BigDecimal amountToDeposit = 1000.00

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
		def bankAccount = Mock(BankAccount)

		BigDecimal currentBalance = 500
		BigDecimal amountToDeposit = 1000

		bankAccount.getBalance() >> currentBalance

		when:
		bankAccountService.deposit(bankAccount, amountToDeposit)

		then:

		1 * transactionDao.saveTransaction(*_) >> { Transaction transaction
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

		bankAccount.setOwner(accountOwner)
		bankAccount.setBalance(100)
		bankAccount.setStatus(bankAccountStatus)

		BigDecimal amountToDeposit = 1500

		when:
		bankAccountService.deposit(bankAccount, amountToDeposit)

		then:

		1 * bankAccountService.saveBankAccount(bankAccount) >> { BankAccount account
			assert bankAccount == account.getOwner()
			assert 1600 == account.getBalance()
			assert bankAccountStatus == account.getStatus()
		}
	}

}
