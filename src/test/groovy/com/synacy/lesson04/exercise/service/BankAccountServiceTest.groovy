package com.synacy.lesson04.exercise.service

import com.synacy.lesson04.exercise.dao.BankAccountDao
import com.synacy.lesson04.exercise.dao.TransactionDao
import com.synacy.lesson04.exercise.domain.BankAccount
import com.synacy.lesson04.exercise.domain.InsufficientBalanceException
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

	def "deposit should increment given bank account's balance with given amount then save"() {
		given:
			def bankAccount = Mock(BankAccount)
			def balance = new BigDecimal(2258.25)
			def amount = new BigDecimal(500.00)
			def netBalance = new BigDecimal(2758.25)

			bankAccount.getBalance() >> balance
		when:
			bankAccountService.deposit(bankAccount, amount)

		then:
			1 * bankAccount.setBalance(netBalance)

		then:
			1 * bankAccountDao.saveBankAccount(bankAccount)
	}

	def "deposit should record the transaction details of the bank account during deposit"() {
		given:
			def bankAccount = Mock(BankAccount)
			def balance = new BigDecimal(2258.25)
			def amount = new BigDecimal(500.00)
			bankAccount.getBalance() >> new BigDecimal(balance)

		when:
			bankAccountService.deposit(bankAccount, amount)

		then:
			1 * transactionDao.saveTransaction(*_) >> { Transaction transaction ->
				assert bankAccount == transaction.bankAccount
				assert TransactionType.DEBIT == transaction.type
				assert amount == transaction.amount
				assert null != transaction.transactionDate
			}
	}

	def "transfer should diminish given sourceBankAccount with the given amount and increment the given destinationBankAccount with the same amount"() {
		given:
			def sourceBankAccount = Mock(BankAccount)
			def destinationBankAccount = Mock(BankAccount)

			def sourceBankAccountBalance = new BigDecimal(2258.25)
			def destinationBankAccountBalance = new BigDecimal(1000.00)

			def amount = new BigDecimal(500.00)

			def sourceExpectedNetBalance = new BigDecimal(1758.25)
			def destinationExpectedNetBalance = new BigDecimal(1500.00)

			destinationBankAccount.getBalance() >> destinationBankAccountBalance
			sourceBankAccount.getBalance() >> sourceBankAccountBalance
		when:
			bankAccountService.transfer(sourceBankAccount, destinationBankAccount, amount)

		then:
			1 * sourceBankAccount.setBalance(sourceExpectedNetBalance)
			1 * destinationBankAccount.setBalance(destinationExpectedNetBalance)

		then:
			1 * bankAccountDao.saveBankAccount(sourceBankAccount)
			1 * bankAccountDao.saveBankAccount(destinationBankAccount)
	}

	def "transfer should record the transaction details of both sourceBankAccount and destinationBankAccount during the transfer"() {
		given:
			def sourceBankAccount = Mock(BankAccount)
			def destinationBankAccount = Mock(BankAccount)

			def sourceBankAccountBalance = new BigDecimal(2258.25)
			def destinationBankAccountBalance = new BigDecimal(1000.00)

			def amount = new BigDecimal(500.00)

			destinationBankAccount.getBalance() >> destinationBankAccountBalance
			sourceBankAccount.getBalance() >> sourceBankAccountBalance

		when:
			bankAccountService.transfer(sourceBankAccount, destinationBankAccount, amount)

		then:
			1 * transactionDao.saveTransaction(*_) >> { Transaction transaction ->
				assert sourceBankAccount == transaction.bankAccount
				assert TransactionType.CREDIT == transaction.type
				assert amount == transaction.amount
				assert null != transaction.transactionDate
			}

			1 * transactionDao.saveTransaction(*_) >> { Transaction transaction ->
				assert destinationBankAccount == transaction.bankAccount
				assert TransactionType.DEBIT == transaction.type
				assert amount == transaction.amount
				assert null != transaction.transactionDate
			}
	}

	def "transfer should throw an exception if sourceBankAccount's balance is less than given amount"() {
		given:
			def sourceBankAccount = Mock(BankAccount)
			def destinationBankAccount = Mock(BankAccount)

			def sourceBankAccountBalance = new BigDecimal(2258.25)
			def amount = new BigDecimal(5000.00)

			sourceBankAccount.getBalance() >> sourceBankAccountBalance

		when:
			bankAccountService.transfer(sourceBankAccount, destinationBankAccount, amount)

		then:
			InsufficientBalanceException exception = thrown()
			sourceBankAccount == exception.bankAccount
			sourceBankAccountBalance == exception.currentBalance
			amount == exception.amountToDiminish
	}

	def "fetchAllTransactions should return list of transactions of the given bank account"(){
		given:
			def bankAccount = Mock(BankAccount)

			def bankAccountsSets = new HashSet<BankAccount>()
			bankAccountsSets.add(Mock(BankAccount))
			bankAccountsSets.add(Mock(BankAccount))

		expect:
			bankAccountService.fetchAllTransactions(bankAccount) == bankAccountsSets


	}

}
