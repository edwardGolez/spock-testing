package com.synacy.lesson04.exercise.domain;

/**
 * Created by michael on 5/4/17.
 */
public class InvalidBankAccountStatusException extends Exception {
    private BankAccount bankAccount;
    private BankAccountStatus bankAccountStatus;

    public InvalidBankAccountStatusException(BankAccount bankAccount, BankAccountStatus bankAccountStatus) {
        this.bankAccount = bankAccount;
        this.bankAccountStatus = bankAccountStatus;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public BankAccountStatus getBankAccountStatus() {
        return bankAccountStatus;
    }

    public void setBankAccountStatus(BankAccountStatus bankAccountStatus) {
        this.bankAccountStatus = bankAccountStatus;
    }
}
