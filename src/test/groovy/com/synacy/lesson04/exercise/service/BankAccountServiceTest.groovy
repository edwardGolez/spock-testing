package com.synacy.lesson04.exercise.service

import com.synacy.lesson04.exercise.dao.BankAccountDao
import com.synacy.lesson04.exercise.dao.TransactionDao
import com.synacy.lesson04.exercise.domain.BankAccount
import com.synacy.lesson04.exercise.domain.InsufficientBalanceException
import com.synacy.lesson04.exercise.domain.Transaction
import com.synacy.lesson04.exercise.domain.TransactionStatus
import com.synacy.lesson04.exercise.domain.TransactionType
import spock.lang.Specification
import spock.lang.Unroll

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

    @Unroll
    def "deposit should increase given bank account's balance with given amount"() {
        given:
        def bankAccount = Mock(BankAccount)
        bankAccount.getBalance() >> new BigDecimal(currentBalance)

        when:
        bankAccountService.deposit(bankAccount, amountToDeposit)

        then:
        1 * bankAccount.setBalance(netBalance)

        then:
        1 * bankAccountDao.saveBankAccount(bankAccount)

        where:
        currentBalance | amountToDeposit | netBalance
        3000.00        | 250.00          | 3250.00
        -500.00        | 600.00          | 100.00
        0              | 0               | 0
    }

	def "deposit should record the transaction to the account's balance and should set the TransactionStatus to CLEARED"() {
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
            assert TransactionStatus.CLEARED == transaction.status
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
        def amountToTransfer = 500.00

        sourceBankAccount.getBalance() >> new BigDecimal(currentBalanceOfSource)
        destinationBankAccount.getBalance() >> new BigDecimal(currentBalanceOfDestination)

        when:
        bankAccountService.transfer(sourceBankAccount, destinationBankAccount, amountToTransfer)

        then:
        1 * sourceBankAccount.setBalance(2500.00)
        1 * destinationBankAccount.setBalance(1750.00)

        then:
        1 * bankAccountDao.saveBankAccount(sourceBankAccount)
        1 * bankAccountDao.saveBankAccount(destinationBankAccount)

    }

    @Unroll
    def "transfer should record the transaction of the bank account's balances both from source and destination, and should set both the TransactionStatus to CLEARED"() {
        given:
        def sourceBankAccount = Mock(BankAccount)
        def destinationBankAccount = Mock(BankAccount)
        sourceBankAccount.getBalance() >> new BigDecimal(2258.25)
        destinationBankAccount.getBalance() >> new BigDecimal(2258.25)

        def amountToTransfer = 500.00

        when:
        bankAccountService.transfer(sourceBankAccount, destinationBankAccount, amountToTransfer)

        then:
        1 * transactionDao.saveTransaction(*_) >> { Transaction transaction ->
            assert sourceBankAccount == transaction.bankAccount
            assert TransactionType.CREDIT == transaction.type
            assert amountToTransfer == transaction.amount
            assert null != transaction.transactionDate
            assert TransactionStatus.CLEARED == transaction.status
        }
        1 * transactionDao.saveTransaction(*_) >> { Transaction transaction ->
            assert destinationBankAccount == transaction.bankAccount
            assert TransactionType.DEBIT == transaction.type
            assert amountToTransfer == transaction.amount
            assert null != transaction.transactionDate
            assert TransactionStatus.CLEARED == transaction.status
        }
    }

    def "fetchAllTransactions should fetch all transactions from a given bank account"() {
        given:
        BankAccount bankAccount = Mock()

        Transaction transaction1 = new Transaction(bankAccount, TransactionType.DEBIT, 5000.00, new Date())
        Transaction transaction2 = new Transaction(bankAccount, TransactionType.DEBIT, 6000.00, new Date())
        def expectedTransactions = [
                transaction1, transaction2
        ]
        transactionDao.fetchAllTransactionsOfBankAccount(bankAccount) >> expectedTransactions

        when:
        def actualTransactions = bankAccountService.fetchAllTransactions(bankAccount)

        then:
        actualTransactions.size() == expectedTransactions.size()
        actualTransactions.containsAll(expectedTransactions)
    }

    def "fetchAllTransactions should see to it that the fetched records are sorted by the most recent transaction"() {
        given:
        BankAccount bankAccount = Mock()

        Date date1 = new SimpleDateFormat("MM-dd-yyyy").parse("05-01-2017")
        Date date2 = new SimpleDateFormat("MM-dd-yyyy").parse("05-03-2017")
        Date date3 = new SimpleDateFormat("MM-dd-yyyy").parse("05-06-2017")

        Transaction transaction1 = new Transaction(bankAccount, TransactionType.DEBIT,  5000.00, date3)
        Transaction transaction2 = new Transaction(bankAccount, TransactionType.CREDIT, 6000.00, date2)
        Transaction transaction3 = new Transaction(bankAccount, TransactionType.DEBIT,  200.00, date1)
        def expectedTransactions = [
                transaction1, transaction2, transaction3
        ]
        def unOrderedTransactions = expectedTransactions.collect()
        Collections.shuffle(unOrderedTransactions)
        transactionDao.fetchAllTransactionsOfBankAccount(bankAccount) >> unOrderedTransactions

        expect:
        expectedTransactions.asList() == bankAccountService.fetchAllTransactions(bankAccount)
    }

}
