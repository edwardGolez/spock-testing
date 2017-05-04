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

    def "deposit should increase given bank account's balance with given amount"() {
        given:
        def bankAccount = Mock(BankAccount)
        bankAccount.getBalance() >> new BigDecimal(3000.00)

        when:
        bankAccountService.deposit(bankAccount, 250.00)

        then:
        1 * bankAccount.setBalance(3250.00)

        then:
        1 * bankAccountDao.saveBankAccount(bankAccount)
    }

	def "deposit should record the transaction to the account's balance"() {
		given:
		def bankAccount = Mock(BankAccount)
		bankAccount.getBalance() >> new BigDecimal(2500.00)

		def amountDeposited = 500.00

		when:
		bankAccountService.deposit(bankAccount, amountDeposited)

		then:
		1 * transactionDao.saveTransaction(*_) >> { Transaction transaction ->
			assert bankAccount == transaction.bankAccount
			assert TransactionType.DEBIT == transaction.type
			assert amountDeposited == transaction.amount
			assert null != transaction.transactionDate
		}
	}

    def "transfer should throw an exception if bank account's (source) balance is less than given amount"() {
        given:
        BankAccount sourceBankAccount = Mock()
        BankAccount destinationBankAccount = Mock()
        def currentBalance = 3000.00
        sourceBankAccount.getBalance() >> new BigDecimal(currentBalance)

        def amountToTransfer = 3001.00

        when:
        bankAccountService.transfer(sourceBankAccount, destinationBankAccount, amountToTransfer)

        then:
        InsufficientBalanceException exception = thrown()
        sourceBankAccount == exception.bankAccount
        currentBalance == exception.currentBalance
        amountToTransfer == exception.amountToDiminish
    }

    def "transfer should transfer a given amount from source bank account to destination bank account"() {
        given:
        BankAccount sourceBankAccount = Mock()
        BankAccount destinationBankAccount = Mock()
        def currentBalanceOfSource = 3000.00
        def currentBalanceOfDestination = 1250.00
        sourceBankAccount.getBalance() >> new BigDecimal(currentBalanceOfSource)
        destinationBankAccount.getBalance() >> new BigDecimal(currentBalanceOfDestination)

        def amountToTransfer = 500.00
//        def balanceOfSourceAfterTransfer = 2500.00
//        def balanceOfDestinationAfterTransfer = 1750.00
//        bankAccountService.withdraw(sourceBankAccount, amountToTransfer) >> balanceOfSourceAfterTransfer
//        bankAccountService.deposit(destinationBankAccount, amountToTransfer) >> balanceOfDestinationAfterTransfer

//        currentBalanceOfSource.subtract(amountToTransfer) >> new BigDecimal(balanceOfSourceAfterTransfer)
//        currentBalanceOfDestination.add(amountToTransfer) >> new BigDecimal(balanceOfDestinationAfterTransfer)
//        sourceBankAccount.setBalance(balanceOfSourceAfterTransfer)
//        destinationBankAccount.setBalance(balanceOfDestinationAfterTransfer)

        when:
        bankAccountService.transfer(sourceBankAccount, destinationBankAccount, amountToTransfer)

        then:
        1 * sourceBankAccount.setBalance(2500.00)
        1 * destinationBankAccount.setBalance(1750.00)

        then:
        1 * bankAccountDao.saveBankAccount(sourceBankAccount)
        1 * bankAccountDao.saveBankAccount(destinationBankAccount)
//        balanceOfSourceAfterTransfer == sourceBankAccount.balance
//        balanceOfDestinationAfterTransfer == destinationBankAccount.balance
    }


}
